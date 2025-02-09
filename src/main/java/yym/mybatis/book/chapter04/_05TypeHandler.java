package yym.mybatis.book.chapter04;

import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.TypeHandler;
import org.apache.ibatis.type.TypeHandlerRegistry;
import org.junit.Test;

/**
 * MyBatis 核心组件之 TypeHandler, 如需自定义实现 BaseTypeHandler 即可, 自定义完后可以使用 TypeHandlerRegistry.register 进行关系的映射
 *    处理 JDBC 类型和 JAVA 类型的转换
 *      1. PreparedStatement 对象为占位符设置值时, 需要调用 setXXX()方法, 将 JAVA 类型 -> JDBC对应的类型
 *      2. 执行SQL语句获取ResultSet对象, 会调用getXXX()方法获取字段, 将 JDBC 类型 -> JAVA 类型
 *    TypeHandlerRegistry: 建立 JDBC 类型、 JAVA 类型 和 TypeHandler 之间的映射关系
 * @Author: Yym
 * @Version: 1.0
 * @Date: 2025/1/11 10:43
 */
public class _05TypeHandler {

  @Test
  public void testTypeHandler() {
    System.out.println(TypeHandler.class);
    System.out.println(BaseTypeHandler.class);
    System.out.println(TypeHandlerRegistry.class);
  }
}
