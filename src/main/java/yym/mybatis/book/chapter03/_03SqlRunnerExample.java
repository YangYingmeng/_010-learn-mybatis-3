package yym.mybatis.book.chapter03;

import yym.mybatis.book.util.IOUtils;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;

import org.apache.ibatis.io.Resources;
import org.apache.ibatis.jdbc.SQL;
import org.apache.ibatis.jdbc.ScriptRunner;
import org.apache.ibatis.jdbc.SqlRunner;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.alibaba.fastjson.JSON;

/**
 * SqlRunner:
 *   该类对 JDBC 做了很好的封装, 结合SQL工具类, 可以很方便的通过java代码执行sql语句并检索执行结果
 * @Author: Yym
 * @Version: 1.0
 * @Date: 2025/1/6 14:42
 */
public class _03SqlRunnerExample {

  Connection connection = null;

  @Before
  public void initTable() throws SQLException, IOException {
    connection = DriverManager.getConnection("jdbc:hsqldb:mem:mybatis",
      "sa", "");
    ScriptRunner scriptRunner = new ScriptRunner(connection);
    scriptRunner.setLogWriter(null);
    scriptRunner.runScript(Resources.getResourceAsReader("mybatis-book/create-table.sql"));
    scriptRunner.runScript(Resources.getResourceAsReader("mybatis-book/init-data.sql"));
  }

  @Test
  public void testSelectOne() throws SQLException {
    SqlRunner sqlRunner = new SqlRunner(connection);
    String queryUserSql = new SQL() {
      {
        SELECT("*");
        FROM("user");
        WHERE("id = ?");
      }
    }.toString();
    Map<String, Object> resultMap = sqlRunner.selectOne(queryUserSql, Integer.valueOf(1));
    System.out.println(JSON.toJSONString(resultMap));
  }

  @Test
  public void testDelete() throws SQLException {
    SqlRunner sqlRunner = new SqlRunner(connection);
    String deleteUserSql = new SQL() {
      {
        DELETE_FROM("user");
        WHERE("id = ?)");
      }
    }.toString();
    sqlRunner.delete(deleteUserSql, Integer.valueOf(1));
  }

  @Test
  public void testUpdate() throws SQLException {
    SqlRunner sqlRunner = new SqlRunner(connection);
    String updateUserSql = new SQL() {
      {
        UPDATE("user");
        SET("nick_name = ?");
        WHERE("id = ?");
      }
    }.toString();
    sqlRunner.update(updateUserSql, Integer.valueOf(1));
  }

  @Test
  public void testInsert() throws SQLException {
    SqlRunner sqlRunner = new SqlRunner(connection);
    String insertUserSql = new SQL() {
      {
        INSERT_INTO("user");
        INTO_COLUMNS("create_time,name,password,phone,nick_name");
        INTO_VALUES("?,?,?,?,?");
      }
    }.toString();
    String createTime = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    sqlRunner.insert(insertUserSql,createTime,"Jane","test","18700000000","J");
  }


  @After
  public void closeConnection() {
    IOUtils.closeQuietly(connection);
  }

}
