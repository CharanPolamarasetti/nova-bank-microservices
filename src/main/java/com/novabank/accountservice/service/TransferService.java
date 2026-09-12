package com.novabank.accountservice.service;

import com.novabank.accountservice.dto.TransactionResponse;
import com.novabank.accountservice.dto.TransferRequest;
import com.novabank.accountservice.entity.Account;
import com.novabank.accountservice.entity.Transaction;
import com.novabank.accountservice.exception.AccountAccessDeniedException;
import com.novabank.accountservice.exception.AccountNotFoundException;
import com.novabank.accountservice.exception.InsufficientBalanceException;
import com.novabank.accountservice.exception.InvalidAccountStateException;
import com.novabank.accountservice.repository.AccountRepository;
import com.novabank.accountservice.repository.TransactionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * Implements the fund-transfer transaction boundary described in Phase 2:
 * lock both accounts, validate, debit source, credit destination, record one
 * transaction row - all inside a single database transaction so a failure at
 * any step rolls back the whole operation and never leaves a partial debit
 * or credit behind.
 */
@Service
public class TransferService {

    private final AccountRepository accountRepository;
    private final TransactionRepository transactionRepository;

    public TransferService(AccountRepository accountRepository,
                            TransactionRepository transactionRepository) {
        this.accountRepository = accountRepository;
        this.transactionRepository = transactionRepository;
    }

    @Transactional
    public TransactionResponse transfer(TransferRequest request, Long requestingCustomerId) {
        if (request.getFromAccountNumber().equals(request.getToAccountNumber())) {
            throw new InvalidAccountStateException("Source and destination accounts must be different");
        }

        // Lock both accounts in a consistent order (by account number) to avoid
        // deadlocks between two transfers that touch the same pair of accounts
        // in opposite directions.
        String first = request.getFromAccountNumber().compareTo(request.getToAccountNumber()) < 0
                ? request.getFromAccountNumber() : request.getToAccountNumber();
        String second = first.equals(request.getFromAccountNumber())
                ? request.getToAccountNumber() : request.getFromAccountNumber();

        accountRepository.findByAccountNumberForUpdate(first)
                .orElseThrow(() -> new AccountNotFoundException("Account not found: " + first));
        accountRepository.findByAccountNumberForUpdate(second)
                .orElseThrow(() -> new AccountNotFoundException("Account not found: " + second));

        Account fromAccount = accountRepository.findByAccountNumber(request.getFromAccountNumber())
                .orElseThrow(() -> new AccountNotFoundException(
                        "Account not found: " + request.getFromAccountNumber()));
        Account toAccount = accountRepository.findByAccountNumber(request.getToAccountNumber())
                .orElseThrow(() -> new AccountNotFoundException(
                        "Account not found: " + request.getToAccountNumber()));

        if (!fromAccount.getCustomerId().equals(requestingCustomerId)) {
            throw new AccountAccessDeniedException("Source account does not belong to the authenticated customer");
        }

        if (!"ACTIVE".equals(fromAccount.getStatus())) {
            throw new InvalidAccountStateException("Source account is not active");
        }
        if (!"ACTIVE".equals(toAccount.getStatus())) {
            throw new InvalidAccountStateException("Destination account is not active");
        }

        BigDecimal amount = request.getAmount();
        if (fromAccount.getBalance().compareTo(amount) < 0) {
            throw new InsufficientBalanceException("Insufficient balance in source account");
        }

        fromAccount.setBalance(fromAccount.getBalance().subtract(amount));
        toAccount.setBalance(toAccount.getBalance().add(amount));

        accountRepository.save(fromAccount);
        accountRepository.save(toAccount);

        Transaction transaction = new Transaction();
        transaction.setReferenceId(UUID.randomUUID().toString());
        transaction.setFromAccount(fromAccount);
        transaction.setToAccount(toAccount);
        transaction.setAmount(amount);
        transaction.setTransactionType("TRANSFER");
        transaction.setStatus("SUCCESS");
        transaction.setDescription(request.getDescription());

        Transaction saved = transactionRepository.save(transaction);

        return new TransactionResponse(saved.getReferenceId(), fromAccount.getAccountNumber(),
                toAccount.getAccountNumber(), saved.getAmount(), saved.getTransactionType(),
                saved.getStatus(), saved.getDescription(), saved.getCreatedAt());
    }
}
