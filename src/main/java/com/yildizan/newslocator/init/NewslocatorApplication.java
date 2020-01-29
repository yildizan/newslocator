package com.yildizan.newslocator.init;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@ComponentScan({
		"com.yildizan.newslocator.controller",
		"com.yildizan.newslocator.feed",
		"com.yildizan.newslocator.utility"
})
@EntityScan({"com.yildizan.newslocator.entity"})
@EnableJpaRepositories({"com.yildizan.newslocator.repository"})
public class NewslocatorApplication {

	public static void main(String[] args) {
		SpringApplication.run(NewslocatorApplication.class, args);
	}

}
