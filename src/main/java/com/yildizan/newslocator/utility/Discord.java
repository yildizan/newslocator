package com.yildizan.newslocator.utility;

import com.yildizan.newslocator.entity.Publisher;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@Component
public final class Discord {

    @Value("${webhook.info}")
    private String infoHook;

    @Value("${webhook.error}")
    private String errorHook;

    @Value("${application.name}")
    private String applicationName;

    @Value("${application.url}")
    private String applicationUrl;

    public void send(String url, String message) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> request = new HttpEntity<>(message, headers);
        new RestTemplate().postForObject(url, request, String.class);
    }

    public void notify(List<Report> reports, long duration) {
        int successRate;
        if(reports.stream().allMatch(Report::isSuccessful)) {
            successRate = SuccessRate.ALL;
        }
        else if(reports.stream().noneMatch(Report::isSuccessful)) {
            successRate = SuccessRate.NONE;
        }
        else {
            successRate = SuccessRate.PARTIAL;
        }
        
        JSONObject footer = new JSONObject();
        footer.put("text", duration + "ms");

        JSONObject embed = new JSONObject();
        embed.put("title", applicationName);
        embed.put("url", applicationUrl);
        embed.put("color", successRate == SuccessRate.ALL ? 3066993 : successRate == SuccessRate.NONE ? 15158332 : 15844367);
        embed.put("timestamp", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
        embed.put("footer", footer);

        if(successRate > SuccessRate.NONE) {
            JSONArray fields = new JSONArray();
            for(Report report : reports) {
                JSONObject field = new JSONObject();
                field.put("name", ":newspaper: " + stringifyPublisher(report.getFeed().getPublisherId()) + "\t" + (report.isSuccessful() ? ":white_check_mark:" : ":x:") + ' ' + report.getDuration() + "ms");
                field.put("value", report.getLocated() + " / " + report.getMatched() + " / " + report.getNotMatched());
                field.put("inline", true);
                fields.put(field);
            }
            embed.put("fields", fields);
        }

        JSONArray array = new JSONArray();
        array.put(embed);

        JSONObject message = new JSONObject();
        message.put("embeds", array);

        send(infoHook, message.toString());
    }

    public void notify(Exception e) {
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        e.printStackTrace(pw);
        try {
            send(errorHook, "```" + sw.toString().replace("\t", "").replace("\n", "\\n").replace("\r", "\\r") + "```");
        }
        catch (Exception ignored) {}
    }

    private static String stringifyPublisher(int publisherId) {
        switch (publisherId) {
            case Publisher.SPUTNIK:
                return "Sputnik";
            case Publisher.REUTERS:
                return "Reuters";
            case Publisher.BBC:
                return "BBC";
            case Publisher.BUZZFEED:
                return "BuzzFeed";
            case Publisher.WASHINGTON_POST:
                return "Washington Post";
            case Publisher.NEW_YORK_TIMES:
                return "New York Times";
            case Publisher.FOX_NEWS:
                return "Fox News";
            default:
                return Integer.toString(publisherId);
        }
    }

    private static class SuccessRate {

        public static final int NONE = -1;
        public static final int PARTIAL = 0;
        public static final int ALL = 1;

    }

}
