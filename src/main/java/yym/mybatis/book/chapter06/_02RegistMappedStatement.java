package yym.mybatis.book.chapter06;

import org.apache.ibatis.builder.xml.XMLConfigBuilder;
import org.apache.ibatis.parsing.XNode;

/**
 * SQL 配置信息的注册
 * 通过 {@link org.apache.ibatis.session.Configuration#addMappedStatement}的 mappedStatements 属性注册所有的 MappedStatement 属性
 * MyBatis 主配置文件的解析通过 {@link org.apache.ibatis.builder.xml.XMLConfigBuilder#parseConfiguration(XNode)} 完成
 * 标签的解析通过 {@link org.apache.ibatis.builder.xml.XMLConfigBuilder#mappersElement(XNode)} 完成, 获取所有子标签 根据不同的标签做不同的处理
 * 具体的解析方法参考{@link org.apache.ibatis.builder.xml.XMLMapperBuilder#parse()}
 *
 *
 * @Author: Yym
 * @Version: 1.0
 * @Date: 2025/2/6 11:00
 */
public class _02RegistMappedStatement {
}
