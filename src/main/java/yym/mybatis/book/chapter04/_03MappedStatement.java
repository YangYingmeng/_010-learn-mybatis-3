package yym.mybatis.book.chapter04;

import org.apache.ibatis.mapping.MappedStatement;
import org.junit.Test;

/**
 * MyBatis 核心组件之 MappedStatement
 *    描述 select|update|insert|delete 包括注解 配置的 SQL 信息, 即 <select id = "配置"/>
 * @Author: Yym
 * @Version: 1.0
 * @Date: 2025/1/9 17:14
 */
public class _03MappedStatement {

  @Test
  public void testMappedStatement() {
    System.out.println(MappedStatement.class);
  }
}
