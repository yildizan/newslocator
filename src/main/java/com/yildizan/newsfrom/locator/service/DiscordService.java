package com.yildizan.newsfrom.locator.service;

import com.yildizan.newsfrom.locator.client.DiscordClient;
import com.yildizan.newsfrom.locator.dto.SummaryDto;
import com.yildizan.newsfrom.locator.dto.discord.EmbedDto;
import com.yildizan.newsfrom.locator.dto.discord.ErrorFileDto;
import com.yildizan.newsfrom.locator.dto.discord.FieldDto;
import com.yildizan.newsfrom.locator.dto.discord.FooterDto;
import com.yildizan.newsfrom.locator.dto.discord.InfoDto;
import com.yildizan.newsfrom.locator.utility.DiscordEmojis;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
public class DiscordService {

    private final DiscordClient discordClient;

    @Value("${application.name}")
    private String applicationName;

    @Value("${application.url}")
    private String applicationUrl;

    public void notify(List<SummaryDto> summaries) {
        EmbedDto embed = new EmbedDto(applicationName, applicationUrl);
        long duration = 0L;

        for (SummaryDto summary : summaries) {
            duration += summary.getDuration();
            String publisherName = summary.getFeed()
                    .getPublisher()
                    .getName();

            if (!summary.isSuccessful()) {
                notify(summary.getException(), publisherName);
            }

            FieldDto field = new FieldDto();
            field.setName((summary.isSuccessful() ? DiscordEmojis.CHECK_MARK : DiscordEmojis.CROSS) + ' ' + publisherName);
            field.setValue(summary.getLocated() + " / " + summary.getMatched() + " / " + summary.getNotMatched());

            embed.getFields().add(field);
        }

        embed.setFooter(new FooterDto(duration + " ms"));
        InfoDto dto = new InfoDto(Collections.singletonList(embed));

        discordClient.notifyInfo(dto);
    }

    private void notify(Exception e, String publisherName) {
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        PrintStream ps = new PrintStream(os);
        e.printStackTrace(ps);

        discordClient.notifyError(new ErrorFileDto(os.toByteArray(), "files[0]", publisherName + ".txt"));
    }

}
