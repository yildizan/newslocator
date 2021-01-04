package com.yildizan.newslocator.utility;

import com.yildizan.newslocator.entity.Feed;
import lombok.Data;

@Data
public class Summary {

    private Feed feed;
    private long start;
    private long finish;
    private boolean isSuccessful;
    private int located;
    private int matched;
    private int notMatched;

    public Summary(Feed feed, long start) {
        this.feed = feed;
        this.start = start;
    }

    public void incrementLocated() {
        this.located++;
    }

    public void incrementMatched() {
        this.matched++;
    }

    public void incrementNotMatched() {
        this.notMatched++;
    }

    public long getDuration() {
        return this.finish - this.start;
    }

}
