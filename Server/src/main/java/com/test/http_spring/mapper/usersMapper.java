package com.test.http_spring.mapper;

import java.util.List;
import com.test.http_spring.pojo.users;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface usersMapper {
    List<users> findAllUser();
    users getOneUserByName(String name);
    users getOneUserByToken(String token);
    void updateUserSetting(String data,String token);
    void updateUserToken(String token,String name);
    void updateUserIcon(String icon,String token);
    void updateUserName(String name,String token);
    void createUser(users data);
    void removeOneUser(String username);
    void updateOneUser(users data);
    void setUserTimes(String token);
    void resetSpeedTime();
}
