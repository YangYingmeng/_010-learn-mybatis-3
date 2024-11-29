/*
 *    Copyright 2009-2024 the original author or authors.
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
package org.apache.ibatis.cache.decorators;

import java.util.concurrent.locks.ReentrantLock;

import org.apache.ibatis.cache.Cache;

/**
 * @author Clinton Begin
 */
public class SynchronizedCache implements Cache {

  /**
   * 可重入锁
   */
  private final ReentrantLock lock = new ReentrantLock();
  /**
   * 装饰的 Cache 对象
   */
  private final Cache delegate;

  public SynchronizedCache(Cache delegate) {
    this.delegate = delegate;
  }

  @Override
  public String getId() {
    return delegate.getId();
  }

  @Override
  public int getSize() {
    // 同步
    lock.lock();
    try {
      return delegate.getSize();
    } finally {
      lock.unlock();
    }
  }

  @Override
  public void putObject(Object key, Object object) {
    // 同步
    lock.lock();
    try {
      delegate.putObject(key, object);
    } finally {
      lock.unlock();
    }
  }

  @Override
  public Object getObject(Object key) {
    // 同步
    lock.lock();
    try {
      return delegate.getObject(key);
    } finally {
      lock.unlock();
    }
  }

  @Override
  public Object removeObject(Object key) {
    // 同步
    lock.lock();
    try {
      return delegate.removeObject(key);
    } finally {
      lock.unlock();
    }
  }

  @Override
  public void clear() {
    lock.lock();
    try {
      delegate.clear();
    } finally {
      lock.unlock();
    }
  }

  @Override
  public int hashCode() {
    return delegate.hashCode();
  }

  @Override
  public boolean equals(Object obj) {
    return delegate.equals(obj);
  }

}
