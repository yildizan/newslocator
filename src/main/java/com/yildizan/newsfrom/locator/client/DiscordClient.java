package com.yildizan.newsfrom.locator.client;

import com.yildizan.newsfrom.locator.configuration.DiscordClientConfiguration;
import com.yildizan.newsfrom.locator.dto.discord.InfoDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;

@FeignClient(name = "discordClient", url = "${discord.webhook.url}", configuration = DiscordClientConfiguration.class)
public interface DiscordClient {

    @PostMapping(value = "${discord.webhook.params.info}", consumes = MediaType.APPLICATION_JSON_VALUE)
    void notifyInfo(@RequestBody InfoDto infoDto);

    @PostMapping(value = "${discord.webhook.params.error}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    void notifyError(@RequestPart(value = "file") MultipartFile file);

}
