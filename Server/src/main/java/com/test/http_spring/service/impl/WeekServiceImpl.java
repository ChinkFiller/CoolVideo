package com.test.http_spring.service.impl;
import com.test.http_spring.mapper.WeekMapper;
import com.test.http_spring.pojo.film_data;
import com.test.http_spring.pojo.weak;
import com.test.http_spring.service.WeekService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class WeekServiceImpl implements WeekService{


    @Autowired
    WeekMapper week;


    @Override
    public List<film_data> findAllWeekData(String date) {
        return week.findAllWeekData(date);
    }

    @Override
    public List<weak> findAllWeekDataNoDay() {
        return week.findAllWeekDataNoDay();
    }
}
