package com.test.http_spring.service.impl;

import com.test.http_spring.mapper.usersMapper;
import com.test.http_spring.pojo.users;
import com.test.http_spring.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.test.http_spring.utils.GlobalValue;

import java.util.Iterator;
import java.util.List;
import java.util.Map;


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
        if (GlobalValue.tokenList.get(token)==null){
            return null;
        }
        return usersmaper.getOneUserByToken(GlobalValue.tokenList.get(token));
    }

    @Override
    public void updateUserSetting(String data, String token) {
        if (GlobalValue.tokenList.get(token)!=null){
            usersmaper.updateUserSetting(data,GlobalValue.tokenList.get(token));
        }
    }

    @Override
    public void updateUserToken(String token, String name) {
        // 使用迭代器遍历 Map 以删除具有特定值的条目
        Iterator<Map.Entry<String, String>> iterator = GlobalValue.tokenList.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<String, String> entry = iterator.next();
            if (entry.getValue().equals(name)) {
                iterator.remove();
                break;
            }
        }
        GlobalValue.tokenList.put(token,name);
    }

    @Override
    public void updateUserIcon(String icon, String token) {
        if (GlobalValue.tokenList.get(token)!=null){
            usersmaper.updateUserIcon(icon,GlobalValue.tokenList.get(token));
        }
    }

    @Override
    public void updateUSerName(String name,String token) {
        if (GlobalValue.tokenList.get(token)!=null){
            usersmaper.updateUserName(name,GlobalValue.tokenList.get(token));
        }
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
        removeToken(username);
        usersmaper.removeOneUser(username);
    }

    @Override
    public void updataOneUser(users data) {
        usersmaper.updateOneUser(data);
    }

    @Override
    public boolean checkUser(String token) {
        if (GlobalValue.tokenList.get(token)==null){
            return false;
        }else {
            return true;
        }
    }

    @Override
    public void setUserTimes(String token) {
        if (GlobalValue.tokenList.get(token)!=null){
            usersmaper.setUserTimes(GlobalValue.tokenList.get(token));
        }
    }

    @Override
    public void resetSpeedTime() {
        usersmaper.resetSpeedTime();
    }

    @Override
    public int removeToken(String user) {
        Iterator<Map.Entry<String, String>> iterator = GlobalValue.tokenList.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<String, String> entry = iterator.next();
            if (entry.getValue().equals(user)) {
                iterator.remove();
                return 1;
            }
        }
        return 0;
    }
}
