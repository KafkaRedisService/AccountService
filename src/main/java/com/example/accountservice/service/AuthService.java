package com.example.accountservice.service;

import com.example.accountservice.model.LoginDto;
import com.example.accountservice.model.RegisterDto;

public interface AuthService {
    String login(LoginDto loginDto);

    String register(RegisterDto registerDto);
}