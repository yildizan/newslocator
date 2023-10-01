package com.yildizan.newsfrom.locator.dto;

import java.util.Objects;

import com.yildizan.newsfrom.locator.entity.Feed;
import lombok.Data;

@Data
public class SummaryDto {

    private Feed feed;
    private long start;
    private long finish;
    private int located;
    private int matched;
    private int none;
    private Exception exception;

    public SummaryDto(Feed feed, long start) {
        this.feed = feed;
        this.start = start;
    }

    public void incrementLocated() {
        this.located++;
    }

    public void incrementMatched() {
        this.matched++;
    }

    public void incrementNone() {
        this.none++;
    }

    public long getDuration() {
        return this.finish - this.start;
    }

    public boolean isSuccessful() {
        return Objects.isNull(exception);
    }

}
