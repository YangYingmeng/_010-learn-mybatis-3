package yym.mybatis.book.chapter02._01jdbc;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.Statement;
import java.util.Properties;

import javax.sql.DataSource;

import org.apache.ibatis.datasource.DataSourceFactory;
import org.apache.ibatis.datasource.unpooled.UnpooledDataSourceFactory;
import org.junit.Test;

import yym.mybatis.book.util.DbUtils;
import yym.mybatis.book.util.IOUtils;

/**
 * DataSource 工厂
 * 工厂模式获取 DataSource实例
 *
 * @Author: Yym
 * @Version: 1.0
 * @Date: 2024/12/30 10:35
 */
public class _03DataSourceFactory {

  @Test
  public void testJdbc() {
    // 初始化数据
    DbUtils.initData();
    try {
      // 创建DataSource实例
      DataSourceFactory dsf = new UnpooledDataSourceFactory();
      // 参数配置化
      Properties properties = new Properties();
      InputStream configStream =
        Thread.currentThread().getContextClassLoader().getResourceAsStream("mybatis-book/database.properties");
      properties.load(configStream);
      dsf.setProperties(properties);
      DataSource dataSource = dsf.getDataSource();
      // 获取Connection对象
      Connection connection = dataSource.getConnection();
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
        System.out.println("----------------------------------------");
      }
      // 关闭连接
      IOUtils.closeQuietly(statement);
      IOUtils.closeQuietly(connection);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
}
