package com.test.http_spring.service.impl;

import com.test.http_spring.mapper.usersMapper;
import com.test.http_spring.pojo.users;
import com.test.http_spring.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
public class UserServiceImpl implements UserService {
    @Autowired
    usersMapper usersmaper;


    @Override
    public users getOneUserByName(String name) {
        return usersmaper.getOneUserByName(name);
    }

    @Override
    public users getOneUserByToken(String token) {
        return usersmaper.getOneUserByToken(token);
    }

    @Override
    public void updateUserSetting(String data, String token) {
      usersmaper.updateUserSetting(data,token);
    }

    @Override
    public void updateUserToken(String token, String name) {
        usersmaper.updateUserToken(token,name);
    }

    @Override
    public void updateUserIcon(String icon, String token) {
        usersmaper.updateUserIcon(icon,token);
    }

    @Override
    public void updateUSerName(String name,String token) {
        usersmaper.updateUserName(name,token);
    }

    @Override
    public void createUser(users data) {
        usersmaper.createUser(data);
    }

    @Override
    public List<users> findAllUser() {
        return usersmaper.findAllUser();
    }

    @Override
    public void removeOneUser(String username) {
        usersmaper.removeOneUser(username);
    }

    @Override
    public void updataOneUser(users data) {
        usersmaper.updateOneUser(data);
    }

    @Override
    public boolean checkUser(String token) {
        if (usersmaper.getOneUserByToken(token)==null){
            return false;
        }else {
            return true;
        }
    }
}
