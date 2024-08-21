package com.test.http_spring.mapper;
import com.test.http_spring.pojo.film_data;
import com.test.http_spring.pojo.weak;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface WeekMapper {
    List<film_data> findAllWeekData(String date);
    List<weak> findAllWeekDataNoDay();
}
