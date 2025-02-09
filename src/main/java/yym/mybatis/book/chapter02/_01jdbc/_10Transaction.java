package yym.mybatis.book.chapter02._01jdbc;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Savepoint;
import java.sql.Statement;

import org.apache.ibatis.io.Resources;
import org.apache.ibatis.jdbc.ScriptRunner;
import org.junit.Before;
import org.junit.Test;

import yym.mybatis.book.util.DbUtils;
import yym.mybatis.book.util.IOUtils;

/**
 * JDBC事务:
 *   用于提供数据完整性、正确的应用程序语义和并发访问数据的一致性
 *      数据完整性:
 *        事务操作的所有步骤都是不可分割的, 要么完全执行, 要么都不执行
 *      正确的应用程序语义:
 *        数据的状态与应用程序的语义一致(应用程序规定订单价格不为负, 事务确保订单更新的过程中总价不为负,
 *        有错误及时回滚), 比如通过异常回滚事务
 *      并发访问数据的一致性:
 *        多个事务并发执行, 保证数据的一致性(事务隔离机制)
 *
 *   事务的隔离级别:
 *      引发的问题:
 *          脏读: A事务读到其它事务未提交的数据, 其它事务可能进行回滚
 *          不可重复读: A事务读取一条数据, B事务对该数据修改并提交, A事务再次读取数据前后不一致
 *          幻读: A事务where读取若干行数据, B事务插入了符合条件的数据, A事务通过相同的条件前后读取数据条数不一致
 *      具体隔离级别:
 *          读未提交: 允许事务读取未提交更改的数据
 *          读已提交: 事务中对任何数据的修改, 在未提交前, 对其它事务不可见
 *          可重复读: 在事务开始时创建一个数据的快照视图, 整个事务的生命周期中, 所有的读取操作都基于事务开始时的快照视图
 *          串行: 所有事务串行执行
 *   事务的保存点:
 *       事务每次回滚都会回滚到保存点
 * @Author: Yym
 * @Version: 1.0
 * @Date: 2025/1/4 19:21
 */
public class _10Transaction {

  @Before
  public void initData() throws  Exception {
    // 初始化数据
    Class.forName("org.hsqldb.jdbcDriver");
    // 获取Connection对象
    Connection conn = DriverManager.getConnection("jdbc:hsqldb:mem:mybatis",
      "sa", "");
    // 使用Mybatis的ScriptRunner工具类执行数据库脚本
    ScriptRunner scriptRunner = new ScriptRunner(conn);
    // 不输出sql日志
    scriptRunner.setLogWriter(null);
    scriptRunner.runScript(Resources.getResourceAsReader("create-table.sql"));
    System.out.println("------------------------");
  }

  @Test
  public void testSavePoint() {
    try {
      Class.forName("org.hsqldb.jdbcDriver");
      // 获取Connection对象
      Connection conn = DriverManager.getConnection("jdbc:hsqldb:mem:mybatis",
        "sa", "");
      String sql1 = "insert into user(create_time, name, password, phone, nick_name) " +
        "values('2010-10-24 10:20:30','User1','test','18700001111','User1')";
      String sql2 = "insert into user(create_time, name, password, phone, nick_name) " +
        "values('2010-10-24 10:20:30','User2','test','18700001111','User2')";
      conn.setAutoCommit(false);
      Statement stmt = conn.createStatement();
      stmt.executeUpdate(sql1);
      // 创建保存点
      Savepoint savepoint = conn.setSavepoint("SP1");
      stmt.executeUpdate(sql2);
      // 回滚到保存点
      conn.rollback(savepoint);
      conn.commit();
      ResultSet rs  = conn.createStatement().executeQuery("select * from user ");
      DbUtils.dumpRS(rs);
      IOUtils.closeQuietly(stmt);
      IOUtils.closeQuietly(conn);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
}
