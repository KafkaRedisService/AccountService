package com.example.accountservice.service;

import com.example.accountservice.model.LoginDto;
import com.example.accountservice.model.RegisterDto;

import java.util.Map;

public interface AuthService {
    String login(LoginDto loginDto);

    String register(RegisterDto registerDto);

    String refreshtoken(Map<String, Object> claims, String subject);
}