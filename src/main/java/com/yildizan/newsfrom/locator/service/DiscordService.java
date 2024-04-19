package com.yildizan.newsfrom.locator.service;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.yildizan.newsfrom.locator.client.DiscordClient;
import com.yildizan.newsfrom.locator.dto.SummaryDto;
import com.yildizan.newsfrom.locator.dto.discord.EmbedDto;
import com.yildizan.newsfrom.locator.dto.discord.ErrorFileDto;
import com.yildizan.newsfrom.locator.dto.discord.FieldDto;
import com.yildizan.newsfrom.locator.dto.discord.FooterDto;
import com.yildizan.newsfrom.locator.dto.discord.InfoDto;
import com.yildizan.newsfrom.locator.utility.DiscordEmojis;

import lombok.RequiredArgsConstructor;

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
        AtomicLong duration = new AtomicLong(0L);

        summaries.parallelStream().forEach(summary -> {
            duration.addAndGet(summary.getDuration());
            String publisherName = summary.getFeed()
                    .getPublisher()
                    .getName();

            if (!summary.isSuccessful()) {
                notify(summary.getException(), publisherName);
            }

            String fieldName = (summary.isSuccessful() ? DiscordEmojis.CHECK_MARK : DiscordEmojis.CROSS) + ' ' + publisherName;
            String fieldValue = summary.getLocated() + " / " + summary.getMatched() + " / " + summary.getNone();
            FieldDto field = new FieldDto(fieldName, fieldValue);
            embed.getFields().add(field);
        });

        FooterDto footer = new FooterDto(duration + " ms");
        embed.setFooter(footer);

        embed.getFields().sort(Comparator.comparing(FieldDto::name));

        InfoDto dto = new InfoDto(Collections.singletonList(embed));
        discordClient.notifyInfo(dto);
    }

    private void notify(Exception e, String publisherName) {
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        PrintStream ps = new PrintStream(os);
        e.printStackTrace(ps);

        ErrorFileDto dto = new ErrorFileDto(os.toByteArray(), "files[0]", publisherName + ".txt");
        discordClient.notifyError(dto);
    }

}
