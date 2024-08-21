package com.test.http_spring.mapper;
import com.test.http_spring.pojo.BadMsg;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;


@Mapper
public interface BadMsgMapper {
    List<BadMsg> findAllBadMsg();
    void writeOneMsg(BadMsg msg);
    void updateData(BadMsg data);
    void deleteOneDanmu(int id);
    BadMsg selectMsgById(int id);
    BadMsg selectMsgByMsgs(BadMsg data);

}
