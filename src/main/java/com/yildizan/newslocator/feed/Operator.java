package com.yildizan.newslocator.feed;

import java.net.URL;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.yildizan.newslocator.entity.*;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.input.SAXBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.sun.syndication.feed.synd.SyndEnclosure;
import com.sun.syndication.feed.synd.SyndEntry;
import com.sun.syndication.feed.synd.SyndFeed;
import com.sun.syndication.io.SyndFeedInput;
import com.sun.syndication.io.XmlReader;
import com.yildizan.newslocator.repository.LinguisticsRepository;
import com.yildizan.newslocator.repository.PhraseRepository;
import com.yildizan.newslocator.utility.StringUtils;

@Component
public class Operator {

    @Autowired
    private LinguisticsRepository linguisticsRepository;

    @Autowired
    private PhraseRepository phraseRepository;

    @Value("${wikipedia.endpoint}")
    private String wikipediaEndpoint;

    private List<Linguistics> englishConjunctions;
    private List<Linguistics> englishExceptions;

    // fetch rss feed and transform into entity form
    public Newspaper read(Feed feed) throws Exception {
        Newspaper newspaper = new Newspaper();
        Map<BufferNews, List<Phrase>> content = new LinkedHashMap<>();

        URL url = new URL(feed.getUrl());
        XmlReader reader = new XmlReader(url);
        SyndFeed rss = new SyndFeedInput().build(reader);
        int index = 0;
        for(Object o : rss.getEntries()) {
            SyndEntry entry = (SyndEntry) o;
            BufferNews news = new BufferNews();
            news.setIndex(index++);
            news.setTitle(StringUtils.cleanCode(entry.getTitle()));
            news.setLink(entry.getLink());
            if(entry.getPublishedDate() == null) {
                news.setPublishDate(System.currentTimeMillis());
            }
            else {
                news.setPublishDate(entry.getPublishedDate().getTime());
            }
            news.setFeedId(feed.getId());
            news.setThumbnailUrl(getThumbnailUrl(entry, feed.getPublisherId()));
            if(news.getThumbnailUrl() == null && rss.getImage() != null) {
                news.setThumbnailUrl(rss.getImage().getUrl());
            }
            if(entry.getDescription() != null && !StringUtils.cleanCode(entry.getDescription().getValue()).isEmpty()) {
                news.setDescription(StringUtils.cleanCode(entry.getDescription().getValue()));
            }
            content.put(news, new ArrayList<>());
        }
        newspaper.setNews(content);
        newspaper.setFeed(feed);

        return newspaper;
    }

    // extract proper nouns (word/phrase which starts with capital)
    public List<Phrase> count(String text, int language) {
        List<Phrase> result = new ArrayList<>();

        String[] words = text.trim().split("\\s+");
        String content = "";
        boolean endPhrase;
        for(int i = 0; i < words.length; i++) {
            String word = words[i];
            endPhrase = i == words.length - 1 || StringUtils.endsWithPunctuation(word) || StringUtils.endsWithPossesive(word, language);

            if(StringUtils.beginsWithUppercase(word)) {
                word = StringUtils.cleanSuffix(word, language);
                content = content.isEmpty() ? word : content + ' ' + word;
            }
            else {
                // check conjunction
                if(!content.isEmpty() && isConjunction(word, language)) {
                    boolean allConjunction = true;
                    for(int j = 0; j < words.length - i - 1; j++) {
                        allConjunction = isConjunction(words[i + j], language);
                        if(!allConjunction || StringUtils.beginsWithUppercase(words[i + j + 1])) {
                            break;
                        }
                    }
                    if(allConjunction) {
                        content = content + ' ' + word;
                    }
                    else {
                        endPhrase = true;
                    }
                }
                else {
                    endPhrase = true;
                }
            }

            if(endPhrase && !content.isEmpty()) {
                if(!isException(content, language)) {
                    Phrase phrase = new Phrase(content);
                    if(result.contains(phrase)) {
                        result.get(result.indexOf(phrase)).addCount(1);
                    }
                    else {
                        result.add(phrase);
                    }
                }
                content = "";
            }
        }

        return result;
    }

