package com.yildizan.newsfrom.locator.dto.openai;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class ChatResponseDto {

    private List<ChoiceDto> choices;

    @Data
    @NoArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class ChoiceDto {
        private ChatMessageDto message;
    }

}
