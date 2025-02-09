package yym.mybatis.book.entity;

import java.util.Date;
import java.util.List;

import lombok.Data;


/**
 * @Author: Yym
 * @Version: 1.0
 * @Date: 2025/1/7 17:04
 */
@Data
public class UserEntity {

  private Long id;
  private String name;
  private Date createTime;
  private String password;
  private String phone;
  private String nickName;

  private List<Order> orders;

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public Date getCreateTime() {
    return createTime;
  }

  public void setCreateTime(Date createTime) {
    this.createTime = createTime;
  }

  public String getPassword() {
    return password;
  }

  public void setPassword(String password) {
    this.password = password;
  }

  public String getPhone() {
    return phone;
  }

  public void setPhone(String phone) {
    this.phone = phone;
  }

  public String getNickName() {
    return nickName;
  }

  public void setNickName(String nickName) {
    this.nickName = nickName;
  }

  public List<Order> getOrders() {
    return orders;
  }

  public void setOrders(List<Order> orders) {
    this.orders = orders;
  }

  public UserEntity(Long id, String name, Date createTime, String password, String phone, String nickName, List<Order> orders) {
    this.id = id;
    this.name = name;
    this.createTime = createTime;
    this.password = password;
    this.phone = phone;
    this.nickName = nickName;
    this.orders = orders;
  }

  /**
   * 供fastJson使用
   */
  public UserEntity() {
  }

  @Override
  public String toString() {
    return "UserEntity{" +
      "id=" + id +
      ", name='" + name + '\'' +
      ", createTime=" + createTime +
      ", password='" + password + '\'' +
      ", phone='" + phone + '\'' +
      ", nickName='" + nickName + '\'' +
      ", orders=" + orders +
      '}';
  }
}
