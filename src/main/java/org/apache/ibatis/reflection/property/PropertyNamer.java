/*
 *    Copyright 2009-2023 the original author or authors.
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *       https://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package org.apache.ibatis.reflection.property;

import java.util.Locale;

import org.apache.ibatis.reflection.ReflectionException;

/**
 * @author Clinton Begin
 */
public final class PropertyNamer {

  private PropertyNamer() {
    // Prevent Instantiation of Static Class
  }

  /**
   * 根据方法名，获得对应的属性名
   */
  public static String methodToProperty(String name) {
    // is 方法
    if (name.startsWith("is")) {
      name = name.substring(2);
      // get / set方法
    } else if (name.startsWith("get") || name.startsWith("set")) {
      name = name.substring(3);
      // 非 is get set方法抛出异常
    } else {
      throw new ReflectionException(
        "Error parsing property name '" + name + "'.  Didn't start with 'is', 'get' or 'set'.");
    }
    // 首字母小写
    if (name.length() == 1 || name.length() > 1 && !Character.isUpperCase(name.charAt(1))) {
      name = name.substring(0, 1).toLowerCase(Locale.ENGLISH) + name.substring(1);
    }

    return name;
  }

  /**
   * 判断是否为 get set方法
   */
  public static boolean isProperty(String name) {
    return isGetter(name) || isSetter(name);
  }

  /**
   * 判断是否是get is方法
   */
  public static boolean isGetter(String name) {
    return name.startsWith("get") && name.length() > 3 || name.startsWith("is") && name.length() > 2;
  }

  /**
   * 判断是否为 set方法
   */
  public static boolean isSetter(String name) {
    return name.startsWith("set") && name.length() > 3;
  }

}
