package yym.mybatis.book.chapter04;

import org.apache.ibatis.executor.parameter.ParameterHandler;
import org.apache.ibatis.scripting.defaults.DefaultParameterHandler;
import org.junit.Test;

/**
 * MyBatis 核心组件之 ParameterHandler
 *    在使用 PreparedStatement 或 CallableStatement, 如果SQL语句中有参数占位符, 在执行之前会使用 ParameterHandler 为占位符设值
 *
 * @Author: Yym
 * @Version: 1.0
 * @Date: 2025/1/11 10:56
 */
public class _06ParameterHandler {

  @Test
  public void testTypeHandler() {
    System.out.println(ParameterHandler.class);
    // 以 defaultParameterHandler.setParameters 方法为例
    DefaultParameterHandler defaultParameterHandler = new DefaultParameterHandler(null, null, null);
    defaultParameterHandler.setParameters(null);
  }
}
