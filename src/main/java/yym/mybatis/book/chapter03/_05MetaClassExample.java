package yym.mybatis.book.chapter03;

import java.lang.reflect.InvocationTargetException;

import org.apache.ibatis.reflection.DefaultReflectorFactory;
import org.apache.ibatis.reflection.MetaClass;
import org.apache.ibatis.reflection.invoker.Invoker;
import org.junit.Test;

import com.alibaba.fastjson.JSON;

import yym.mybatis.book.entity.Order;

/**
 * MetaClass
 * MyBatis的反射工具类, 用于获取类相关信息
 *
 * @Author: Yym
 * @Version: 1.0
 * @Date: 2025/1/7 9:47
 */
public class _05MetaClassExample {


  @Test
  public void testMetaClass() {
    MetaClass metaClass = MetaClass.forClass(Order.class, new DefaultReflectorFactory());
    // 获取所有有Getter方法的属性名
    String[] getterNames = metaClass.getGetterNames();
    System.out.println(JSON.toJSONString(getterNames));
    // 是否有默认构造方法
    System.out.println("是否有默认构造方法：" + metaClass.hasDefaultConstructor());
    // 某属性是否有对应的Getter/Setter方法
    System.out.println("orderNo属性是否有对应的Getter方法：" + metaClass.hasGetter("orderNo"));
    System.out.println("orderNo属性是否有对应的Setter方法：" + metaClass.hasSetter("orderNo"));

    System.out.println("orderNo属性类型：" + metaClass.getGetterType("orderNo"));

    // 获取属性Getter方法
    Invoker invoker = metaClass.getGetInvoker("orderNo");
    try {
      // 通过Invoker对象调用Getter方法获取属性值
      Object orderNo = invoker.invoke(new Order("order20171024010248", "《Mybatis源码深度解析》图书"), null);
      System.out.println(orderNo);
    } catch (IllegalAccessException e) {
      e.printStackTrace();
    } catch (InvocationTargetException e) {
      e.printStackTrace();
    }

  }
}
