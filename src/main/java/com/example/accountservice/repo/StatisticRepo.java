package com.example.accountservice.repo;

import com.example.accountservice.model.MessageDTO;
import com.example.accountservice.model.StatisticDTO;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface StatisticRepo extends JpaRepository<StatisticDTO, Integer> {
    List<StatisticDTO> findByStatus(boolean status);
}
