package com.springinpractice.ch08.web;

import javax.inject.Inject;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import com.springinpractice.ch08.service.NewsService;

@Controller
public class NewsController {

	@Inject private NewsService newsService;
	
	@RequestMapping("/news.rss")
	public String rss(Model model) {
		
		model.addAttribute(newsService.getRecentNews());
		return "news";
		
	} // end rss
	
} // end NewsController class
