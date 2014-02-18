package com.springinpractice.ch08.service.impl;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;
import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

import static org.springframework.util.Assert.notNull;

import org.apache.velocity.app.VelocityEngine;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.velocity.VelocityEngineUtils;

import com.springinpractice.ch08.dao.UserMessageDao;
import com.springinpractice.ch08.model.UserMessage;
import com.springinpractice.ch08.service.ContactService;

@Service
@Transactional(
		propagation = Propagation.REQUIRED,
		isolation = Isolation.DEFAULT,
		readOnly = true)
public class ContactServiceImpl implements ContactService{
	
	private static final String CONFIRMATION_TEMPLATE_PATH =
			"contactConfirm.vm";
	private static final String USER_MSG_TEMPLATE_PATH =
			"contactUserMessage.vm";

	@Inject private UserMessageDao userMsgDao;
	@Inject private JavaMailSender mailSender;
	@Inject private VelocityEngine velocityEngine;
	
	@Value("#{contactServiceProps.sendConfirmation}")
	private boolean sendConfirmation;
	
	@Value("#{contactServiceProps.notifyAdmin}")
	private boolean notifyAdmin;
	
	@Value("#{contactServiceProps.noReplyEmailAddress}")
	private String noReplyEmailAddr;
	
	@Value("#{contactServiceProps.adminEmailAddress}")
	private String adminEmailAddr;
	
	@Transactional(readOnly = false)
	public void saveUserMessage(UserMessage userMsg) {
		
		notNull(userMsg, "userMsg can't be null");
		userMsgDao.create(userMsg);
		if (sendConfirmation) {
			
			MimeMessage mimeMsg = createEmail(
					userMsg, CONFIRMATION_TEMPLATE_PATH,
					"Confirmation message", userMsg.getEmail(),
					noReplyEmailAddr, null);
			
			sendEmail(mimeMsg);
			
		} // end if
		
		if (notifyAdmin) {
			
			MimeMessage mimeMsg = createEmail(
					userMsg, USER_MSG_TEMPLATE_PATH,
					"User message", adminEmailAddr,
					userMsg.getEmail(), userMsg.getName());
			
			sendEmail(mimeMsg);
			
		} // end if
		
	} // end saveUserMessage()
	
	
	private MimeMessage createEmail(UserMessage userMsg,
			String templatePath, String subject, String toEmail,
			String fromEmail, String fromName) {
		
		MimeMessage mimeMsg = mailSender.createMimeMessage();
		Map<String, Object> model = new HashMap<String, Object>();
		model.put("userMessage", userMsg);
		String text = VelocityEngineUtils.mergeTemplateIntoString(velocityEngine, templatePath, model);
		text = text.replaceAll("\n", "<br>");
		
		try {
			
			MimeMessageHelper helper = new MimeMessageHelper(mimeMsg);
			helper.setSubject(subject);
			helper.setTo(toEmail);
			
			if (fromName == null) {
				
				helper.setFrom(fromEmail);
				
			} else {
				
				try {
					
					helper.setFrom(fromEmail, fromName);
					
				} // end try
				catch(UnsupportedEncodingException e) {
					
					helper.setFrom(fromEmail);
					
				} // end catch
				
			} // end if/else
			
			helper.setSentDate(userMsg.getDateCreated());
			helper.setText(text, true);
			
		} // end try
		catch(MessagingException e) {
			
			throw new RuntimeException(e);
			
		} // end try/catch
		
		return mimeMsg;
		
	} // end createEmail()
	
	
	private void sendEmail(MimeMessage mimeMsg) {
		
		mailSender.send(mimeMsg);
		
	} // end sendEmail()
	
} // end ContactServiceImpl
