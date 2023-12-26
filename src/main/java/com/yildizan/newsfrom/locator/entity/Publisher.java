package com.yildizan.newsfrom.locator.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;

import lombok.Data;

@Data
@Entity
public class Publisher {

    @Id
    private Integer id;

    private String name;

}
