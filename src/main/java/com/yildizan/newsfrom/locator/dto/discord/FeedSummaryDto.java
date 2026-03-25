package com.yildizan.newsfrom.locator.dto.discord;

import java.util.Objects;

import com.yildizan.newsfrom.locator.entity.Feed;
import lombok.Data;

@Data
public class FeedSummaryDto {

    private Feed feed;
    private long duration;
    private int count;
    private Exception exception;

    public boolean isSuccessful() {
        return Objects.isNull(exception);
    }

}
