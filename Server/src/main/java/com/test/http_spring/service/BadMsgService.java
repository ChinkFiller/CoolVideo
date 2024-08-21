package com.test.http_spring.service;

import com.test.http_spring.pojo.BadMsg;

import java.util.List;

public interface BadMsgService {
    List<BadMsg> findAllBadMsg();
    void writeOneMsg(BadMsg msg);
    void deleteOneDanmu(int id,boolean isDelete);
}
