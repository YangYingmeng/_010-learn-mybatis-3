/*
 *    Copyright 2009-2023 the original author or authors.
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *       https://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */
package org.apache.ibatis.datasource.pooled;

import java.io.PrintWriter;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.logging.Logger;

import javax.sql.DataSource;

import org.apache.ibatis.datasource.unpooled.UnpooledDataSource;
import org.apache.ibatis.logging.Log;
import org.apache.ibatis.logging.LogFactory;

/**
 * This is a simple, synchronous, thread-safe database connection pool.
 *
 * @author Clinton Begin
 */
public class PooledDataSource implements DataSource {

  private static final Log log = LogFactory.getLog(PooledDataSource.class);

  /**
   * 记录池化状态
   */
  private final PoolState state = new PoolState(this);

  /**
   * UnpooledDataSource 对象
   */
  private final UnpooledDataSource dataSource;

  // OPTIONAL CONFIGURATION FIELDS
  /**
   * 在任意时间可以存在的活动（也就是正在使用）连接数量
   */
  protected int poolMaximumActiveConnections = 10;
  /**
   * 任意时间可能存在的空闲连接数
   */
  protected int poolMaximumIdleConnections = 5;
  /**
   * 在强制返回之前, 池中连接被检出时间
   */
  protected int poolMaximumCheckoutTime = 20000;
  /**
   * 获取连接时间过长, 会进行状态日志打印, 并尝试重新获取一个连接
   */
  protected int poolTimeToWait = 20000;
  /**
   * 环连接容忍度, 尝试从缓存池获取连接的线程, 如果线程获取到的是一个环的连接, 那么该数据源允许该线程重新获取一个连接
   * 但是尝试次数不能超过: poolMaximumLocalBadConnectionTolerance + poolMaximumIdleConnections
   */
  protected int poolMaximumLocalBadConnectionTolerance = 3;
  /**
   * 发送到数据的侦测查询, 用来检查连接是否正常工作, 并准备接受请求
   */
  protected String poolPingQuery = "NO PING QUERY SET";
  /**
   * 是否启用侦测查询, 需要poolPingQuery为一个可执行的sql语句
   */
  protected boolean poolPingEnabled;
  /**
   * 配置 poolPingQuery 的频率, 可以被设置为和数据库连接超时时间一样, 来避免不必要的侦测
   */
  protected int poolPingConnectionsNotUsedFor;

  /**
   * 期望Connection的类型编码
   * 通过 {@link #assembleConnectionTypeCode(String, String, String)} 计算
   */
  private int expectedConnectionTypeCode;

  private final Lock lock = new ReentrantLock();
  private final Condition condition = lock.newCondition();

  public PooledDataSource() {
    dataSource = new UnpooledDataSource();
  }

  public PooledDataSource(UnpooledDataSource dataSource) {
    this.dataSource = dataSource;
  }

  public PooledDataSource(String driver, String url, String username, String password) {
    dataSource = new UnpooledDataSource(driver, url, username, password);
    expectedConnectionTypeCode = assembleConnectionTypeCode(dataSource.getUrl(), dataSource.getUsername(),
        dataSource.getPassword());
  }

  public PooledDataSource(String driver, String url, Properties driverProperties) {
    dataSource = new UnpooledDataSource(driver, url, driverProperties);
    expectedConnectionTypeCode = assembleConnectionTypeCode(dataSource.getUrl(), dataSource.getUsername(),
        dataSource.getPassword());
  }

  public PooledDataSource(ClassLoader driverClassLoader, String driver, String url, String username, String password) {
    dataSource = new UnpooledDataSource(driverClassLoader, driver, url, username, password);
    expectedConnectionTypeCode = assembleConnectionTypeCode(dataSource.getUrl(), dataSource.getUsername(),
        dataSource.getPassword());
  }

