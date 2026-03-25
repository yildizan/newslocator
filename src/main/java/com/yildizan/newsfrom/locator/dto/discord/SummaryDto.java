package com.yildizan.newsfrom.locator.dto.discord;

import java.util.ArrayList;
import java.util.List;

import lombok.Data;

@Data
public class SummaryDto {

    private List<FeedSummaryDto> feeds = new ArrayList<>();
    private LocateSummaryDto locate;

    public boolean isSuccessful() {
        boolean feedsOk = feeds.stream().allMatch(FeedSummaryDto::isSuccessful);
        boolean locateOk = locate == null || locate.isSuccessful();
        return feedsOk && locateOk;
    }

}
