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
 * ResultSet:
 *    本质上是某次查询对数据库的快照, 包含了数据库的游标信息, 在遍历ResultSet时是对快照的游标进行移动
 *    提供检索和操作SQL执行结果相关的方法, 并不是java的集合
 * 操作方式:
 *    游标: resultSet中有多条数据, 游标所在位置表示当前位置, resultSet的快照会对数据库的游标进行复制
 *    修改ResultSet对象是否影响数据库数据, 需要commit事务才会最终影响到数据库
 * 相关属性:
 *    类型:
 *      TYPE_FORWARD_ONLY(默认): 不可滚动, 游标只能向前移动, 从第一行到最后一行
 *      TYPE_SCROLL_INSENSITIVE: 游标可以前后移动, 也可以移动到绝对位置, 对resultSet对象的修改不影响数据库数据
 *      TYPE_SCROLL_SENSITIVE: 游标可以前后移动, 也可以移动到绝对位置, 对resultSet对象的修改直接影响数据库数据
 *    并行性:
 *      CONCUR_READ_ONLY: 只能从resultSet中读数据
 *      CONCUR_UPDATABLE: 能读且能更新
 *    可保持性:
 *      HOLD_CURSORS_OVER_COMMIT: 当调用Connection#commit(), 不关闭当前事务创建的ResultSet对象
 *      CLOSE_CURSORS_AT_COMMIT: 前事务创建的ResultSet对象在事务提交后被关闭
 *
 * @Author: Yym
 * @Version: 1.0
 * @Date: 2025/1/3 16:57
 */
public class _08ResultSet {

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
      // 设置resultSet相关属性
      Statement statement = connection.createStatement(
        ResultSet.TYPE_SCROLL_INSENSITIVE,
        ResultSet.CONCUR_READ_ONLY,
        ResultSet.CLOSE_CURSORS_AT_COMMIT
      );
      // 获取对应的resultSet
      ResultSet resultSet = statement.executeQuery("select * from user");
      ResultSetMetaData metaData = resultSet.getMetaData();
      int columCount = metaData.getColumnCount();
      // 通过游标遍历结果集
      while (resultSet.next()) {
        for (int i = 1; i <= columCount; i++) {
          String columName = metaData.getColumnName(i);
          String columVal = resultSet.getString(columName);
          System.out.println(columName + ":" + columVal);
          if (i == 1) {
            // 更改结果集
            resultSet.updateString("name", "update");
            // 将对结果集的更改同步到数据库需要提交事务, 如果是TYPE_SCROLL_INSENSITIVE模式
            // 数据库的变更不会同步到结果集中, 遍历的数据可能不是实时的
            resultSet.updateRow();
            connection.commit();
          }
          if (i == 2) {
            // 将游标移动到某行, 该行数据在数据库中进行删除,
            // INSENSITIVE属性可以决定数据库数据删除是否同步到resultSet中
            resultSet.absolute(2);
            resultSet.deleteRow();
            connection.commit();
          }
        }
        System.out.println("---------------------------------------");
      }
      // 关闭resultSet api
      resultSet.close();
      // 2. 关闭连接, 会自动关闭resultSet
      IOUtils.closeQuietly(statement);
      IOUtils.closeQuietly(connection);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
}
