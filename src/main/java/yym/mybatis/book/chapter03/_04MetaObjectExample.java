package yym.mybatis.book.chapter03;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.ibatis.reflection.MetaObject;
import org.apache.ibatis.reflection.SystemMetaObject;
import org.junit.Test;

import yym.mybatis.book.entity.Order;
import yym.mybatis.book.entity.UserEntity;

/**
 * MetaObject: 反射工具类
 * 用于获取和设置对象的属性值
 *
 * @Author: Yym
 * @Version: 1.0
 * @Date: 2025/1/6 15:41
 */
public class _04MetaObjectExample {


  @Test
  public void testMetaObject() {
    List<Order> orders = new ArrayList() {
      {
        add(new Order("order20171024010246", "《Mybatis源码深度解析》图书"));
        add(new Order("order20171024010248", "《AngularJS入门与进阶》图书"));
      }
    };

    UserEntity user = new UserEntity(1l, "yym", new Date(), "123", "110", "m", orders);
    MetaObject metaObject = SystemMetaObject.forObject(user);
    // 获取第一笔订单的商品名称
    System.out.println(metaObject.getValue("orders[0].goodsName"));
    // 获取第二笔订单的商品名称
    System.out.println(metaObject.getValue("orders[1].goodsName"));
    // 为属性设置值
    metaObject.setValue("orders[1].orderNo", "order20181113010139");
    // 判断User对象是否有orderNo属性
    System.out.println("是否有orderNo属性且orderNo属性有对应的Getter方法：" + metaObject.hasGetter("orderNo"));
    // 判断User对象是否有name属性
    System.out.println("是否有name属性且orderNo属性有对应的name方法：" + metaObject.hasGetter("name"));
  }
}
