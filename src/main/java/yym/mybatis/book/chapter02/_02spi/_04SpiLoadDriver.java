package yym.mybatis.book.chapter02._02spi;

import java.sql.Driver;
import java.sql.DriverManager;
import java.util.ServiceLoader;

import org.junit.Test;

/**
 * 通过SPI形式动态加载数据驱动(解耦 灵活配置)
 * SPI: 服务提供接口
 * 操作:
 *  服务A提供接口InterfaceA
 *  服务B C 实现 InterfaceA, ImplementB ImplementC
 *  服务A可以通过静态注册+动态加载的形式加载BC的实现, 也可以通过
 *    1. 静态注册 + ServiceLoader: DriverManager的实现方式
 *        1.1 所有Driver的实现类中(驱动厂商), 都有静态代码块注册自身(如: JDBCDriver#DriverManager.registerDriver() )
 *        1.2 注册成功后, DriverManager通过 getDrivers#ensureDriversInitialized()方法使用
 *            ServiceLoader.load(Driver.class)方法动态加载第三方数据源驱动
 *        1.3 驱动厂商可以增加META-INF/services文件 变更驱动的实现类
 *        1.4 前提是项目支持热更新或
 *
 *
 * @Author: Yym
 * @Version: 1.0
 * @Date: 2024/12/30 14:00
 */
public class _04SpiLoadDriver {

  @Test
  public void testSPI() {
    // 查看 DriverManager#ensureDriversInitialized(), 是JDBC使用SPI机制动态加载驱动
    // 加载 java.sql.Driver下的org.hsqldb.jdbc.JDBCDriver
    ServiceLoader<Driver> drivers = ServiceLoader.load(java.sql.Driver.class);
    for (Driver driver : drivers ) {
      // 通过反射动态加载实现
      System.out.println(driver.getClass().getName());
    }
  }
}
