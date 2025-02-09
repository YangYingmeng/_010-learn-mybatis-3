package yym.mybatis.book.chapter06;

import yym.mybatis.book.chapter04.mapper.UserMapper;
import yym.mybatis.book.entity.UserEntity;

import java.io.IOException;
import java.io.Reader;
import java.util.List;

import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.junit.Test;

/**
 * 项目启动时 Mybatis 解析所有的 Mapper 接口,
 * 通过 {@link org.apache.ibatis.binding.MapperRegistry#addMapper(Class)} 方法
 * 将 Mapper 接口对应的信息和 MapperProxyFactory 对象注册到 MapperRegistry 中
 *
 * @Author: Yym
 * @Version: 1.0
 * @Date: 2025/2/6 10:34
 */
public class _01RegistMapper {

  @Test
  public void testMybatis() throws IOException {

    // 获取 MyBatis 配置文件输入流
    Reader reader = Resources.getResourceAsReader("mybatis-config.xml");
    // 通过 SqlSessionFactoryBuilder 获取 SqlSessionFactory, 返回的是 DefaultSqlSessionFactory, 多态
    SqlSessionFactory sqlSessionFactory = new SqlSessionFactoryBuilder().build(reader);
    // 通过 SqlSessionFactory 获取 SqlSession
    SqlSession sqlSession = sqlSessionFactory.openSession();
    // 调用 getMapper 获取一个动态代理对象
    UserMapper mapper = sqlSession.getMapper(UserMapper.class);
    List<UserEntity> allUser = mapper.listAllUser();
  }
}
