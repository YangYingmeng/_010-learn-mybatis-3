package yym.mybatis.book.chapter04;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.junit.Before;
import org.junit.Test;

import com.alibaba.fastjson.JSON;

import yym.mybatis.book.entity.UserEntity;
import yym.mybatis.book.chapter04.mapper.UserMapper;
import yym.mybatis.book.util.DbUtils;

/**
 * 测试 MyBatis 如何操作数据库
 *
 * @Author: Yym
 * @Version: 1.0
 * @Date: 2025/1/7 17:22
 */
public class MybatisExample {

  @Before
  public void initData() {
    DbUtils.initData();
  }

  @Test
  public void testMyBatis() throws IOException {
    // 获取配置文件输入流
    InputStream inputStream = Resources.getResourceAsStream("mybatis-config.xml");
    // 通过SqlSessionFactoryBuilder的build()方法创建SqlSessionFactory实例
    SqlSessionFactory sqlSessionFactory = new SqlSessionFactoryBuilder().build(inputStream);
    // 调用openSession()方法创建SqlSession实例
    SqlSession sqlSession = sqlSessionFactory.openSession();
    // 获取UserMapper代理对象
    UserMapper userMapper = sqlSession.getMapper(UserMapper.class);
    // 执行Mapper方法，获取执行结果
    List<UserEntity> userList = userMapper.listAllUser();
         /*
        // 兼容Ibatis，通过Mapper Id执行SQL操作
        List<UserEntity> userList = sqlSession.selectList(
                "com.blog4java.mybatis.com.blog4java.mybatis.example.mapper.UserMapper.listAllUser");
        */
    System.out.println(JSON.toJSONString(userList));
  }
}
