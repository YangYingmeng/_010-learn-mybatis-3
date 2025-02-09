package yym.mybatis.book.chapter03.objectfactory;


import org.apache.ibatis.reflection.factory.DefaultObjectFactory;

/**
 * 自定义ObjectFactory进行扩展
 * 自定义完, 需要在MyBatis主配置文件通过<objectFactory>标签进行配置
 *
 * @Author: Yym
 * @Version: 1.0
 * @Date: 2025/1/7 16:06
 */
public class CustomObjectFactory extends DefaultObjectFactory {

  /*@Override
  public Object create(Class type) {
    if (type.equals(User.class)) {
      // 实例化User类
      User user = (User)super.create(type);
      user.setName("测试CustomObjectFactory");
      return user;
    }
    return super.create(type);
  }*/
}
