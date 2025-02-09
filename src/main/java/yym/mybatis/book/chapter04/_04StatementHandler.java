package yym.mybatis.book.chapter04;

import org.apache.ibatis.executor.statement.StatementHandler;
import org.junit.Test;

/**
 * MyBatis 核心组件之 StatementHandler
 *    封装了对JDBC Statement的操作
 *    子实现:
 *       1. BaseStatementHandler抽象类, 封装了通用的处理逻辑及方法执行流程, 模板方法模式
 *       2. RoutingStatementHandler: 根据不同的类型创建StatementHandler对应的实现
 *       3. SimpleStatementHandler: 封装了对JDBC Statement对象的操作
 *       4. PreparedStatementHandler: 封装了对 PreparedStatement 的操作
 *       5. CallableStatementHandler: 封装了对 JDBC CallableStatement 对象的操作
 * @Author: Yym
 * @Version: 1.0
 * @Date: 2025/1/10 9:44
 */
public class _04StatementHandler {

  @Test
  public void testStatementHandler() {
    System.out.println(StatementHandler.class);
  }
}
