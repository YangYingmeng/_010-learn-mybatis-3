package yym.mybatis.book.chapter04;

import yym.mybatis.book.entity.UserEntity;
import yym.mybatis.book.util.DbUtils;

import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;
import java.util.List;

import org.apache.ibatis.executor.BaseExecutor;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.ExecutorType;
import org.apache.ibatis.session.RowBounds;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.apache.ibatis.transaction.jdbc.JdbcTransaction;
import org.junit.Before;
import org.junit.Test;

import com.alibaba.fastjson.JSON;

/**
 * MyBatis 核心组件之 Executor
 *    SqlSession 提供了操作数据库的 API, SqlSession 实质上是 Executor 的包装模式, 真正执行SQL的是 Executor组件
 *    Executor 具体实现:
 *        SimpleExecutor: 基础 Executor, 完成基本的增删改查操作, 每次操作都会打开一个新的数据库连接
 *        ReuseExecutor: 对 JDBC 中的 Statement 对象做了缓存, 执行相同的SQL语句时, 直接取出Statement对象进行复用(享元思想)
 *        BatchExecutor: 将多条SQL语句进行缓存, 在一个数据库连接中一次性执行所有缓存的SQL
 *        BaseExecutor: 定义了执行流程及通用的处理方法 模板方法模式, 是上面三个Executor的父类
 *        CachingExecutor: 如果 MyBatis 开启二级缓存, 则会用 CachingExecutor 对上面三个 Executor 修饰, 为查询操作增加二级缓存功能(装饰器模式)
 * @Author: Yym
 * @Version: 1.0
 * @Date: 2025/1/9 9:50
 */
public class _02Executor {

  @Test
  public void printExecutor() {
    System.out.println(BaseExecutor.class);
  }

  @Before
  public void initData() {

    DbUtils.initData();
  }


  @Test
  public void testExecutor() throws IOException, SQLException {
    // 获取配置文件输入流
    InputStream inputStream = Resources.getResourceAsStream("mybatis-config.xml");
    // 通过SqlSessionFactoryBuilder的build()方法创建SqlSessionFactory实例
    SqlSessionFactory sqlSessionFactory = new SqlSessionFactoryBuilder().build(inputStream);
    // 调用openSession()方法创建SqlSession实例
    SqlSession sqlSession = sqlSessionFactory.openSession();
    Configuration configuration = sqlSession.getConfiguration();
    // 从Configuration对象中获取描述SQL配置的MappedStatement对象
    MappedStatement listAllUserStmt = configuration.getMappedStatement(
      "yym.mybatis.book.chapter04.mapper.UserMapper.listAllUser");
    //创建ReuseExecutor实例
    Executor reuseExecutor = configuration.newExecutor(
      new JdbcTransaction(sqlSession.getConnection()),
      ExecutorType.REUSE
    );
    // 调用query()方法执行查询操作
    List<UserEntity> userList =  reuseExecutor.query(listAllUserStmt,
      null,
      RowBounds.DEFAULT,
      Executor.NO_RESULT_HANDLER);
    System.out.println(JSON.toJSON(userList));
  }
}
