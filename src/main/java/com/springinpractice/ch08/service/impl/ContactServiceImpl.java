package com.springinpractice.ch08.service.impl;

import javax.inject.Inject;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.springinpractice.ch08.dao.UserMessageDao;
import com.springinpractice.ch08.model.UserMessage;
import com.springinpractice.ch08.service.ContactService;

@Service
@Transactional(
		propagation = Propagation.REQUIRED,
		isolation = Isolation.DEFAULT,
		readOnly = true)
public class ContactServiceImpl implements ContactService{

	@Inject private UserMessageDao userMsgDao;
	@Transactional(readOnly = false)
	public void saveUserMessage(UserMessage userMsg) {
		
		userMsgDao.create(userMsg);
		
	} // end saveUsreMessage()
	
} // end ContactServiceImpl