  public PooledDataSource(ClassLoader driverClassLoader, String driver, String url, Properties driverProperties) {
    dataSource = new UnpooledDataSource(driverClassLoader, driver, url, driverProperties);
    expectedConnectionTypeCode = assembleConnectionTypeCode(dataSource.getUrl(), dataSource.getUsername(),
        dataSource.getPassword());
  }

  @Override
  public Connection getConnection() throws SQLException {
    // 获取一个池化连接对象
    return popConnection(dataSource.getUsername(), dataSource.getPassword()).getProxyConnection();
  }

  @Override
  public Connection getConnection(String username, String password) throws SQLException {
    // 获取一个池化连接对象
    return popConnection(username, password).getProxyConnection();
  }

  @Override
  public void setLoginTimeout(int loginTimeout) {
    DriverManager.setLoginTimeout(loginTimeout);
  }

  @Override
  public int getLoginTimeout() {
    return DriverManager.getLoginTimeout();
  }

  @Override
  public void setLogWriter(PrintWriter logWriter) {
    DriverManager.setLogWriter(logWriter);
  }

  @Override
  public PrintWriter getLogWriter() {
    return DriverManager.getLogWriter();
  }

  public void setDriver(String driver) {
    dataSource.setDriver(driver);
    forceCloseAll();
  }

  public void setUrl(String url) {
    dataSource.setUrl(url);
    forceCloseAll();
  }

  public void setUsername(String username) {
    dataSource.setUsername(username);
    forceCloseAll();
  }

  public void setPassword(String password) {
    dataSource.setPassword(password);
    forceCloseAll();
  }

  public void setDefaultAutoCommit(boolean defaultAutoCommit) {
    dataSource.setAutoCommit(defaultAutoCommit);
    forceCloseAll();
  }

  public void setDefaultTransactionIsolationLevel(Integer defaultTransactionIsolationLevel) {
    dataSource.setDefaultTransactionIsolationLevel(defaultTransactionIsolationLevel);
    forceCloseAll();
  }

  public void setDriverProperties(Properties driverProps) {
    dataSource.setDriverProperties(driverProps);
    forceCloseAll();
  }

  /**
   * Sets the default network timeout value to wait for the database operation to complete. See
   * {@link Connection#setNetworkTimeout(java.util.concurrent.Executor, int)}
   *
   * @param milliseconds
   *          The time in milliseconds to wait for the database operation to complete.
   *
   * @since 3.5.2
   */
  public void setDefaultNetworkTimeout(Integer milliseconds) {
    dataSource.setDefaultNetworkTimeout(milliseconds);
    forceCloseAll();
  }

  /**
   * The maximum number of active connections.
   *
   * @param poolMaximumActiveConnections
   *          The maximum number of active connections
   */
  public void setPoolMaximumActiveConnections(int poolMaximumActiveConnections) {
    this.poolMaximumActiveConnections = poolMaximumActiveConnections;
    forceCloseAll();
  }

  /**
   * The maximum number of idle connections.
   *
   * @param poolMaximumIdleConnections
   *          The maximum number of idle connections
   */
  public void setPoolMaximumIdleConnections(int poolMaximumIdleConnections) {
    this.poolMaximumIdleConnections = poolMaximumIdleConnections;
    forceCloseAll();
  }

  /**
   * The maximum number of tolerance for bad connection happens in one thread which are applying for new
   * {@link PooledConnection}.
   *
   * @param poolMaximumLocalBadConnectionTolerance
   *          max tolerance for bad connection happens in one thread
   *
   * @since 3.4.5
   */
  public void setPoolMaximumLocalBadConnectionTolerance(int poolMaximumLocalBadConnectionTolerance) {
    this.poolMaximumLocalBadConnectionTolerance = poolMaximumLocalBadConnectionTolerance;
  }

  /**
   * The maximum time a connection can be used before it *may* be given away again.
   *
   * @param poolMaximumCheckoutTime
   *          The maximum time
   */
  public void setPoolMaximumCheckoutTime(int poolMaximumCheckoutTime) {
    this.poolMaximumCheckoutTime = poolMaximumCheckoutTime;
    forceCloseAll();
  }

