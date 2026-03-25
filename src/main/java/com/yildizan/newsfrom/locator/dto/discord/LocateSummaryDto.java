package com.yildizan.newsfrom.locator.dto.discord;

import java.util.Objects;

import lombok.Data;

@Data
public class LocateSummaryDto {

    private long duration;
    private int count;
    private Exception exception;

    public boolean isSuccessful() {
        return Objects.isNull(exception);
    }

}
