package com.yildizan.newsfrom.locator.utility.rss.thumbnail;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class ThumbnailExtractorFactory {

    private static final Map<String ,ThumbnailExtractor> thumbnailExtractors = List.of(
            new BbcExtractor(),
            new BuzzFeedExtractor(),
            new FoxNewsExtractor(),
            new NewYorkTimesExtractor(),
            new ReutersExtractor(),
            new SputnikExtractor(),
            new WashingtonPostExtractor()
        )
        .stream()
        .collect(Collectors.toMap(ThumbnailExtractor::getPublisherName, extractor -> extractor));

    public static ThumbnailExtractor provide(String publisherName) {
        return thumbnailExtractors.get(publisherName.toLowerCase());
    }
    
}
