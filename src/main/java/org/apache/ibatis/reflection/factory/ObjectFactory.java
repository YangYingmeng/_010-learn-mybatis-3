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
package org.apache.ibatis.reflection.factory;

import java.util.List;
import java.util.Properties;

/**
 * MyBatis uses an ObjectFactory to create all needed new Objects.
 *
 * @author Clinton Begin
 */
public interface ObjectFactory {

  /**
   * 设置Properties
   * Sets configuration properties.
   *
   * @param properties
   *          configuration properties
   */
  default void setProperties(Properties properties) {
    // NOP
  }

  /**
   * 通过默认构造器创建指定类的对象
   * Creates a new object with default constructor.
   *
   * @param <T>
   *          the generic type
   * @param type
   *          Object type
   *
   * @return the t
   */
  <T> T create(Class<T> type);

  /**
   * 通过特定的构造方法创建指定类的对象
   * Creates a new object with the specified constructor and params.
   *
   * @param <T>
   *          the generic type
   * @param type
   *          Object type
   * @param constructorArgTypes
   *          Constructor argument types
   * @param constructorArgs
   *          Constructor argument values
   *
   * @return the t
   */
  <T> T create(Class<T> type, List<Class<?>> constructorArgTypes, List<Object> constructorArgs);

  /**
   * Returns true if this object can have a set of other objects. It's main purpose is to support
   * non-java.util.Collection objects like Scala collections.
   * 判断类是否是集合
   * @param <T>
   *          the generic type
   * @param type
   *          Object type
   *
   * @return whether it is a collection or not
   *
   * @since 3.1.0
   */
  <T> boolean isCollection(Class<T> type);

}
