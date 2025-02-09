package yym.mybatis.book.chapter03;

import java.sql.Connection;
import java.sql.DriverManager;

import org.apache.ibatis.io.Resources;
import org.apache.ibatis.jdbc.ScriptRunner;
import org.junit.Test;

/**
 * ScriptRunner
 *    读取脚本文件中的SQL语句并执行
 *
 * @Author: Yym
 * @Version: 1.0
 * @Date: 2025/1/6 14:38
 */
public class _02ScriptRunnerExample {

  @Test
  public void testScriptRunner() {
    try {
      Connection connection = DriverManager.getConnection("jdbc:hsqldb:mem:mybatis",
        "sa", "");
      ScriptRunner scriptRunner = new ScriptRunner(connection);
      scriptRunner.runScript(Resources.getResourceAsReader("mybatis-book/create-table.sql"));
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
}
