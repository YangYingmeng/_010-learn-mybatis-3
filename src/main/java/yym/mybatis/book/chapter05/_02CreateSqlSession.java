package yym.mybatis.book.chapter05;

import java.io.IOException;
import java.io.Reader;

import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.junit.Test;

/**
 * SqlSession 实例的创建过程
 *
 * @Author: Yym
 * @Version: 1.0
 * @Date: 2025/1/16 10:01
 */
public class _02CreateSqlSession {

  @Test
  public void testSqlSession() throws IOException {

    // 获取 MyBatis 配置文件输入流
    Reader reader = Resources.getResourceAsReader("mybatis-config.xml");
    // 通过 SqlSessionFactoryBuilder 获取 SqlSessionFactory, 返回的是 DefaultSqlSessionFactory, 多态
    SqlSessionFactory sqlSessionFactory = new SqlSessionFactoryBuilder().build(reader);
    // 通过 SqlSessionFactory 获取 SqlSession
    SqlSession sqlSession = sqlSessionFactory.openSession();
  }
}
