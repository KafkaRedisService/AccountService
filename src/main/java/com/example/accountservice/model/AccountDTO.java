package com.example.accountservice.model;

import com.example.accountservice.constants.enums.EmailType;
import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;

import java.util.Date;

@Entity
@Data
public class AccountDTO {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;

	private String name;

	private String email;

	private String fileName;

	private String pathFile;

	@CreationTimestamp
	private Date timeCreate;

	@Enumerated(EnumType.STRING)
	private EmailType action;
}
