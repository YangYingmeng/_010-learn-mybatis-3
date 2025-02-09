package yym.mybatis.book.util;

import java.io.Closeable;
import java.io.IOException;

/**
 * 关闭连接工具
 *
 * @Author: Yym
 * @Version: 1.0
 * @Date: 2024/12/30 10:18
 */
public abstract class IOUtils {

  public static void closeQuietly(Closeable closeable) {
    if (closeable != null) {
      try {
        closeable.close();
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
  }

  public static void closeQuietly(AutoCloseable closeable) {
    if (closeable != null) {
      try {
        closeable.close();
      } catch (Exception e) {
        e.printStackTrace();
      }
    }
  }
}
