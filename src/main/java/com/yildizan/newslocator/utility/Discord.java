package com.yildizan.newslocator.utility;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.io.PrintWriter;
import java.io.StringWriter;

@Component
public final class Discord {

    @Value("${discord-info}")
    private String infoHook;

    @Value("${discord-error}")
    private String errorHook;

    public void notify(String url, String header, String... body) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<String> request = new HttpEntity<>(new Notification(header, body).toString(), headers);
            new RestTemplate().postForObject(url, request, String.class);
        }
        catch (Exception e) {}
    }

    public void notifyInfo(String header, String info) {
        notify(infoHook, header, info);
    }

    public void notifyError(Exception e) {
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        e.printStackTrace(pw);
        notify(errorHook, "", sw.toString()
                .replace("\t", "")
                .replace("\n", "\\n")
                .replace("\r", "\\r"));
    }

    private class Notification {

        private String content;
        private Embed[] embeds;

        public Notification(String content, String... descriptions) {
            this.content = content;
            Embed[] embeds = new Embed[descriptions.length];
            for(int i = 0; i < descriptions.length; i++) {
                embeds[i] = new Embed(descriptions[i]);
            }
            this.embeds = embeds;
        }

        @Override
        public String toString() {
            String str = "{\"content\":\"" + content + "\",\"embeds\":[";
            for(Embed embed : embeds) {
                str += "{\"description\":\"" + embed
                        .getDescription()
                        .substring(0, Math.min(500, embed.getDescription().length())) + "\"},";
            }
            if(str.charAt(str.length() - 1) == ',') {
                str = str.substring(0, str.length() - 1);
            }
            str += "]}";
            return str;
        }

        private class Embed {

            private String description;

            public Embed(String description) {
                this.description = description;
            }

            public String getDescription() {
                return description;
            }

        }

    }
}
