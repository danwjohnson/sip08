package com.springinpractice.ch08.web;

import java.util.Date;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.springframework.beans.propertyeditors.StringTrimmerEditor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.springinpractice.ch08.model.UserMessage;
import com.springinpractice.ch08.service.ContactService;

@Controller
@RequestMapping("/contact")
public class ContactController {

	@Inject private ContactService contactService;
	
	@InitBinder
	public void initBinder(WebDataBinder binder) {
		
		binder.setAllowedFields(new String[] {
				"name", "email", "text", "referer"
		});
		
		binder.registerCustomEditor(
				String.class, new StringTrimmerEditor(true));
		
	} // end initBinder()
	
	
	@RequestMapping(value="/new", method = RequestMethod.GET)
	public String getContactForm(HttpServletRequest req, Model model) {
		
		UserMessage userMsg = new UserMessage();
		userMsg.setReferer(req.getHeader("Referer"));
		model.addAttribute(userMsg);
		
		return getFullViewName("contactForm");
		
	} // end getContactForm()
	
	
	@RequestMapping(value="", method = RequestMethod.POST)
	public String postContactForm(HttpServletRequest req,
			@ModelAttribute @Valid UserMessage userMessage,
			BindingResult result) {

		if (result.hasErrors()) {
			
			result.reject("error.global");
			return getFullViewName("contactForm");
			
		}
		
		userMessage.setIpAddress(req.getRemoteAddr());
		userMessage.setAcceptLanguage(req.getHeader("Accept-Language"));
		userMessage.setUserAgent(req.getHeader("User-Agent"));
		userMessage.setDateCreated(new Date());
		contactService.saveUserMessage(userMessage);
		return "redirect:/contact/thanks.html";

	} // end postContactForm()
	
	
	@RequestMapping(value = "/thanks", method = RequestMethod.GET)
	public String getThanksPage() {
		
		return getFullViewName("thanks");
		
	} // end getThanksPage()
	
	
	private String getFullViewName(String viewName) {
		
		return "contact/" + viewName;
		
	} // end getFullViewName()
	
} // end ContactController class
