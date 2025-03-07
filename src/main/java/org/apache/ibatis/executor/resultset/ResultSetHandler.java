/*
 *    Copyright 2009-2022 the original author or authors.
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
package org.apache.ibatis.executor.resultset;

import java.sql.CallableStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

import org.apache.ibatis.cursor.Cursor;

/**
 * @author Clinton Begin
 */
public interface ResultSetHandler {

  /**
   * 获取 Statement 对象中的ResultSet 对象, 对ResultSet对象进行处理返回结果集
   */
  <E> List<E> handleResultSets(Statement stmt) throws SQLException;

  /**
   * 将 Result对象包装成 Cursor 对象, 对 Cursor 遍历时可以动态的从数据库查询数据, 避免一次性将数据加载到内存
   */
  <E> Cursor<E> handleCursorResultSets(Statement stmt) throws SQLException;

  /**
   * 处理存储过程调用结果
   */
  void handleOutputParameters(CallableStatement cs) throws SQLException;

}
