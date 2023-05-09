package com.example.accountservice.repo;

import com.example.accountservice.model.MessageDTO;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MessageRepo extends JpaRepository<MessageDTO, Integer> {
    List<MessageDTO> findByStatus(boolean status);
}