  /**
   * The time to wait before retrying to get a connection.
   *
   * @param poolTimeToWait
   *          The time to wait
   */
  public void setPoolTimeToWait(int poolTimeToWait) {
    this.poolTimeToWait = poolTimeToWait;
    forceCloseAll();
  }

  /**
   * The query to be used to check a connection.
   *
   * @param poolPingQuery
   *          The query
   */
  public void setPoolPingQuery(String poolPingQuery) {
    this.poolPingQuery = poolPingQuery;
    forceCloseAll();
  }

  /**
   * Determines if the ping query should be used.
   *
   * @param poolPingEnabled
   *          True if we need to check a connection before using it
   */
  public void setPoolPingEnabled(boolean poolPingEnabled) {
    this.poolPingEnabled = poolPingEnabled;
    forceCloseAll();
  }

  /**
   * If a connection has not been used in this many milliseconds, ping the database to make sure the connection is still
   * good.
   *
   * @param milliseconds
   *          the number of milliseconds of inactivity that will trigger a ping
   */
  public void setPoolPingConnectionsNotUsedFor(int milliseconds) {
    this.poolPingConnectionsNotUsedFor = milliseconds;
    forceCloseAll();
  }

  public String getDriver() {
    return dataSource.getDriver();
  }

  public String getUrl() {
    return dataSource.getUrl();
  }

  public String getUsername() {
    return dataSource.getUsername();
  }

  public String getPassword() {
    return dataSource.getPassword();
  }

  public boolean isAutoCommit() {
    return dataSource.isAutoCommit();
  }

  public Integer getDefaultTransactionIsolationLevel() {
    return dataSource.getDefaultTransactionIsolationLevel();
  }

  public Properties getDriverProperties() {
    return dataSource.getDriverProperties();
  }

  /**
   * Gets the default network timeout.
   *
   * @return the default network timeout
   *
   * @since 3.5.2
   */
  public Integer getDefaultNetworkTimeout() {
    return dataSource.getDefaultNetworkTimeout();
  }

  public int getPoolMaximumActiveConnections() {
    return poolMaximumActiveConnections;
  }

  public int getPoolMaximumIdleConnections() {
    return poolMaximumIdleConnections;
  }

  public int getPoolMaximumLocalBadConnectionTolerance() {
    return poolMaximumLocalBadConnectionTolerance;
  }

  public int getPoolMaximumCheckoutTime() {
    return poolMaximumCheckoutTime;
  }

  public int getPoolTimeToWait() {
    return poolTimeToWait;
  }

  public String getPoolPingQuery() {
    return poolPingQuery;
  }

  public boolean isPoolPingEnabled() {
    return poolPingEnabled;
  }

  public int getPoolPingConnectionsNotUsedFor() {
    return poolPingConnectionsNotUsedFor;
  }

  /**
   * Closes all active and idle connections in the pool.
   */
  public void forceCloseAll() {
    lock.lock();
    try {
      // 计算 expectedConnectionTypeCode
      expectedConnectionTypeCode = assembleConnectionTypeCode(dataSource.getUrl(), dataSource.getUsername(),
          dataSource.getPassword());
      // 遍历 activeConnections ，进行关闭
      for (int i = state.activeConnections.size(); i > 0; i--) {
        try {
          // 设置为失效
          PooledConnection conn = state.activeConnections.remove(i - 1);
          conn.invalidate();

          // 回滚事务，如果有事务未提交或回滚
          Connection realConn = conn.getRealConnection();
          if (!realConn.getAutoCommit()) {
            realConn.rollback();
          }
          // 关闭真实的连接
          realConn.close();
        } catch (Exception e) {
          // ignore
        }
      }
      // 遍历 idleConnections ，进行关闭
      for (int i = state.idleConnections.size(); i > 0; i--) {
        try {
          PooledConnection conn = state.idleConnections.remove(i - 1);
          conn.invalidate();

          Connection realConn = conn.getRealConnection();
          if (!realConn.getAutoCommit()) {
            realConn.rollback();
          }
          realConn.close();
        } catch (Exception e) {
          // ignore
        }
      }
    } finally {
      lock.unlock();
    }
    if (log.isDebugEnabled()) {
      log.debug("PooledDataSource forcefully closed/removed all connections.");
    }
  }

