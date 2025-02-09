package yym.mybatis.book.chapter04;

import java.sql.SQLException;

import org.apache.ibatis.executor.result.DefaultResultHandler;
import org.apache.ibatis.executor.resultset.DefaultResultSetHandler;
import org.apache.ibatis.executor.resultset.ResultSetHandler;
import org.junit.Test;

/**
 * MyBatis 核心组件之 ResultSetHandler
 *    用于在 StatementHandler 对象执行完查询操作或存储过程后, 对结果集或存储过程的执行结果进行处理
 *    DefaultResultHandler 默认实现类
 *    DefaultResultSetHandler 默认实现类
 * @Author: Yym
 * @Version: 1.0
 * @Date: 2025/1/11 16:28
 */
public class _07ResultSetHandler {

  @Test
  public void testResultSetHandler() throws SQLException {
    System.out.println(ResultSetHandler.class);
    // 以 handleResultSets 为例
    DefaultResultSetHandler defaultResultHandler = new DefaultResultSetHandler(null, null, null,
      null, null, null);
    defaultResultHandler.handleResultSets(null);
  }
}
