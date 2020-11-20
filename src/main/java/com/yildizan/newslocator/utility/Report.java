package com.yildizan.newslocator.utility;

import com.yildizan.newslocator.entity.Feed;
import lombok.Data;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
public class Report {

    @NonNull private Feed feed;
    @NonNull private long start;
    private long finish;
    private boolean isSuccessful;
    private int located;
    private int matched;
    private int notMatched;

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
