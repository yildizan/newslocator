package com.yildizan.newslocator.controller;

import com.yildizan.newslocator.service.LocatorService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;

@Controller
@RequestMapping(path="/locator")
@RequiredArgsConstructor
public class LocatorController {

	private final LocatorService locatorService;

	@GetMapping(path = "/locate")
	@ResponseStatus(value = HttpStatus.OK)
	public void locate() {
		locatorService.process();
	}

}
