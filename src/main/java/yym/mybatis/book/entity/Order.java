package yym.mybatis.book.entity;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

/**
 * @Author: Yym
 * @Version: 1.0
 * @Date: 2025/1/7 9:55
 */
@Data
@Getter
@Setter
public class Order {

  private String orderNo;
  private String goodsName;

  public Order(String orderNo, String goodsName) {
    this.orderNo = orderNo;
    this.goodsName = goodsName;
  }
}
