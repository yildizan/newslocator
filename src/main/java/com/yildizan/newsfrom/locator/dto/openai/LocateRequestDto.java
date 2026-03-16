package com.yildizan.newsfrom.locator.dto.openai;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LocateRequestDto {

    private long id;
    private String title;
    private String description;

}
