package com.yildizan.newsfrom.locator.entity;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Id;

@Data
@Entity
public class Location {

    @Id
    private Integer id;

}
