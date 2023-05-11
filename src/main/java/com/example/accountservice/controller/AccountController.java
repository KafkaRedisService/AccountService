package com.example.accountservice.controller;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import com.example.accountservice.fileUtil.FileUploadUtil;
import com.example.accountservice.repo.AccountRepo;
import com.example.accountservice.repo.MessageRepo;
import com.example.accountservice.repo.StatisticRepo;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.hibernate.annotations.Parameter;
import org.hibernate.boot.jaxb.internal.XmlSources;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.util.ResourceUtils;
import org.springframework.web.bind.annotation.*;

import com.example.accountservice.model.AccountDTO;
import com.example.accountservice.model.MessageDTO;
import com.example.accountservice.model.StatisticDTO;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/account")
public class AccountController {
	private static final Logger log = LoggerFactory.getLogger(AccountController.class);
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

	@PostMapping(value = "/insertFiles")
	public String insertUser(@RequestPart("listFile") List<MultipartFile> listFile, @RequestPart("jsonUser") String jsonUser ) throws IOException {
		ObjectMapper mapper = new ObjectMapper();
		AccountDTO vo = mapper.readValue(jsonUser, AccountDTO.class);

		String uploadDir ="C:\\temp" ;
		try {
			LocalDate today = LocalDate.now();
			Path path = Paths.get("C:\\template\\"+vo.getName()+"\\"+today);

			//java.nio.file.Files;
			Files.createDirectories(path);
			uploadDir = path.toString();

			log.info(uploadDir);
		} catch (IOException e) {
			log.error("Failed to create directory!" + e.getMessage());
		}

		for (MultipartFile file : listFile) {
			AccountDTO voUser = mapper.readValue(jsonUser, AccountDTO.class);

			String fileName = file.getOriginalFilename();
			FileUploadUtil.saveFile(uploadDir, fileName, file);

			voUser.setFileName(fileName);
			voUser.setPathFile(uploadDir+"\\"+fileName);
			accountRepo.save(voUser);
		}
		return "ok";
	}

	@GetMapping("/getFile")
	public String getFile(@RequestParam("fileName") String fileName, @RequestParam("email") String email) throws IOException {
		log.info("Read file from resource folder using Spring ResourceUtils");
		Optional optionalFileName = accountRepo.findByPathFile(fileName, email);

		if(optionalFileName.isPresent()) {
			File file = ResourceUtils.getFile(optionalFileName.get().toString());
			// Read File Content
			String content = new String(Files.readAllBytes(file.toPath()));
			return content;
		}
		return "";
	}

	@GetMapping("/getAllFile")
	public List<File> getAllFile(@RequestParam("email") String email) throws IOException {
		log.info("Read file from resource folder using Spring ResourceUtils");
		List<String> fileList = accountRepo.findAllPathFile(email);
		List<File> newList = new ArrayList<>();

		for (String fileItem : fileList) {
			File file = ResourceUtils.getFile(fileItem.toString());
			//String content = new String(Files.readAllBytes(file.toPath()));
			newList.add(file);
		}
		return newList;

	}

	@RequestMapping(value = "/removeFile",produces="text/html", method = RequestMethod.DELETE)
	public String removeFileHandler(@RequestParam("deletedFileName") String fileName, @RequestParam("email") String email) {
		log.info("Read file from resource folder using Spring ResourceUtils");
		Optional optionalFileName = accountRepo.findByPathFile(fileName, email);
		accountRepo.deleteFile(fileName, email);

		// File or Directory to be deleted
		Path path = Paths.get(optionalFileName.get().toString());

		try {
			// Delete file or directory
			Files.delete(path);
			log.info("File or directory deleted successfully");
		} catch (IOException ex) {
			System.out.println(ex);
		}
		return "OK";
	}
}
