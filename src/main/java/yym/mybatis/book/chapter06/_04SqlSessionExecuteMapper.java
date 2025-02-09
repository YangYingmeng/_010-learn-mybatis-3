package yym.mybatis.book.chapter06;

import java.io.IOException;
import java.io.Reader;
import java.util.List;

import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.junit.Test;

import yym.mybatis.book.entity.UserEntity;

/**
 * SqlSession 执行 Mapper 的过程
 *
 * @Author: Yym
 * @Version: 1.0
 * @Date: 2025/2/6 17:23
 */
public class _04SqlSessionExecuteMapper {

  @Test
  public void testMybatis() throws IOException {

    // 获取 MyBatis 配置文件输入流
    Reader reader = Resources.getResourceAsReader("mybatis-config.xml");
    // 通过 SqlSessionFactoryBuilder 获取 SqlSessionFactory, 返回的是 DefaultSqlSessionFactory, 多态
    SqlSessionFactory sqlSessionFactory = new SqlSessionFactoryBuilder().build(reader);
    // 通过 SqlSessionFactory 获取 SqlSession
    SqlSession sqlSession = sqlSessionFactory.openSession();
    List<UserEntity> users = sqlSession.selectList("yym.mybatis.book.chapter04.mapper.listAllUser");
    System.out.println(users);
  }
}
