package com.yildizan.newsfrom.locator.service;

import com.sun.syndication.feed.synd.SyndEnclosure;
import com.sun.syndication.feed.synd.SyndEntry;
import com.sun.syndication.feed.synd.SyndFeed;
import com.sun.syndication.io.SyndFeedInput;
import com.sun.syndication.io.XmlReader;
import com.yildizan.newsfrom.locator.entity.*;
import com.yildizan.newsfrom.locator.repository.LinguisticsRepository;
import com.yildizan.newsfrom.locator.repository.NewsPhraseRepository;
import com.yildizan.newsfrom.locator.repository.NewsRepository;
import com.yildizan.newsfrom.locator.repository.PhraseRepository;
import com.yildizan.newsfrom.locator.utility.StringUtils;
import com.yildizan.newsfrom.locator.dto.SummaryDto;
import lombok.RequiredArgsConstructor;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.input.SAXBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.StoredProcedureQuery;
import javax.transaction.Transactional;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@Service
@RequiredArgsConstructor
public class LocatorService {

    private final NewsRepository newsRepository;
    private final PhraseRepository phraseRepository;
    private final NewsPhraseRepository newsPhraseRepository;
    private final LinguisticsRepository linguisticsRepository;

    private List<Linguistics> englishConjunctions;
    private List<Linguistics> englishExceptions;

    @Value("${wikipedia.url}")
    private String wikipediaEndpoint;

    @Value("${wikipedia.enabled}")
    private boolean wikipediaEnabled;

    @PersistenceContext
    private EntityManager entityManager;

    @Async
    public CompletableFuture<SummaryDto> process(Feed feed) {
        SummaryDto summary = new SummaryDto(feed, System.currentTimeMillis());
        try {
            // build newspaper from feed
            List<BufferNews> newsList = read(feed);
            int language = feed.getLanguageId();
            for(BufferNews news : newsList) {
                locate(news, language);
                saveNews(news);
                for(Phrase phrase : news.getPhrases()) {
                    savePhrase(phrase);
                    newsPhraseRepository.save(new BufferNewsPhrase(new BufferNewsPhraseId(news.getId(), phrase.getId()), phrase.getCurrentCount()));
                }
                // summary
                if(news.isMatched()) {
                    news.getPhrases()
                            .stream()
                            .filter(phrase -> phrase.getId().equals(news.getTopPhraseId()))
                            .findAny()
                            .ifPresentOrElse(p -> summary.incrementLocated(), summary::incrementMatched);
                }
                else {
                    summary.incrementNotMatched();
                }
            }
        }
        catch (Exception e) {
            summary.setException(e);
        }
        finally {
            summary.setFinish(System.currentTimeMillis());
        }
        return CompletableFuture.completedFuture(summary);
    }

    public void clear() {
        StoredProcedureQuery query = entityManager.createStoredProcedureQuery("clear_buffer");
        query.execute();
    }

    public void update() {
        StoredProcedureQuery query = entityManager.createStoredProcedureQuery("update_news");
        query.execute();
    }

    @Transactional
    public void saveNews(BufferNews news) {
        newsRepository.save(news);
    }

    @Transactional
    public void savePhrase(Phrase phrase) {
        phraseRepository.save(phrase);
    }

