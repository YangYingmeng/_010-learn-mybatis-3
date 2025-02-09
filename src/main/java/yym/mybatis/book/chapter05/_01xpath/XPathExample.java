package yym.mybatis.book.chapter05._01xpath;

import java.io.InputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;

import org.apache.ibatis.builder.BuilderException;
import org.apache.ibatis.io.Resources;
import org.junit.Test;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

import yym.mybatis.book.entity.UserEntity;

/**
 * 使用 XPath 解析 xml
 *    1. 创建 Document 对象
 *    2. 创建执行 XPath 表达式的 XPath 对象
 *    3. 使用 XPath 解析表达式
 *
 * @Author: Yym
 * @Version: 1.0
 * @Date: 2025/1/11 17:23
 */
public class XPathExample {

  @Test
  public void testXPathParser() {
    try {
      // 创建DocumentBuilderFactory实例
      DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
      // 创建DocumentBuilder实例
      DocumentBuilder builder = factory.newDocumentBuilder();
      InputStream inputSource = Resources.getResourceAsStream("chapter05/Users.xml");
      Document doc = builder.parse(inputSource);
      // 获取XPath实例
      XPath xpath = XPathFactory.newInstance().newXPath();
      // 执行XPath表达式，获取节点信息
      NodeList nodeList = (NodeList) xpath.evaluate("/users/*", doc, XPathConstants.NODESET);
      List<UserEntity> userList = new ArrayList<>();
      for (int i = 1; i < nodeList.getLength() + 1; i++) {
        String path = "/users/user[" + i + "]";
        String id = (String) xpath.evaluate(path + "/@id", doc, XPathConstants.STRING);
        String name = (String) xpath.evaluate(path + "/name", doc, XPathConstants.STRING);
        String createTime = (String) xpath.evaluate(path + "/createTime", doc, XPathConstants.STRING);
        String passward = (String) xpath.evaluate(path + "/passward", doc, XPathConstants.STRING);
        String phone = (String) xpath.evaluate(path + "/phone", doc, XPathConstants.STRING);
        String nickName = (String) xpath.evaluate(path + "/nickName", doc, XPathConstants.STRING);
        // 调用buildUserEntity()方法，构建UserEntity对象
        UserEntity userEntity = buildUserEntity(Long.valueOf(id), name, createTime, passward, phone, nickName);
        userList.add(userEntity);
      }
      System.out.println(userList);
    } catch (Exception e) {
      throw new BuilderException("Error creating document instance.  Cause: " + e, e);
    }
  }

  private UserEntity buildUserEntity(Long id, String name,
                                     String createTime, String passward,
                                     String phone, String nickName) throws ParseException {
    UserEntity userEntity = new UserEntity();
    userEntity.setId(id);
    userEntity.setName(name);
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    Date date = sdf.parse(createTime);
    userEntity.setCreateTime(date);
    userEntity.setPassword(passward);
    userEntity.setPhone(phone);
    userEntity.setNickName(nickName);
    return userEntity;
  }
}
