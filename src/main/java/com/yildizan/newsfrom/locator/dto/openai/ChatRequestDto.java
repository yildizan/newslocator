package com.yildizan.newsfrom.locator.dto.openai;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChatRequestDto {

    private List<ChatMessageDto> messages;

}
