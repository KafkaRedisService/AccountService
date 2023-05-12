package com.example.accountservice.controller;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.*;

import com.example.accountservice.fileUtil.FileUploadUtil;
import com.example.accountservice.repo.AccountRepo;
import com.example.accountservice.repo.MessageRepo;
import com.example.accountservice.repo.StatisticRepo;
import com.example.accountservice.response.AllFileUserResponse;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.hibernate.annotations.Parameter;
import org.hibernate.boot.jaxb.internal.XmlSources;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
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
	public String insertUser(@RequestPart("listFile") String listFile, @RequestPart("jsonUser") String jsonUser ) throws IOException {
		ObjectMapper mapper = new ObjectMapper();
		AccountDTO vo = mapper.readValue(jsonUser, AccountDTO.class);

		//Convert Base64 to file
		String base64String = listFile;
		byte[] bytes = Base64.getDecoder().decode(base64String);
		ByteArrayInputStream inputStream = new ByteArrayInputStream(bytes);
		MultipartFile multipartFile = new MultipartFile() {
			@Override
			public String getName() {
				return vo.getFileName();
			}

			@Override
			public String getOriginalFilename() {
				return vo.getFileName();
			}

			@Override
			public String getContentType() {
				return null;
			}

			@Override
			public boolean isEmpty() {
				return false;
			}

			@Override
			public long getSize() {
				return 0;
			}

			@Override
			public byte[] getBytes() throws IOException {
				return new byte[0];
			}

			@Override
			public InputStream getInputStream() throws IOException {
				return inputStream;
			}

			@Override
			public void transferTo(File dest) throws IOException, IllegalStateException {

			}
		};

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

		AccountDTO voUser = mapper.readValue(jsonUser, AccountDTO.class);
		String fileName = multipartFile.getOriginalFilename();
		FileUploadUtil.saveFile(uploadDir, fileName, multipartFile);

		voUser.setFileName(vo.getFileName());
		voUser.setPathFile(uploadDir+"\\"+fileName);

		Optional checkPath = accountRepo.findByPathFile(fileName, voUser.getEmail());
		if(checkPath.isPresent()) {
			if (voUser.getPathFile().compareTo(checkPath.get().toString()) == 0) {
				log.error("exist File Name, File Path!!!!");
				return "Exist File Name, File Path!!!!";
			}
		}
		accountRepo.save(voUser);
	/*
		for (MultipartFile file : listFile) {
			AccountDTO voUser = mapper.readValue(jsonUser, AccountDTO.class);

			String fileName = file.getOriginalFilename();
			FileUploadUtil.saveFile(uploadDir, fileName, file);

			voUser.setFileName(fileName);
			voUser.setPathFile(uploadDir+"\\"+fileName);
			accountRepo.save(voUser);
		}

	 */
		return "ok";
	}

	@GetMapping("/getFileUser")
	public Map getFile(@RequestParam("fileName") String fileName, @RequestParam("email") String email) throws IOException {
		log.info("Read file from resource folder using Spring ResourceUtils");
		Optional optionalFileName = accountRepo.findByPathFile(fileName, email);
		Map map=new HashMap();

		if(optionalFileName.isPresent()) {
			File file = ResourceUtils.getFile(optionalFileName.get().toString());
			// Read File Content
			String content = new String(Files.readAllBytes(file.toPath()));
			Base64.Encoder encoder = Base64.getEncoder();
			String originalString = content;
			String encodedString = encoder.encodeToString(originalString.getBytes());
			map.put(fileName,encodedString);
			return map;
		}
		return map;
	}

	@GetMapping("/getAllFileUser")
	public HashMap getAllFile(@RequestParam("email") String email) throws IOException {
		log.info("Read file from resource folder using Spring ResourceUtils");
		List<AccountDTO> fileList = accountRepo.findAllPathFile(email);
		HashMap map=new HashMap();

		for (AccountDTO fileItem : fileList) {
			File file = ResourceUtils.getFile(fileItem.getPathFile());
			String content = new String(Files.readAllBytes(file.toPath()));
			Base64.Encoder encoder = Base64.getEncoder();
			String originalString = content;
			String encodedString = encoder.encodeToString(originalString.getBytes());
			map.put(fileItem.getFileName(),encodedString);
		}
		return map;

	}

	@GetMapping("/getAllFileUsers")
	public AllFileUserResponse getAllFileUsers() throws IOException {
		AllFileUserResponse allFileUserResponse = new AllFileUserResponse();
		log.info("Read file from resource folder using Spring ResourceUtils");
		Pageable pageable = PageRequest.of(0, 14);
		Page<AccountDTO> fileList = accountRepo.findAll(pageable);
		HashMap map=new HashMap();

		for (AccountDTO fileItem : fileList) {

			File file = ResourceUtils.getFile(fileItem.getPathFile());
			String content = new String(Files.readAllBytes(file.toPath()));
			Base64.Encoder encoder = Base64.getEncoder();
			String originalString = content;
			String encodedString = encoder.encodeToString(originalString.getBytes());
			map.put(fileItem.getPathFile(),encodedString);
			allFileUserResponse.setMapResponse(map);
		}
		allFileUserResponse.setAccountDTO(fileList.toList());
		allFileUserResponse.setPage(fileList.getTotalPages());
		allFileUserResponse.setSize(fileList.getSize());
		allFileUserResponse.setNumber(fileList.getNumber());
		allFileUserResponse.setNumberOfElements(fileList.getNumberOfElements());
		return allFileUserResponse;
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

	@PostMapping(value = "/updateFile")
	public String updateUser(@RequestPart("file") String file, @RequestPart("jsonUser") String jsonUser ) throws IOException {
		ObjectMapper mapper = new ObjectMapper();
		AccountDTO vo = mapper.readValue(jsonUser, AccountDTO.class);

		//Convert Base64 to file
		String base64String = file;
		byte[] bytes = Base64.getDecoder().decode(base64String);
		ByteArrayInputStream inputStream = new ByteArrayInputStream(bytes);
		MultipartFile multipartFile = new MultipartFile() {
			@Override
			public String getName() {
				return vo.getFileName();
			}

			@Override
			public String getOriginalFilename() {
				return vo.getFileName();
			}

			@Override
			public String getContentType() {
				return null;
			}

			@Override
			public boolean isEmpty() {
				return false;
			}

			@Override
			public long getSize() {
				return 0;
			}

			@Override
			public byte[] getBytes() throws IOException {
				return new byte[0];
			}

			@Override
			public InputStream getInputStream() throws IOException {
				return inputStream;
			}

			@Override
			public void transferTo(File dest) throws IOException, IllegalStateException {

			}
		};

		String fileName = multipartFile.getOriginalFilename();

		log.info("Read file from resource folder using Spring ResourceUtils");
		Optional optionalFileName = accountRepo.findByPathFile(fileName, vo.getEmail());
		//accountRepo.deleteFile(fileName, email);

		// File or Directory to be deleted
		Path path = Paths.get(optionalFileName.get().toString());

		try {
			// Delete file or directory
			Files.delete(path);
			log.info("File or directory deleted successfully");
		} catch (IOException ex) {
			System.out.println(ex);
		}
		String newStrg= path.toString().replace(fileName, "");
		FileUploadUtil.saveFile(newStrg, fileName, multipartFile);
		return "ok";
	}
}
