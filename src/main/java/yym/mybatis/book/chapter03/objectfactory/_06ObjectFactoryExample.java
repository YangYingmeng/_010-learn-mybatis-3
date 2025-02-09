package yym.mybatis.book.chapter03.objectfactory;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.reflection.factory.DefaultObjectFactory;
import org.junit.Test;

/**
 * ObjectFactory: 对象工厂
 *    MyBatis 通过 ObjectFactory 实例创建Mapper映射结果对象, 是MyBatis提供的扩展机制, 防止在获取实例前需要处理逻辑
 *    参考 CustomObjectFactory
 * @Author: Yym
 * @Version: 1.0
 * @Date: 2025/1/7 9:53
 */
public class _06ObjectFactoryExample {

  @Test
  public void testObjectFactory() {
    DefaultObjectFactory objectFactory = new DefaultObjectFactory();
    List<Integer> list = objectFactory.create(List.class);
    Map map = objectFactory.create(Map.class);
    list.addAll(Arrays.asList(1,2,3));
    map.put("test", "test");
    System.out.println(list);
    System.out.println(map);
  }
}
