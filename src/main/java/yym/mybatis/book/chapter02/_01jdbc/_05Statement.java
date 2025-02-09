package yym.mybatis.book.chapter02._01jdbc;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.Statement;
import java.util.Properties;

import javax.sql.DataSource;

import org.apache.ibatis.datasource.DataSourceFactory;
import org.apache.ibatis.datasource.unpooled.UnpooledDataSourceFactory;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.jdbc.ScriptRunner;
import org.junit.Before;
import org.junit.Test;

import yym.mybatis.book.util.IOUtils;

/**
 * Statement 数据库交互对象, 定义了一些数据库操作以及检索SQL执行结果相关的方法
 * 重要子实现:
 *    PreparedStatement
 *    CallableStatement: 存储过程相关接口
 *
 * @Author: Yym
 * @Version: 1.0
 * @Date: 2025/1/2 13:55
 */
public class _05Statement {

  @Before
  public void initData() throws Exception {
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
  }

  @Test
  public void testJdbcBatch() {

    try {
      // 创建DataSource实例
      DataSourceFactory dsf = new UnpooledDataSourceFactory();
      Properties properties = new Properties();
      InputStream configStream = Thread.currentThread().getContextClassLoader().getResourceAsStream("database.properties");
      properties.load(configStream);
      dsf.setProperties(properties);
      DataSource dataSource = dsf.getDataSource();
      // 获取Connection对象
      Connection connection = dataSource.getConnection();
      // Statement 对象由 connection 获取
      Statement statement = connection.createStatement();
      statement.addBatch("insert into  " +
        "user(create_time, name, password, phone, nick_name) " +
        "values('2010-10-24 10:20:30', 'User1', 'test', '18700001111', 'User1');");
      statement.addBatch("insert into " +
        "user (create_time, name, password, phone, nick_name) " +
        "values('2010-10-24 10:20:30', 'User2', 'test', '18700002222', 'User2');");
      statement.executeBatch();
      statement.execute("select * from user");

      ResultSet result = statement.getResultSet();
      dumpRS(result);
      // 关闭连接
      IOUtils.closeQuietly(statement);
      IOUtils.closeQuietly(connection);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  private void dumpRS(ResultSet resultSet) throws Exception {
    ResultSetMetaData metaData = resultSet.getMetaData();
    int columCount = metaData.getColumnCount();
    while (resultSet.next()) {
      for (int i = 1; i <= columCount; i++) {
        String columName = metaData.getColumnName(i);
        String columVal = resultSet.getString(columName);
        System.out.println(columName + ":" + columVal);
      }
      System.out.println("-------------------------------------");
    }
  }
}