    // search for location
    public Phrase match(Phrase phrase, int language) {
        Phrase result = phrase;
        if(!isException(phrase.getContent(), language)) {
            result = phraseRepository.findByContent(phrase.getContent()).orElse(phrase);
            if(result.getId() != null && result.getId() > 0) {
                result.setCurrentCount(phrase.getCurrentCount());
                result.setTotalCount(result.getTotalCount() + phrase.getCurrentCount());
            }
        }
        return result;
    }

    // research with wikipedia api description service
    public String research(String text) throws Exception {
        String url = wikipediaEndpoint + text.replace(" ", "%20");

        Document document = new SAXBuilder().build(url);
        Element element = (Element) document.getRootElement()
                                            .getChild("query")
                                            .getChild("pages")
                                            .getChildren("page")
                                            .get(0);
        return element.getAttributeValue("description");
    }

    private boolean isConjunction(String string, int language) {
        if(language == Language.ENGLISH) {
            if(englishConjunctions == null || englishConjunctions.isEmpty()) {
                englishConjunctions = linguisticsRepository.findEnglishConjunctions();
            }
            return englishConjunctions.stream().anyMatch(s -> s.getWord().equals(string));
        }
        else {
            throw new UnsupportedOperationException();
        }
    }

    private boolean isException(String string, int language) {
        if(language == Language.ENGLISH) {
            if(englishExceptions == null || englishExceptions.isEmpty()) {
                englishExceptions = linguisticsRepository.findEnglishExceptions();
            }
            return englishExceptions.stream().anyMatch(s -> s.getWord().equals(string));
        }
        else {
            throw new UnsupportedOperationException();
        }
    }

    @SuppressWarnings("unchecked")
    private String getThumbnailUrl(SyndEntry entry, int publisherId) {
        String thumbnailUrl = null;
        try {
            if(publisherId == Publisher.SPUTNIK) {
                SyndEnclosure enclosure = ((List<SyndEnclosure>) entry.getEnclosures())
                        .stream()
                        .filter(e -> e.getType().equals("image/jpeg"))
                        .findFirst()
                        .orElseThrow();
                thumbnailUrl = enclosure.getUrl();
            }
            else if(publisherId == Publisher.BBC) {
                Element element = ((List<Element>) entry.getForeignMarkup())
                        .stream()
                        .filter(e -> e.getName().equals("thumbnail"))
                        .findFirst()
                        .orElseThrow();
                thumbnailUrl = element.getAttributeValue("url");
            }
            else if(publisherId == Publisher.BUZZFEED) {
                String description = entry.getDescription().getValue();
                int index = description.indexOf("src=\"", description.indexOf("<img ")) + "src=\"".length();
                thumbnailUrl = description.substring(index, description.indexOf("\"", index + 1));
            }
            else if(publisherId == Publisher.WASHINGTON_POST) {
                Element element = ((List<Element>) entry.getForeignMarkup())
                        .stream()
                        .filter(e -> e.getName().equals("thumbnail"))
                        .findFirst()
                        .orElseThrow();
                thumbnailUrl = element.getAttributeValue("url");
            }
            else if(publisherId == Publisher.NEW_YORK_TIMES) {
                Element element = ((List<Element>) entry.getForeignMarkup())
                        .stream()
                        .filter(e -> e.getName().equals("content"))
                        .findFirst()
                        .orElseThrow();
                thumbnailUrl = element.getAttributeValue("url");
            }
            else if(publisherId == Publisher.FOX_NEWS) {
                Element group = ((List<Element>) entry.getForeignMarkup())
                        .stream()
                        .filter(e -> e.getName().equals("group"))
                        .findFirst()
                        .orElseThrow();
                Element element = ((Element) group.getContent()
                        .stream()
                        .filter(e -> e instanceof Element && ((Element) e).getAttributeValue("isDefault").equals("true"))
                        .findFirst()
                        .orElseThrow());
                thumbnailUrl = element.getAttributeValue("url");
            }
        }
        catch (Exception e) {
            thumbnailUrl = null;
        }
        return thumbnailUrl;
    }
}
