package yym.mybatis.book.chapter02._01jdbc;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.Statement;

import javax.sql.DataSource;

import org.apache.ibatis.datasource.unpooled.UnpooledDataSource;
import org.junit.Test;

import yym.mybatis.book.util.DbUtils;
import yym.mybatis.book.util.IOUtils;

/**
 * 使用 DataSource 连接数据库(目前主推荐)
 * 一个 DataSource 对象的属性被设置后, 就代表一个特定的数据源
 *
 * @Author: Yym
 * @Version: 1.0
 * @Date: 2024/12/30 10:29
 */
public class _02DataSource {

  @Test
  public void testJdbc() {
    // 初始化数据
    DbUtils.initData();
    try {
      // 创建DataSource实例
      DataSource dataSource = new UnpooledDataSource("org.hsqldb.jdbcDriver",
        "jdbc:hsqldb:mem:mybatis", "sa", "");
      // 获取Connection对象
      Connection connection = dataSource.getConnection();
      // JDBC提供的SQL语句执行器, 可以对SQL进行CRUD等操作
      Statement statement = connection.createStatement();
      ResultSet resultSet = statement.executeQuery("select * from user");
      // 遍历ResultSet
      ResultSetMetaData metaData = resultSet.getMetaData();
      int columCount = metaData.getColumnCount();
      while (resultSet.next()) {
        for (int i = 1; i <= columCount; i++) {
          String columName = metaData.getColumnName(i);
          String columVal = resultSet.getString(columName);
          System.out.println(columName + ":" + columVal);
        }
        System.out.println("---------------------------------------");
      }
      // 关闭连接
      IOUtils.closeQuietly(statement);
      IOUtils.closeQuietly(connection);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
}