    private void locate(BufferNews news, int language) throws Exception {
        List<Phrase> phrases = extract(news, language);
        // descending order
        phrases.sort(Collections.reverseOrder());
        for(int i = 0; i < phrases.size(); i++) {
            // match original
            Phrase phrase = match(phrases.get(i), language);
            phrases.set(i, phrase);
            if(!news.isMatched() && phrase.hasLocation()) {
                news.setTopPhraseId(phrase.getId());
            }
            else {
                // match partially
                String[] words = phrase.getContent().split("\\s+");
                for(int j = 0; j < words.length && !news.isMatched(); j++) {
                    Phrase child = match(new Phrase(words[j]), language);
                    if(child.hasLocation()) {
                        news.setTopPhraseId(child.getId());
                        phrase.setLocationId(child.getLocationId());
                        phrases.set(i, phrase);
                        break;
                    }
                    // match by appending
                    for(int k = j + 1; k < words.length; k++) {
                        child = match(new Phrase(child.getContent() + ' ' + words[k]), language);
                        if(child.hasLocation()) {
                            news.setTopPhraseId(child.getId());
                            phrase.setLocationId(child.getLocationId());
                            phrases.set(i, phrase);
                            break;
                        }
                    }
                }
                if(!news.isMatched() && wikipediaEnabled) {
                    // research
                    String description = research(phrase.getContent());
                    if(description != null && !description.isEmpty() && !description.startsWith("Disambiguation")) {
                        List<Phrase> researches = extract(description, Language.ENGLISH);
                        for(int j = 0; j < researches.size(); j++) {
                            Phrase research = match(researches.get(j), language);
                            if(research.hasLocation()) {
                                news.setTopPhraseId(research.getId());
                                phrase.setLocationId(research.getLocationId());
                                phrases.set(i, phrase);
                                break;
                            }
                        }
                    }
                }
            }
        }
        news.setPhrases(phrases);
    }

    // fetch rss feed and transform into entity form
    private List<BufferNews> read(Feed feed) throws Exception {
        List<BufferNews> newsList = new ArrayList<>();

        URL url = new URL(feed.getUrl());
        XmlReader reader = new XmlReader(url);
        SyndFeed rss = new SyndFeedInput().build(reader);
        for(Object o : rss.getEntries()) {
            SyndEntry entry = (SyndEntry) o;
            BufferNews news = new BufferNews();
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
            newsList.add(news);
        }

        return newsList;
    }

    // extract proper nouns (word/phrase which starts with capital)
    private List<Phrase> extract(BufferNews news, int language) {
        return extract(news.getDescription() == null || news.getDescription().isEmpty() ? news.getTitle() : news.getDescription(), language);
    }

    // extract proper nouns (word/phrase which starts with capital)
    private List<Phrase> extract(String text, int language) {
        List<Phrase> phrases = new ArrayList<>();
        String[] words = text.trim().split("\\s+");
        String content = "";
        boolean endPhrase;
        for(int i = 0; i < words.length; i++) {
            String word = words[i];
            endPhrase = i == words.length - 1 || StringUtils.endsWithPunctuation(word) || StringUtils.endsWithPossessive(word, language);

            if(StringUtils.startsWithUppercase(word)) {
                word = StringUtils.cleanSuffix(word, language);
                content = content.isEmpty() ? word : content + ' ' + word;
            }
            else {
                // check conjunction
                if(!content.isEmpty() && isConjunction(word, language)) {
                    boolean allConjunction = true;
                    for(int j = 0; j < words.length - i - 1; j++) {
                        allConjunction = isConjunction(words[i + j], language);
                        if(!allConjunction || StringUtils.startsWithUppercase(words[i + j + 1])) {
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
                if(notException(content, language)) {
                    final String finalContent = content;
                    phrases.stream()
                            .filter(p -> p.getContent().equals(finalContent))
                            .findAny()
                            .ifPresentOrElse(Phrase::incrementCount, () -> phrases.add(new Phrase(finalContent)));
                }
                content = "";
            }
        }
        return phrases;
    }

    // search for located phrase
    private Phrase match(Phrase phrase, int language) {
        Phrase result = phrase;
        if(notException(phrase.getContent(), language)) {
            result = phraseRepository.findByContent(phrase.getContent()).orElse(phrase);
            if(result.getId() != null && result.getId() > 0) {
                result.setCurrentCount(phrase.getCurrentCount());
                result.setTotalCount(result.getTotalCount() + phrase.getCurrentCount());
            }
        }
        return result;
    }

    // research with wikipedia api description service
    private String research(String text) throws Exception {
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

    private boolean notException(String string, int language) {
        if(language == Language.ENGLISH) {
            if(englishExceptions == null || englishExceptions.isEmpty()) {
                englishExceptions = linguisticsRepository.findEnglishExceptions();
            }
            return englishExceptions.stream().noneMatch(s -> s.getWord().equals(string));
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
        catch (Exception ignored) {
            thumbnailUrl = null;
        }
        return thumbnailUrl;
    }

}
