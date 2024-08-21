package com.test.http_spring.pojo;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class film_data {
    Integer id;
    String name;
    String state;
    String leader;
    String actor;
    String img_url;
    Integer agree;
    String danmu;
}
