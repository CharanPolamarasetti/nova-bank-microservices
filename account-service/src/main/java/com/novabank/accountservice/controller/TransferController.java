package com.novabank.accountservice.controller;

import com.novabank.accountservice.dto.TransactionResponse;
import com.novabank.accountservice.dto.TransferRequest;
import com.novabank.accountservice.service.TransferService;
import jakarta.validation.Valid;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
//@RequestMapping("/api/accounts")
@RequestMapping({"/api/accounts", "/api/accounts/"})
public class TransferController {

    private final TransferService transferService;

    public TransferController(TransferService transferService) {
        this.transferService = transferService;
    }

    @PostMapping("/transfer")
    public TransactionResponse transfer(@Valid @RequestBody TransferRequest request,
                                         Authentication authentication) {
        Long customerId = (Long) authentication.getPrincipal();
        return transferService.transfer(request, customerId);
    }
}
