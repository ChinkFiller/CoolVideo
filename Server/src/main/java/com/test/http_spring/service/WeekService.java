package com.test.http_spring.service;

import com.test.http_spring.pojo.film_data;
import com.test.http_spring.pojo.weak;

import java.util.List;

public interface WeekService {
    List<film_data> findAllWeekData(String date);
    List<weak> findAllWeekDataNoDay();
}
