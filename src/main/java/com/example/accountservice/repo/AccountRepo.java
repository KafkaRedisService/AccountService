package com.example.accountservice.repo;

import com.example.accountservice.model.AccountDTO;
import com.example.accountservice.model.StatisticDTO;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AccountRepo extends JpaRepository<AccountDTO, Integer> {
}
