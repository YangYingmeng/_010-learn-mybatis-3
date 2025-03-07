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
package org.apache.ibatis.io;

import java.io.InputStream;
import java.net.URL;

/**
 * A class to wrap access to multiple class loaders making them work as one
 *
 * @author Clinton Begin
 */
public class ClassLoaderWrapper {

  /**
   * 默认 ClassLoader 对象,
   * 可通过ClassLoaderWrapper.defaultClassLoader = xxx 的方式初始化
   */
  ClassLoader defaultClassLoader;
  /**
   * 系统 ClassLoader 对象, 由构造方法初始化
   */
  ClassLoader systemClassLoader;

  ClassLoaderWrapper() {
    try {
      systemClassLoader = ClassLoader.getSystemClassLoader();
    } catch (SecurityException ignored) {
      // AccessControlException on Google App Engine
    }
  }

  /**
   * Get a resource as a URL using the current class path
   *
   * @param resource
   *          - the resource to locate
   *
   * @return the resource or null
   */
  public URL getResourceAsURL(String resource) {
    return getResourceAsURL(resource, getClassLoaders(null));
  }

  /**
   * Get a resource from the classpath, starting with a specific class loader
   *
   * @param resource
   *          - the resource to find
   * @param classLoader
   *          - the first classloader to try
   *
   * @return the stream or null
   */
  public URL getResourceAsURL(String resource, ClassLoader classLoader) {
    return getResourceAsURL(resource, getClassLoaders(classLoader));
  }

  /**
   * Get a resource from the classpath
   *
   * @param resource
   *          - the resource to find
   *
   * @return the stream or null
   */
  public InputStream getResourceAsStream(String resource) {
    return getResourceAsStream(resource, getClassLoaders(null));
  }

  /**
   * Get a resource from the classpath, starting with a specific class loader
   *
   * @param resource
   *          - the resource to find
   * @param classLoader
   *          - the first class loader to try
   *
   * @return the stream or null
   */
  public InputStream getResourceAsStream(String resource, ClassLoader classLoader) {
    return getResourceAsStream(resource, getClassLoaders(classLoader));
  }

  /**
   * Find a class on the classpath (or die trying)
   *
   * @param name
   *          - the class to look for
   *
   * @return - the class
   *
   * @throws ClassNotFoundException
   *           Duh.
   */
  public Class<?> classForName(String name) throws ClassNotFoundException {
    return classForName(name, getClassLoaders(null));
  }

  /**
   * Find a class on the classpath, starting with a specific classloader (or die trying)
   *
   * @param name
   *          - the class to look for
   * @param classLoader
   *          - the first classloader to try
   *
   * @return - the class
   *
   * @throws ClassNotFoundException
   *           Duh.
   */
  public Class<?> classForName(String name, ClassLoader classLoader) throws ClassNotFoundException {
    return classForName(name, getClassLoaders(classLoader));
  }

  /**
   * Try to get a resource from a group of classloaders
   *
   * @param resource
   *          - the resource to get
   * @param classLoader
   *          - the classloaders to examine
   *
   * @return the resource or null
   */
  InputStream getResourceAsStream(String resource, ClassLoader[] classLoader) {
    // 遍历 classLoader 数组
    for (ClassLoader cl : classLoader) {
      if (null != cl) {

        // try to find the resource as passed
        // 获得不带 / 的InputStream
        InputStream returnValue = cl.getResourceAsStream(resource);

        // now, some class loaders want this leading "/", so we'll add it and try again if we didn't find the resource
        // 获得带 / 的InputStream
        if (null == returnValue) {
          returnValue = cl.getResourceAsStream("/" + resource);
        }
        // 返回结果
        if (null != returnValue) {
          return returnValue;
        }
      }
    }
    return null;
  }

  /**
   * Get a resource as a URL using the current class path
   *
   * @param resource
   *          - the resource to locate
   * @param classLoader
   *          - the class loaders to examine
   *
   * @return the resource or null
   */
  URL getResourceAsURL(String resource, ClassLoader[] classLoader) {

    URL url;
    // 遍历classLoader数组
    for (ClassLoader cl : classLoader) {

      if (null != cl) {
        // 获得不带 / 的 URL
        // look for the resource as passed in...
        url = cl.getResource(resource);
        // 获得带 / 的 URL
        // ...but some class loaders want this leading "/", so we'll add it
        // and try again if we didn't find the resource
        if (null == url) {
          url = cl.getResource("/" + resource);
        }

        // "It's always in the last place I look for it!"
        // ... because only an idiot would keep looking for it after finding it, so stop looking already.
        if (null != url) {
          return url;
        }

      }

    }

    // didn't find it anywhere.
    return null;

  }

  /**
   * Attempt to load a class from a group of classloaders
   *
   * @param name
   *          - the class to load
   * @param classLoader
   *          - the group of classloaders to examine
   *
   * @return the class
   *
   * @throws ClassNotFoundException
   *           - Remember the wisdom of Judge Smails: Well, the world needs ditch diggers, too.
   */
  Class<?> classForName(String name, ClassLoader[] classLoader) throws ClassNotFoundException {

    // 遍历 classLoader 数组
    for (ClassLoader cl : classLoader) {

      if (null != cl) {

        try {
          // 知道有一成功类找到, 返回
          return Class.forName(name, true, cl);

        } catch (ClassNotFoundException e) {
          // we'll ignore this until all classloaders fail to locate the class
        }

      }

    }

    throw new ClassNotFoundException("Cannot find class: " + name);

  }

  ClassLoader[] getClassLoaders(ClassLoader classLoader) {
    return new ClassLoader[] { classLoader, defaultClassLoader, Thread.currentThread().getContextClassLoader(),
        getClass().getClassLoader(), systemClassLoader };
  }

}
