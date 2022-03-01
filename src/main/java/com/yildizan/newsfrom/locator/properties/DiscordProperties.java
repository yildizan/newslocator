package com.yildizan.newsfrom.locator.properties;

import lombok.Getter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Getter
@Configuration
@ConfigurationProperties(prefix = "discord.webhook")
public class DiscordProperties {

    private String infoUrl;
    private String errorUrl;

}
