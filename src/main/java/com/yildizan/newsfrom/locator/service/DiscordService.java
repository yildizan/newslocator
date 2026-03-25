package com.yildizan.newsfrom.locator.service;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.Collections;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.yildizan.newsfrom.locator.client.DiscordClient;
import com.yildizan.newsfrom.locator.dto.discord.EmbedDto;
import com.yildizan.newsfrom.locator.dto.discord.ErrorFileDto;
import com.yildizan.newsfrom.locator.dto.discord.FooterDto;
import com.yildizan.newsfrom.locator.dto.discord.InfoDto;
import com.yildizan.newsfrom.locator.dto.discord.LocateSummaryDto;
import com.yildizan.newsfrom.locator.dto.discord.SummaryDto;
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

    public void notify(SummaryDto summary) {
        EmbedDto embed = new EmbedDto(applicationName, applicationUrl);
        long totalDuration = 0L;
        StringBuilder description = new StringBuilder();

        description.append("**Read feeds:**\n");
        for (var feedSummary : summary.getFeeds()) {
            totalDuration += feedSummary.getDuration();
            String publisherName = feedSummary.getFeed()
                    .getPublisher()
                    .getName();
            String feedUrl = feedSummary.getFeed().getUrl();

            if (!feedSummary.isSuccessful()) {
                notify(feedSummary.getException(), publisherName);
            }

            String icon = feedSummary.isSuccessful() ? DiscordEmojis.CHECK_MARK : DiscordEmojis.CROSS;
            String line = feedSummary.isSuccessful()
                    ? String.format("%s [%s](%s): read %d news in %d ms", icon, publisherName, feedUrl, feedSummary.getCount(), feedSummary.getDuration())
                    : String.format("%s [%s](%s): failed", icon, publisherName, feedUrl);
            description.append(line).append("\n");
        }

        LocateSummaryDto locateSummary = summary.getLocate();
        if (locateSummary != null) {
            totalDuration += locateSummary.getDuration();
            if (!locateSummary.isSuccessful()) {
                notify(locateSummary.getException(), "locate");
            }
            String icon = locateSummary.isSuccessful() ? DiscordEmojis.CHECK_MARK : DiscordEmojis.CROSS;
            String line = locateSummary.isSuccessful()
                    ? String.format("%s **Locate:** %d news in %d ms", icon, locateSummary.getCount(), locateSummary.getDuration())
                    : String.format("%s **Locate:** failed", icon);
            description.append("\n").append(line);
        }

        embed.setDescription(description.toString());
        embed.setFooter(new FooterDto(totalDuration + " ms"));

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
