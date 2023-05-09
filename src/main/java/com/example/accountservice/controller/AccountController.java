package com.example.accountservice.controller;

import java.util.Date;

import com.example.accountservice.repo.AccountRepo;
import com.example.accountservice.repo.MessageRepo;
import com.example.accountservice.repo.StatisticRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.accountservice.model.AccountDTO;
import com.example.accountservice.model.MessageDTO;
import com.example.accountservice.model.StatisticDTO;

@RestController
@RequestMapping("/account")
public class AccountController {

	@Autowired
	KafkaTemplate<String, Object> kafkaTemplate;

	@Autowired
	AccountRepo accountRepo;

	@Autowired
	MessageRepo messageRepo;

	@Autowired
	StatisticRepo statisticRepo;

	@PostMapping("/new")
	public AccountDTO create(@RequestBody AccountDTO account) {
		StatisticDTO stat = new StatisticDTO("Account: " + account.getEmail() + " is created", new Date());
		stat.setStatus(false);

		// send notification 
		MessageDTO messageDTO = new MessageDTO();
		messageDTO.setTo(account.getEmail());
		messageDTO.setToName(account.getName());
		messageDTO.setSubject("Welcome to DONG PHUOC AN!!");
		messageDTO.setContent("DPA");
		messageDTO.setStatus(false);

		accountRepo.save(account);
		messageRepo.save(messageDTO);
		statisticRepo.save(stat);

//		kafkaTemplate.send("notification", messageDTO);
//		kafkaTemplate.send("statistic", stat);
		
		return account;
	}
}
