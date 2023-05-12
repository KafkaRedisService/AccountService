package com.example.accountservice.response;

import com.example.accountservice.model.AccountDTO;
import lombok.Data;

import java.util.HashMap;
import java.util.List;

@Data
public class AllFileUserResponse {
    private List<AccountDTO> accountDTO;
    private HashMap mapResponse;
    private int page;
    private int size;
    private int number;
    private int numberOfElements;
}
