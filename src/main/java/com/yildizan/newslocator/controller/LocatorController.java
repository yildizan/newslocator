package com.yildizan.newslocator.controller;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.StoredProcedureQuery;
import javax.transaction.Transactional;

import com.yildizan.newslocator.entity.*;
import com.yildizan.newslocator.utility.Discord;
import com.yildizan.newslocator.utility.Report;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;

import com.yildizan.newslocator.feed.Newspaper;
import com.yildizan.newslocator.feed.Operator;
import com.yildizan.newslocator.repository.FeedRepository;
import com.yildizan.newslocator.repository.NewsPhraseRepository;
import com.yildizan.newslocator.repository.NewsRepository;
import com.yildizan.newslocator.repository.PhraseRepository;

@Controller
@RequestMapping(path="/locator")
public class LocatorController {
	
	private final Logger log = LoggerFactory.getLogger("locator");

	@Autowired
	private FeedRepository feedRepository;

	@Autowired
	private NewsRepository newsRepository;
	
	@Autowired
	private PhraseRepository phraseRepository;
	
	@Autowired
	private NewsPhraseRepository newsPhraseRepository;
	
	@Autowired
	private Operator operator;

	@Autowired
	private Discord discord;

	@Value("${wikipedia.enabled}")
	private boolean wikipediaEnabled;
	
	@PersistenceContext
	private EntityManager entityManager;

	/*
	steps:
	1. read each of active feeds
	2. extract proper nouns
	3. search for location related to them
	4. (optional) research wikipedia if no location exists with that phrase
	 */
	@GetMapping(path = "/locate")
	@ResponseStatus(value = HttpStatus.OK)
	public void locate() {
		long start = System.currentTimeMillis();
		List<Report> reports = new ArrayList<>();
		List<Feed> feeds = feedRepository.findActive();

		// dump buffer
		StoredProcedureQuery query = entityManager.createStoredProcedureQuery("clear_buffer");
		query.execute();
		log.info("buffer clear");

		for(Feed feed : feeds) {
			Report report = new Report(feed, System.currentTimeMillis());
			try {
				// build newspaper from feed
				Newspaper newspaper = operator.read(feed);
				int language = feed.getLanguageId();
				for(Map.Entry<BufferNews, List<Phrase>> entry : newspaper.getNews().entrySet()) {
					BufferNews news = entry.getKey();
					String text = news.getDescription() == null || news.getDescription().isEmpty() ? news.getTitle() : news.getDescription();
					List<Phrase> phrases = operator.count(text, language);
					// descending order
					phrases.sort(Collections.reverseOrder());
					for(int i = 0; i < phrases.size(); i++) {
						// match original
						Phrase phrase = operator.match(phrases.get(i), language);
						phrases.set(i, phrase);
						if(!news.isMatched()) {
							if(phrase.hasLocation()) {
								news.setTopPhraseId(phrase.getId());
							}
							else {
								// match partially
								String[] words = phrase.getContent().split("\\s+");
								for(int j = 0; j < words.length; j++) {
									Phrase child = operator.match(new Phrase(words[j]), language);
									if(child.hasLocation()) {
										news.setTopPhraseId(child.getId());
										phrase.setLocationId(child.getLocationId());
										phrases.set(i, phrase);
										break;
									}
									// match by appending
									for(int k = j + 1; k < words.length; k++) {
										child = operator.match(new Phrase(child.getContent() + ' ' + words[k]), language);
										if(child.hasLocation()) {
											news.setTopPhraseId(child.getId());
											phrase.setLocationId(child.getLocationId());
											phrases.set(i, phrase);
											break;
										}
									}
									if(news.isMatched()) {
										break;
									}
								}
								if(!news.isMatched() && wikipediaEnabled) {
									// research
									String description = operator.research(phrase.getContent());
									if(description != null && !description.isEmpty() && !description.startsWith("Disambiguation")) {
										List<Phrase> researches = operator.count(description, Language.ENGLISH);
										for(int j = 0; j < researches.size(); j++) {
											Phrase research = operator.match(researches.get(j), language);
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
					}
					saveNews(news);
					for(Phrase phrase : phrases) {
						savePhrase(phrase);
						newsPhraseRepository.save(new BufferNewsPhrase(new BufferNewsPhraseId(news.getId(), phrase.getId()), phrase.getCurrentCount()));
					}
					// report
					if(news.isMatched()) {
						Phrase dummy = new Phrase();
						dummy.setLocationId(1);
						if(phrases.stream().filter(phrase -> phrase.getId() == news.getTopPhraseId()).findFirst().orElse(dummy).hasLocation()) {
							report.incrementLocated();
						}
						else {
							report.incrementMatched();
						}
					}
					else {
						report.incrementNotMatched();
					}
				}
				report.setSuccessful(true);
			}
			catch (Exception e) {
				report.setSuccessful(false);
				log.error("feedId: " + feed.getId() + " exception: ", e);
				discord.notify(e);
			}
			finally {
				report.setFinish(System.currentTimeMillis());
				reports.add(report);
			}
		}

		// refresh news
		query = entityManager.createStoredProcedureQuery("update_news");
		query.execute();
		log.info("news updated");

		long duration = System.currentTimeMillis() - start;
		log.info("execution time: " + duration + "ms feedCount: " + feeds.size());
		discord.notify(reports, duration);
	}
	
	@Transactional
	public void saveNews(BufferNews news) {
		newsRepository.save(news);
	}
	
	@Transactional
	public void savePhrase(Phrase phrase) {
		phraseRepository.save(phrase);
	}

}
