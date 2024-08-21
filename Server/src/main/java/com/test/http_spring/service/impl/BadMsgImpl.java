package com.test.http_spring.service.impl;

import com.test.http_spring.mapper.BadMsgMapper;
import com.test.http_spring.pojo.BadMsg;
import com.test.http_spring.service.BadMsgService;
import com.test.http_spring.service.FilmService;
import com.test.http_spring.utils.ToolsFunction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.awt.image.TileObserver;
import java.util.List;

@Service
public class BadMsgImpl implements BadMsgService {
    @Autowired
    BadMsgMapper badMsgMapper;
    @Autowired
    FilmService filmService;


    @Override
    public List<BadMsg> findAllBadMsg() {
        return badMsgMapper.findAllBadMsg();
    }

    @Override
    public void writeOneMsg(BadMsg data) {
        BadMsg ordata=badMsgMapper.selectMsgByMsgs(data);
        if (ordata==null){
            badMsgMapper.writeOneMsg(data);
        }else{
            if (ordata.getUser().contains(data.getUser())){
                return;
            }
            data.setUser(ordata.getUser()+","+data.getUser());
            badMsgMapper.updateData(data);
        }
    }

    @Override
    public void deleteOneDanmu(int id,boolean isDelete){
        BadMsg data=badMsgMapper.selectMsgById(id);
        if (isDelete){
            if (filmService.DeleteDanmu(data.getFilmid(),data.getPart(),data.getDanmuid())){
                badMsgMapper.deleteOneDanmu(id);
            }else{
                throw new RuntimeException("Delete Danmu Error");
            }
        }else{
            badMsgMapper.deleteOneDanmu(id);
        }
    }
}
