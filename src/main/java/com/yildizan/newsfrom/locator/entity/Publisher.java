package com.yildizan.newsfrom.locator.entity;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Id;

@Data
@Entity
public class Publisher {

    @Id
    private Integer id;

    private String name;

    public boolean isSputnik() {
        return id == 1;
    }

    public boolean isReuters() {
        return id == 2;
    }

    public boolean isBbc() {
        return id == 3;
    }

    public boolean isBuzzFeed() {
        return id == 4;
    }

    public boolean isWashingtonPost() {
        return id == 5;
    }

    public boolean isNewYorkTimes() {
        return id == 6;
    }

    public boolean isFoxNews() {
        return id == 7;
    }

}
