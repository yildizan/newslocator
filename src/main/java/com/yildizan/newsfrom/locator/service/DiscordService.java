package com.yildizan.newsfrom.locator.service;

import com.yildizan.newsfrom.locator.client.DiscordClient;
import com.yildizan.newsfrom.locator.dto.discord.*;
import com.yildizan.newsfrom.locator.utility.DiscordEmojis;
import com.yildizan.newsfrom.locator.utility.StringUtils;
import com.yildizan.newsfrom.locator.dto.SummaryDto;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.PrintWriter;
import java.io.StringWriter;
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
            if (!summary.isSuccessful()) {
                notify(summary.getException());
            }

            duration += summary.getDuration();
            String publisherName = summary.getFeed()
                    .getPublisher()
                    .getName();

            FieldDto field = new FieldDto();
            field.setName((summary.isSuccessful() ? DiscordEmojis.CHECK_MARK : DiscordEmojis.CROSS) + ' ' + publisherName);
            field.setValue(summary.getLocated() + " / " + summary.getMatched() + " / " + summary.getNotMatched());

            embed.getFields().add(field);
        }

        embed.setFooter(new FooterDto(duration + " ms"));
        InfoDto dto = new InfoDto(Collections.singletonList(embed));

        discordClient.notifyInfo(dto);
    }

    private void notify(Exception e) {
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        e.printStackTrace(pw);

        String stackTrace = sw.toString()
                .replace("\t", "")
                .replace("\n", "\\n")
                .replace("\r", "\\r");

        discordClient.notifyError(new ErrorDto(StringUtils.wrapWith(stackTrace, "```")));
    }

}
