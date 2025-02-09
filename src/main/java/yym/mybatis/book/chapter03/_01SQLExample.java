package yym.mybatis.book.chapter03;

import static org.junit.Assert.assertEquals;

import org.apache.ibatis.jdbc.SQL;
import org.junit.Test;

/**
 * SQL工具类:
 *    MyBatis 提供了一种工具类, 帮助我们动态拼接sql
 *    SQL继承抽象父类AbstractSQL, 大部分功能由AbstractSQL完成, 重写了toString方法, 调用SQLStatement的sql()方法生成sql字符串
 *      1. UPDATE 等一系列构造sql的方法
 *      2. SQLStatement SQL语句的描述
 *          2.1 StatementType: 确定sql语句的类型
 *          2.2 维护了很多List数组, 记录方法的参数
 *          2.3 sql()方法, 抽象父类重写了toString(), toString中调用了sql()方法用来组装sql
 *              根据 statementType 的不同, 调用不同的执行方法, 以update()为例
 *                  1. 调用sqlClause()方法组装sql
 *
 * @Author: Yym
 * @Version: 1.0
 * @Date: 2025/1/6 10:09
 */
public class _01SQLExample {

  @Test
  public void testSelectSQL() {
    // 原生sql
    String orgSql = "SELECT P.ID, P.USERNAME, P.PASSWORD, P.FULL_NAME, P.LAST_NAME, P.CREATED_ON, P.UPDATED_ON\n" +
      "FROM PERSON P, ACCOUNT A\n" +
      "INNER JOIN DEPARTMENT D on D.ID = P.DEPARTMENT_ID\n" +
      "INNER JOIN COMPANY C on D.COMPANY_ID = C.ID\n" +
      "WHERE (P.ID = A.ID AND P.FIRST_NAME like ?) \n" +
      "OR (P.LAST_NAME like ?)\n" +
      "GROUP BY P.ID\n" +
      "HAVING (P.LAST_NAME like ?) \n" +
      "OR (P.FIRST_NAME like ?)\n" +
      "ORDER BY P.ID, P.FULL_NAME";

    // MyBatis的SQL工具生成sql
    String newSql =  new SQL() {{
      SELECT("P.ID, P.USERNAME, P.PASSWORD, P.FULL_NAME");
      SELECT("P.LAST_NAME, P.CREATED_ON, P.UPDATED_ON");
      FROM("PERSON P");
      FROM("ACCOUNT A");
      INNER_JOIN("DEPARTMENT D on D.ID = P.DEPARTMENT_ID");
      INNER_JOIN("COMPANY C on D.COMPANY_ID = C.ID");
      WHERE("P.ID = A.ID");
      WHERE("P.FIRST_NAME like ?");
      OR();
      WHERE("P.LAST_NAME like ?");
      GROUP_BY("P.ID");
      HAVING("P.LAST_NAME like ?");
      OR();
      HAVING("P.FIRST_NAME like ?");
      ORDER_BY("P.ID");
      ORDER_BY("P.FULL_NAME");
    }}.toString();

    assertEquals(orgSql, newSql);
  }

  @Test
  public void testDynamicSQL() {
    selectPerson(null,null,null);
  }

  /**
   * 动态sql拼接
   */
  public String selectPerson(final String id, final String firstName, final String lastName) {
    return new SQL() {{
      SELECT("P.ID, P.USERNAME, P.PASSWORD");
      SELECT("P.FIRST_NAME, P.LAST_NAME");
      FROM("PERSON P");
      if (id != null) {
        WHERE("P.ID = #{id}");
      }
      if (firstName != null) {
        WHERE("P.FIRST_NAME = #{firstName}");
      }
      if (lastName != null) {
        WHERE("P.LAST_NAME = #{lastName}");
      }
      ORDER_BY("P.LAST_NAME");
    }}.toString();
  }

  @Test
  public  void testInsertSql() {
    String insertSql = new SQL().
      INSERT_INTO("PERSON").
      VALUES("ID, FIRST_NAME", "#{id}, #{firstName}").
      VALUES("LAST_NAME", "#{lastName}").toString();
    System.out.println(insertSql);
  }

  @Test
  public void  testDeleteSql() {
    String deleteSql =  new SQL() {{
      DELETE_FROM("PERSON");
      WHERE("ID = #{id}");
    }}.toString();
    System.out.println(deleteSql);
  }

  @Test
  public void testUpdateSql() {
    String updateSql =  new SQL() {{
      UPDATE("PERSON");
      SET("FIRST_NAME = #{firstName}");
      WHERE("ID = #{id}");
    }}.toString();
    System.out.println(updateSql);
  }
}
