package yym.mybatis.book.chapter04;

import org.apache.ibatis.session.Configuration;
import org.junit.Test;

/**
 * MyBatis 核心组件之 Configuration
 *    MyBatis配置文件有2种, 一是框架属性的主配置文件, 二是执行SQL 的 Mapper配置文件
 *    Configuration 中定义的属性可以通过 <setting> 标签指定
 *    作为容器存放 TypeHandler TypeAlias Mapper接口及Mapper SQL配置信息(MapperRegistry 等)
 *    作为 Executor StatementHandler ResultSetHandler ParameterHandler 组件的工厂类, 根据不同的配置创建这些组件对应的实例
 * @Author: Yym
 * @Version: 1.0
 * @Date: 2025/1/8 13:47
 */
public class _01Configuration {

  @Test
  public void testConfiguration() {
    System.out.println(Configuration.class);
  }
}
