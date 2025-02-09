package yym.mybatis.book.chapter05;

import java.io.IOException;
import java.io.Reader;

import org.apache.ibatis.builder.xml.XMLConfigBuilder;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.Configuration;
import org.junit.Test;

/**
 * Configuration 实例的创建过程
 *     作用:
 *        1. 用于描述 MyBatis 配置信息, 如标签信息
 *        2. 作为容器注册 MyBatis 其它组件
 *        3. 提供工厂方法
 *     创建过程:
 *        MyBatis 框架启动后, 创建 Configuration 对象。 解析配置信息, 将配置信息放在 Configuration 对象中
 *
 * @Author: Yym
 * @Version: 1.0
 * @Date: 2025/1/11 18:27
 */
public class _01CreateConfiguration {

  @Test
  public void testConfiguration() throws IOException {
    Reader reader = Resources.getResourceAsReader("mybatis-config.xml");
    // 创建XMLConfigBuilder实例
    XMLConfigBuilder builder = new XMLConfigBuilder(reader);
    // 调用XMLConfigBuilder.parse（）方法，解析XML创建Configuration对象
    Configuration conf = builder.parse();
  }
}
