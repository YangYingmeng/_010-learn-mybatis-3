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
package org.apache.ibatis.executor.statement;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

import org.apache.ibatis.cursor.Cursor;
import org.apache.ibatis.executor.parameter.ParameterHandler;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.session.ResultHandler;

/**
 * @author Clinton Begin
 */
public interface StatementHandler {

  /**
   * 创建 JDBC Statement 对象, 并完成 Statement对象的属性设置
   */
  Statement prepare(Connection connection, Integer transactionTimeout) throws SQLException;

  /**
   * 使用 ParameterHandler 替参数占位符设置值
   */
  void parameterize(Statement statement) throws SQLException;

  /**
   * 将 SQL 命令添加到批处量执行列表中
   */
  void batch(Statement statement) throws SQLException;

  int update(Statement statement) throws SQLException;

  <E> List<E> query(Statement statement, ResultHandler resultHandler) throws SQLException;

  /**
   * 带 游标 的查询
   */
  <E> Cursor<E> queryCursor(Statement statement) throws SQLException;

  /**
   * 获取 Mapper 中配置的 SQL 信息
   */
  BoundSql getBoundSql();

  /**
   * 获取 ParameterHandler 实例
   */
  ParameterHandler getParameterHandler();

}
