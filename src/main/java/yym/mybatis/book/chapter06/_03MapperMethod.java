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
 * Mapper 方法调用过程详解 (通过动态代理的方式将 Mapper方法的调用转为对 sqlSession api的调用即 iBatis 框架的调用)
 * 1. sqlSession.getMapper 会通过 mapperRegistry 中的 MapperProxyFactory 拿到对应的 MapperProxy 对象
 * 2. MapperProxy 对象是代理对象, 即通过 sqlSession.getMapper 只是拿到一个代理对象
 *    2.1 sqlSession.getMapper 从 mapperRegistry 中获取 MapperProxyFactory
 *    2.2 MapperProxyFactory#newInstance 方法返回一个 MapperProxy 对象, 通过多态调用子类 UserMapper 的方法
 * 3. 代理对象 UserMapper 在触发任何方法时都会调用 MapperProxy#invoke方法
 * 4. MapperProxy#invoke 方法最终会返回一个 MapperMethodInvoke 对象
 * 5. 在调用 invoke#cachedInvoker 方法时, 会通过构造函数构造 MapperMethod 对象
 *   5.1 MapperMethod会初始化 SqlCommand(描述SQL id类型相关信息) 和 MethodSignature(具体的方法签名) 对象
 * 6. 通过返回的 MapperMethodInvoke.invoke() 方法调用 MapperMethod 的 execute() 方法来执行最终的方法
 * 7. execute() 方法会用 SqlCommand以及MethodSignature等信息调用 iBatis 的 增删改查操作
 * 优点:
 * 这种动态代理的方法开发者无需写具体的实现 只要关注 Mapper 接口与对应SQL的映射, 不用关注底层实现
 * 也更符合面向接口编程
 * 与具体的sqlSession的api进行解耦
 *
 * @Author: Yym
 * @Version: 1.0
 * @Date: 2025/2/6 16:01
 */
public class _03MapperMethod {

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
    // 执行Mapper方法
    List<UserEntity> allUser = mapper.listAllUser();
  }
}
