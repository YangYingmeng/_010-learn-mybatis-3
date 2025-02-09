package yym.mybatis.book.chapter04.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import yym.mybatis.book.entity.UserEntity;

/**
 * @Author: Yym
 * @Version: 1.0
 * @Date: 2025/1/7 17:04
 */
public interface UserMapper {

  List<UserEntity> listAllUser();

  @Select("select * from user where id=#{userId,jdbcType=INTEGER}")
  UserEntity getUserById(@Param("userId") String userId);

  List<UserEntity> getUserByEntity(UserEntity user);

  UserEntity getUserByPhone(@Param("phone") String phone);
}
