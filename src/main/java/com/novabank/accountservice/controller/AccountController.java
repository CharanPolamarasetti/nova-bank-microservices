package com.novabank.accountservice.controller;

import com.novabank.accountservice.dto.*;
import com.novabank.accountservice.service.AccountService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
//@RequestMapping("/api/accounts")
@RequestMapping({"/api/accounts", "/api/accounts/"})
public class AccountController {

    private final AccountService accountService;

    public AccountController(AccountService accountService) {
        this.accountService = accountService;
    }

    @GetMapping
    public List<AccountResponse> listAccounts(Authentication authentication) {
        Long customerId = (Long) authentication.getPrincipal();
        return accountService.listAccountsForCustomer(customerId);
    }

    @GetMapping("/{accountNumber}")
    public AccountResponse getAccount(@PathVariable String accountNumber, Authentication authentication) {
        Long customerId = (Long) authentication.getPrincipal();
        return accountService.getAccount(accountNumber, customerId);
    }

    @GetMapping("/{accountNumber}/balance")
    public BalanceResponse getBalance(@PathVariable String accountNumber, Authentication authentication) {
        Long customerId = (Long) authentication.getPrincipal();
        return accountService.getBalance(accountNumber, customerId);
    }

    @GetMapping("/{accountNumber}/transactions")
    public Page<TransactionResponse> getTransactions(@PathVariable String accountNumber,
                                                       Authentication authentication,
                                                       Pageable pageable) {
        Long customerId = (Long) authentication.getPrincipal();
        return accountService.getTransactionHistory(accountNumber, customerId, pageable);
    }

    @PostMapping("/{accountNumber}/beneficiaries")
    public BeneficiaryResponse addBeneficiary(@PathVariable String accountNumber,
                                               @Valid @RequestBody BeneficiaryRequest request,
                                               Authentication authentication) {
        Long customerId = (Long) authentication.getPrincipal();
        return accountService.addBeneficiary(accountNumber, customerId, request);
    }

    @GetMapping("/{accountNumber}/beneficiaries")
    public List<BeneficiaryResponse> listBeneficiaries(@PathVariable String accountNumber,
                                                         Authentication authentication) {
        Long customerId = (Long) authentication.getPrincipal();
        return accountService.listBeneficiaries(accountNumber, customerId);
    }
}