  public PoolState getPoolState() {
    return state;
  }

  private int assembleConnectionTypeCode(String url, String username, String password) {
    return ("" + url + username + password).hashCode();
  }

  protected void pushConnection(PooledConnection conn) throws SQLException {

    lock.lock();
    try {
      // 从激活的连接池中移走该连接
      state.activeConnections.remove(conn);
      // 通过ping来测试连接是否有效
      if (conn.isValid()) {
        // 判断是否超过空闲连接上限，并且和当前连接池的标识匹配
        if (state.idleConnections.size() < poolMaximumIdleConnections
            && conn.getConnectionTypeCode() == expectedConnectionTypeCode) {
          // 统计连接使用时长
          state.accumulatedCheckoutTime += conn.getCheckoutTime();
          // 非自动提交时 回滚事务, 防止回收的连接不干净
          if (!conn.getRealConnection().getAutoCommit()) {
            conn.getRealConnection().rollback();
          }
          // 创建 PooledConnection 对象, 添加到空闲的连接集合中
          PooledConnection newConn = new PooledConnection(conn.getRealConnection(), this);
          state.idleConnections.add(newConn);
          newConn.setCreatedTimestamp(conn.getCreatedTimestamp());
          newConn.setLastUsedTimestamp(conn.getLastUsedTimestamp());
          // 设置原连接失效, 防止原连接还在被使用, 设置为失效再次调用时会报异常
          conn.invalidate();
          if (log.isDebugEnabled()) {
            log.debug("Returned connection " + newConn.getRealHashCode() + " to pool.");
          }
          // 唤醒正在等待的空闲连接的线程
          condition.signal();
        } else {
          // 统计连接使用时长
          state.accumulatedCheckoutTime += conn.getCheckoutTime();
          // 非自动提交时 回滚事务, 防止回收的连接不干净
          if (!conn.getRealConnection().getAutoCommit()) {
            conn.getRealConnection().rollback();
          }
          // 关闭真正的数据库连接
          conn.getRealConnection().close();
          if (log.isDebugEnabled()) {
            log.debug("Closed connection " + conn.getRealHashCode() + ".");
          }
          // 原连接失效
          conn.invalidate();
        }
      } else {
        if (log.isDebugEnabled()) {
          log.debug("A bad connection (" + conn.getRealHashCode()
              + ") attempted to return to the pool, discarding connection.");
        }
        // 统计获取到坏连接的次数
        state.badConnectionCount++;
      }
    } finally {
      lock.unlock();
    }
  }

