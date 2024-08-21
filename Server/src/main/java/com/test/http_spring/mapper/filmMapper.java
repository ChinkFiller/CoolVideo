package com.test.http_spring.mapper;
import com.test.http_spring.pojo.film_data;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface filmMapper {
    List<film_data> findAllFilm();

    List<film_data> findFilmLikeByName(String info);
    film_data findFilmDataById(int id);
    void addOneFilmAgree(int id);
    void removeOneFilmAgree(int id);
    void removeOneFilm(int id);
    void addOneFilm(film_data data);
    void updateData(film_data data);
    String getDanmu(int id);
    void setDanmu(@Param(value = "id") int id,@Param(value = "data") String data);
}