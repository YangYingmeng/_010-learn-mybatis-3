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
package org.apache.ibatis.type;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

/**
 * References a generic type.
 *
 * @param <T>
 *          the referenced type
 *
 * @since 3.1.0
 *
 * @author Simone Tripodi
 */
public abstract class TypeReference<T> {

  /**
   * 所有类型的通用超接口, 可以通过反射获取和操作类型信息
   * 例如A<T> extend TypeReference<T>
   * 在编译阶段, 字节码中只有A, <T>被擦除,
   * 但是在A加载时, 由于有继承会明确<T>的类型, 并存放在类元数据中(JVM内存)
   * 关键点在于继承, 继承时子类会明确其泛型类型
   */
  private final Type rawType;

  protected TypeReference() {
    rawType = getSuperclassTypeParameter(getClass());
  }

  Type getSuperclassTypeParameter(Class<?> clazz) {
    // 通过反射从父类中获取泛型的类型参数 <T>
    Type genericSuperclass = clazz.getGenericSuperclass();
    if (genericSuperclass instanceof Class) {
      // try to climb up the hierarchy until meet something useful
      // 如果父类不是 TypeReference 类型, 会一直查找到第一个带有泛型类型的类, 并将T返回
      if (TypeReference.class != genericSuperclass) {
        return getSuperclassTypeParameter(clazz.getSuperclass());
      }

      throw new TypeException("'" + getClass() + "' extends TypeReference but misses the type parameter. "
          + "Remove the extension or add a type parameter to it.");
    }
    // 获取 T
    Type rawType = ((ParameterizedType) genericSuperclass).getActualTypeArguments()[0];
    // TODO remove this when Reflector is fixed to return Types
    // 必须是泛型, 才获取 T
    if (rawType instanceof ParameterizedType) {
      rawType = ((ParameterizedType) rawType).getRawType();
    }

    return rawType;
  }

  public final Type getRawType() {
    return rawType;
  }

  @Override
  public String toString() {
    return rawType.toString();
  }

}
