package com.test.http_spring.service;

import com.test.http_spring.pojo.users;

import java.util.List;

public interface UserService {
    users getOneUserByName(String name);
    users getOneUserByToken(String token);
    void updateUserSetting(String data,String token);
    void updateUserToken(String token,String name);
    void updateUserIcon(String icon,String token);
    void updateUSerName(String name,String token);
    void createUser(users data);
    List<users> findAllUser();
    void removeOneUser(String username);
    void updataOneUser(users data);
    boolean checkUser(String token);

}
