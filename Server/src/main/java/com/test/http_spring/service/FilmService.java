package com.test.http_spring.service;

import com.test.http_spring.pojo.film_data;
import com.test.http_spring.pojo.weak;
import org.apache.ibatis.annotations.Param;
import org.json.JSONArray;

import java.util.List;
import java.util.Map;


public interface FilmService {
    List<film_data> findAllFilm();
    List<film_data> findFilmLikeByName(String info);
    film_data findFilmDataById(int id);
    void addOneFilmAgree(int id);
    void removeOneFilmAgree(int id);
    void removeOneFilm(int id);
    void addOneFilm(film_data data);
    void updateData(film_data data);
    void bootAutoGetter();
    JSONArray getDanmu(int id, String part);
    boolean DeleteDanmu(int id,String part,int danmuID);
    boolean setDanmu(Map data);
    Map getOneDanmu(int filmid,int danmuID,String part);
}
