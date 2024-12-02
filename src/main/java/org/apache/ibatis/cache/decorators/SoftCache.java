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

import java.lang.ref.ReferenceQueue;
import java.lang.ref.SoftReference;
import java.util.Deque;
import java.util.LinkedList;
import java.util.concurrent.locks.ReentrantLock;

import org.apache.ibatis.cache.Cache;

/**
 * Soft Reference cache decorator.
 * <p>
 * Thanks to Dr. Heinz Kabutz for his guidance here.
 *
 * @author Clinton Begin
 */
public class SoftCache implements Cache {
  /**
   * 强引用的键的队列
   */
  private final Deque<Object> hardLinksToAvoidGarbageCollection;
  /**
   * 被 GC 回收的 WeakEntry 集合, 当软引用被回收时, 会将回收对象加入队列, 方便判断哪些对象被回收或者维护缓存的一致性
   */
  private final ReferenceQueue<Object> queueOfGarbageCollectedEntries;
  /**
   * 装饰的cache
   */
  private final Cache delegate;
  /**
   * {@link #hardLinksToAvoidGarbageCollection} 的大小
   */
  private int numberOfHardLinks;
  private final ReentrantLock lock = new ReentrantLock();

  public SoftCache(Cache delegate) {
    this.delegate = delegate;
    this.numberOfHardLinks = 256;
    this.hardLinksToAvoidGarbageCollection = new LinkedList<>();
    this.queueOfGarbageCollectedEntries = new ReferenceQueue<>();
  }

  @Override
  public String getId() {
    return delegate.getId();
  }

  @Override
  public int getSize() {
    removeGarbageCollectedItems();
    return delegate.getSize();
  }

  public void setSize(int size) {
    this.numberOfHardLinks = size;
  }

  @Override
  public void putObject(Object key, Object value) {
    // 移除已经被 GC 回收的 SoftEntry
    removeGarbageCollectedItems();
    delegate.putObject(key, new SoftEntry(key, value, queueOfGarbageCollectedEntries));
  }

  @Override
  public Object getObject(Object key) {
    Object result = null;
    @SuppressWarnings("unchecked") // assumed delegate cache is totally managed by this cache
    // 获得值的 WeakReference 对象
    SoftReference<Object> softReference = (SoftReference<Object>) delegate.getObject(key);
    if (softReference != null) {
      // 获得值
      result = softReference.get();
      // 为 null, 意味着已经被GC清除, 从delegate中删除
      if (result == null) {
        delegate.removeObject(key);
      } else {
        // See #586 (and #335) modifications need more than a read lock
        lock.lock();
        try {
          hardLinksToAvoidGarbageCollection.addFirst(result);
          if (hardLinksToAvoidGarbageCollection.size() > numberOfHardLinks) {
            hardLinksToAvoidGarbageCollection.removeLast();
          }
        } finally {
          lock.unlock();
        }
      }
    }
    return result;
  }

  @Override
  public Object removeObject(Object key) {
    removeGarbageCollectedItems();
    @SuppressWarnings("unchecked")
    SoftReference<Object> softReference = (SoftReference<Object>) delegate.removeObject(key);
    return softReference == null ? null : softReference.get();
  }

  @Override
  public void clear() {
    lock.lock();
    try {
      hardLinksToAvoidGarbageCollection.clear();
    } finally {
      lock.unlock();
    }
    // 移除已经被gc回收的对象
    removeGarbageCollectedItems();
    delegate.clear();
  }

  private void removeGarbageCollectedItems() {
    // 通过垃圾回收队列 保证缓存和回收对象的一致性
    SoftEntry sv;
    while ((sv = (SoftEntry) queueOfGarbageCollectedEntries.poll()) != null) {
      delegate.removeObject(sv.key);
    }
  }

  private static class SoftEntry extends SoftReference<Object> {
    private final Object key;

    SoftEntry(Object key, Object value, ReferenceQueue<Object> garbageCollectionQueue) {
      super(value, garbageCollectionQueue);
      this.key = key;
    }
  }

}
