package com.yildizan.newsfrom.locator.entity;

import javax.persistence.Entity;
import javax.persistence.Id;

import lombok.Data;

@Data
@Entity
public class Publisher {

    @Id
    private Integer id;

    private String name;

    public boolean isSputnik() {
        return name.equalsIgnoreCase("sputnik");
    }

    public boolean isReuters() {
        return name.equalsIgnoreCase("reuters");
    }

    public boolean isBbc() {
        return name.equalsIgnoreCase("bbc");
    }

    public boolean isBuzzFeed() {
        return name.equalsIgnoreCase("buzzfeed");
    }

    public boolean isWashingtonPost() {
        return name.equalsIgnoreCase("washington post");
    }

    public boolean isNewYorkTimes() {
        return name.equalsIgnoreCase("new york times");
    }

    public boolean isFoxNews() {
        return name.equalsIgnoreCase("fox news");
    }

}
