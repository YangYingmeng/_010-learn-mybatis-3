package yym.mybatis.book.chapter02._01jdbc;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.Statement;

import org.junit.Test;

import yym.mybatis.book.util.DbUtils;
import yym.mybatis.book.util.IOUtils;

/**
 * 使用 DriverManager 连接数据库
 * 1. 与数据源建立连接
 * 2. 执行SQL语句
 * 3. 检索SQL执行结果
 * 4. 关闭连接
 *
 * 2个重要方法:
 *    DriverManager#registerDriver()
 *      注册驱动, 每个驱动类在加载时, 都会用静态代码块优先进行驱动注册, 如JDBCDriver
 *    DriverManager#getConnection()
 *      DriverManager会对所有的Driver进行遍历, 通过aDriver.driver.connect方法找到对应url驱动,
 *      与数据库建立连接, 然后返回Connection对象
 *
 * @Author: Yym
 * @Version: 1.0
 * @Date: 2024/12/30 10:14
 */
public class _01DriverManager {

  @Test
  public void testJDBCByDriverManager() {

    // 初始化数据
    DbUtils.initData();
    try {
      // 加载驱动, 驱动加载时会执行静态注册
      Class.forName("org.hsqldb.jdbcDriver");
      // 使用DriverManager获取Connection对象, 通过该对象对数据库进行操作
      Connection connection = DriverManager.getConnection("jdbc:hsqldb:mem:mybatis",
        "sa", "");
      Statement statement = connection.createStatement();
      // 查询操作的结果集
      ResultSet resultSet = statement.executeQuery("select * from user");
      // 获取结果集元数据对象, 遍历ResultSet
      ResultSetMetaData metaData = resultSet.getMetaData();
      int columCount = metaData.getColumnCount();
      while (resultSet.next()) {
        for (int i = 1; i <= columCount; i++) {
          String columName = metaData.getColumnName(i);
          String columVal = resultSet.getString(columName);
          System.out.println(columName + ":" + columVal);
        }
        System.out.println("--------------------------------------");
      }
      // 关闭连接
      IOUtils.closeQuietly(statement);
      IOUtils.closeQuietly(connection);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
}
