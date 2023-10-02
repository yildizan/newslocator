package com.yildizan.newsfrom.locator.entity;

import lombok.Data;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;

@Data
@Entity
public class Location {

    @Id
    private Integer id;

}