  /**
   * 获取 PooledConnection 对象
   */
  private PooledConnection popConnection(String username, String password) throws SQLException {
    // 标记 获取连接时, 是否进行了等待
    boolean countedWait = false;
    // 最终获取到的连接对象
    PooledConnection conn = null;
    // 记录当前时间
    long t = System.currentTimeMillis();
    // 记录当前方法，获取到坏连接的次数
    int localBadConnectionCount = 0;

    // 循环, 获取可用的 Connection 连接
    while (conn == null) {
      lock.lock();
      try {
        // 空闲连接非空
        if (!state.idleConnections.isEmpty()) {
          // Pool has available connection
          // 通过移除的方式，获得首个空闲的连接
          conn = state.idleConnections.remove(0);
          if (log.isDebugEnabled()) {
            log.debug("Checked out connection " + conn.getRealHashCode() + " from pool.");
          }
          // 已活跃连接数 < 最大活跃连接数
        } else if (state.activeConnections.size() < poolMaximumActiveConnections) {
          // Pool does not have available connection and can create a new connection
          // 创建新的连接对象
          conn = new PooledConnection(dataSource.getConnection(), this);
          if (log.isDebugEnabled()) {
            log.debug("Created connection " + conn.getRealHashCode() + ".");
          }
        } else {
          // Cannot create new connection
          // 获得首个激活的 PooledConnection 对象
          PooledConnection oldestActiveConnection = state.activeConnections.get(0);
          long longestCheckoutTime = oldestActiveConnection.getCheckoutTime();
          // 检查超时
          if (longestCheckoutTime > poolMaximumCheckoutTime) {
            // Can claim overdue connection
            // 对连接超时的时间统计
            state.claimedOverdueConnectionCount++;
            state.accumulatedCheckoutTimeOfOverdueConnections += longestCheckoutTime;
            state.accumulatedCheckoutTime += longestCheckoutTime;
            // 从活跃的连接池中移除连接
            state.activeConnections.remove(oldestActiveConnection);
            // 如果非自动提交, 需要回滚
            if (!oldestActiveConnection.getRealConnection().getAutoCommit()) {
              try {
                oldestActiveConnection.getRealConnection().rollback();
              } catch (SQLException e) {
                /*
                 * Just log a message for debug and continue to execute the following statement like nothing happened.
                 * Wrap the bad connection with a new PooledConnection, this will help to not interrupt current
                 * executing thread and give current thread a chance to join the next competition for another valid/good
                 * database connection. At the end of this loop, bad {@link @conn} will be set as null.
                 */
                log.debug("Bad connection. Could not roll back");
              }
            }
            // 创建新的 PooledConnection 连接对象
            conn = new PooledConnection(oldestActiveConnection.getRealConnection(), this);
            conn.setCreatedTimestamp(oldestActiveConnection.getCreatedTimestamp());
            conn.setLastUsedTimestamp(oldestActiveConnection.getLastUsedTimestamp());
            // 设置 oldestActiveConnection 无效
            oldestActiveConnection.invalidate();
            if (log.isDebugEnabled()) {
              log.debug("Claimed overdue connection " + conn.getRealHashCode() + ".");
            }
          } else {
            // 检查到未超时
            // Must wait
            try {
              // 对等待连接进行统计
              if (!countedWait) {
                state.hadToWaitCount++;
                countedWait = true;
              }
              if (log.isDebugEnabled()) {
                log.debug("Waiting as long as " + poolTimeToWait + " milliseconds for connection.");
              }
              // 记录当前时间
              long wt = System.currentTimeMillis();
              // 等待, 直到超时, 或 pingConnection 方法中归还连接时的唤醒
              if (!condition.await(poolTimeToWait, TimeUnit.MILLISECONDS)) {
                log.debug("Wait failed...");
              }
              // 统计等待连接的时间
              state.accumulatedWaitTime += System.currentTimeMillis() - wt;
            } catch (InterruptedException e) {
              // set interrupt flag
              Thread.currentThread().interrupt();
              break;
            }
          }
        }
        // 获取到连接
        if (conn != null) {
          // ping to server and check the connection is valid or not
          // 通过 ping 测试连接书否有效
          if (conn.isValid()) {
            // 如果非自动提交(事务没有结束, 会污染连接回收)的，需要进行回滚。即将原有执行中的事务，全部回滚。
            if (!conn.getRealConnection().getAutoCommit()) {
              conn.getRealConnection().rollback();
            }
            // 设置获取连接的属性
            conn.setConnectionTypeCode(assembleConnectionTypeCode(dataSource.getUrl(), username, password));
            conn.setCheckoutTimestamp(System.currentTimeMillis());
            conn.setLastUsedTimestamp(System.currentTimeMillis());
            // 添加到活跃的连接集合
            state.activeConnections.add(conn);
            // 对获取成功连接的统计
            state.requestCount++;
            state.accumulatedRequestTime += System.currentTimeMillis() - t;
          } else {
            if (log.isDebugEnabled()) {
              log.debug("A bad connection (" + conn.getRealHashCode()
                  + ") was returned from the pool, getting another connection.");
            }
            // 统计获取到坏的连接的次数
            state.badConnectionCount++;
            localBadConnectionCount++;
            conn = null;
            // 如果超过最大次数 抛出异常
            if (localBadConnectionCount > poolMaximumIdleConnections + poolMaximumLocalBadConnectionTolerance) {
              if (log.isDebugEnabled()) {
                log.debug("PooledDataSource: Could not get a good connection to the database.");
              }
              throw new SQLException("PooledDataSource: Could not get a good connection to the database.");
            }
          }
        }
      } finally {
        lock.unlock();
      }

    }

    if (conn == null) {
      if (log.isDebugEnabled()) {
        log.debug("PooledDataSource: Unknown severe error condition.  The connection pool returned a null connection.");
      }
      throw new SQLException(
          "PooledDataSource: Unknown severe error condition.  The connection pool returned a null connection.");
    }

    return conn;
  }

