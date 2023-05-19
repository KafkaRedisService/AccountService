package com.example.accountservice.service.Impl;

import com.example.accountservice.model.AccountDTO;
import com.example.accountservice.repo.AccountRepo;
import com.example.accountservice.service.AccountEmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AccountEmailServiceImpl implements AccountEmailService {
    @Autowired
    AccountRepo accountRepo;

    @Override
    public void updateActionEmail(AccountDTO accountDTO) {
        Optional<AccountDTO> accountDTO1 = accountRepo.findById(accountDTO.getId());

        if(accountDTO1.isPresent()) {
            accountDTO1.get().setAction(accountDTO.getAction());
            accountRepo.save(accountDTO1.get());
        }

    }
}
