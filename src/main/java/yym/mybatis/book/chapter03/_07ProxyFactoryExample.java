package yym.mybatis.book.chapter03;


import static org.mockito.Mockito.mock;

/**
 * ProxyFactory: 代理工厂, 创建动态代理对象
 *    子实现:
 *      JavassistProxyFactory: 基于接口的代理, 通过字节码修改直接生成代理类
 *      CglibProxyFactory(废弃): 适用于没有实现接口的类, 基于原始类创建代理类
 *
 * @Author: Yym
 * @Version: 1.0
 * @Date: 2025/1/7 16:23
 */
public class _07ProxyFactoryExample {

  /*@Test
  public void testProxyFactory() {
    // 创建ProxyFactory对象
    ProxyFactory proxyFactory = new JavassistProxyFactory();
    Order order = new Order("gn20170123", "《Mybatis源码深度解析》图书");
    ObjectFactory objectFactory = new DefaultObjectFactory();
    // 调用ProxyFactory对象的createProxy（）方法创建代理对象
    Object proxyOrder = proxyFactory.createProxy(order
      , mock(ResultLoaderMap.class)
      , mock(Configuration.class)
      , objectFactory
      , Arrays.asList(String.class, String.class)
      , Arrays.asList(order.getOrderNo(), order.getGoodsName())
    );
    System.out.println(proxyOrder.getClass());
    System.out.println(((Order) proxyOrder).getGoodsName());
  }*/
}