  /**
   * Method to check to see if a connection is still usable
   *
   * @param conn
   *          - the connection to check
   *
   * @return True if the connection is still usable
   */
  protected boolean pingConnection(PooledConnection conn) {
    // 记录是否 ping 成功
    boolean result;

    // 判断真实的连接是否已经关闭, 若已关闭 ping操作直接失败
    try {
      result = !conn.getRealConnection().isClosed();
    } catch (SQLException e) {
      if (log.isDebugEnabled()) {
        log.debug("Connection " + conn.getRealHashCode() + " is BAD: " + e.getMessage());
      }
      result = false;
    }

    // 是否启用侦测查询, 判断是否长时间未使用? 若是, 才需要发起 ping
    if (result && poolPingEnabled && poolPingConnectionsNotUsedFor >= 0
        && conn.getTimeElapsedSinceLastUse() > poolPingConnectionsNotUsedFor) {
      try {
        if (log.isDebugEnabled()) {
          log.debug("Testing connection " + conn.getRealHashCode() + " ...");
        }
        // 通过执行 poolPingQuery 语句来发起 ping
        Connection realConn = conn.getRealConnection();
        try (Statement statement = realConn.createStatement()) {
          statement.executeQuery(poolPingQuery).close();
        }
        if (!realConn.getAutoCommit()) {
          realConn.rollback();
        }
        if (log.isDebugEnabled()) {
          log.debug("Connection " + conn.getRealHashCode() + " is GOOD!");
        }
      } catch (Exception e) {
        // 关闭数据库真实的连接
        log.warn("Execution of ping query '" + poolPingQuery + "' failed: " + e.getMessage());
        try {
          conn.getRealConnection().close();
        } catch (Exception e2) {
          // ignore
        }
        // 标记执行失败
        result = false;
        if (log.isDebugEnabled()) {
          log.debug("Connection " + conn.getRealHashCode() + " is BAD: " + e.getMessage());
        }
      }
    }
    return result;
  }

  /**
   * Unwraps a pooled connection to get to the 'real' connection
   *
   * @param conn
   *          - the pooled connection to unwrap
   *
   * @return The 'real' connection
   */
  public static Connection unwrapConnection(Connection conn) {
    // 如果传入的是被代理的连接
    if (Proxy.isProxyClass(conn.getClass())) {
      // 获取 InvocationHandler 对象
      InvocationHandler handler = Proxy.getInvocationHandler(conn);
      // 如果是 PooledConnection 对象，则获取真实的连接
      if (handler instanceof PooledConnection) {
        return ((PooledConnection) handler).getRealConnection();
      }
    }
    return conn;
  }

  @Override
  protected void finalize() throws Throwable {
    forceCloseAll();
    super.finalize();
  }

  @Override
  public <T> T unwrap(Class<T> iface) throws SQLException {
    throw new SQLException(getClass().getName() + " is not a wrapper.");
  }

  @Override
  public boolean isWrapperFor(Class<?> iface) {
    return false;
  }

  @Override
  public Logger getParentLogger() {
    return Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);
  }

}
