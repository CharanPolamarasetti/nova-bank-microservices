package com.novabank.accountservice.service;

import com.novabank.accountservice.dto.*;
import com.novabank.accountservice.entity.Account;
import com.novabank.accountservice.entity.Beneficiary;
import com.novabank.accountservice.exception.AccountAccessDeniedException;
import com.novabank.accountservice.exception.AccountNotFoundException;
import com.novabank.accountservice.repository.AccountRepository;
import com.novabank.accountservice.repository.BeneficiaryRepository;
import com.novabank.accountservice.repository.TransactionRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class AccountService {

    private final AccountRepository accountRepository;
    private final BeneficiaryRepository beneficiaryRepository;
    private final TransactionRepository transactionRepository;

    public AccountService(AccountRepository accountRepository,
                           BeneficiaryRepository beneficiaryRepository,
                           TransactionRepository transactionRepository) {
        this.accountRepository = accountRepository;
        this.beneficiaryRepository = beneficiaryRepository;
        this.transactionRepository = transactionRepository;
    }

    public List<AccountResponse> listAccountsForCustomer(Long customerId) {
        return accountRepository.findByCustomerId(customerId).stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    public AccountResponse getAccount(String accountNumber, Long requestingCustomerId) {
        Account account = loadOwnedAccount(accountNumber, requestingCustomerId);
        return toResponse(account);
    }

    public BalanceResponse getBalance(String accountNumber, Long requestingCustomerId) {
        Account account = loadOwnedAccount(accountNumber, requestingCustomerId);
        return new BalanceResponse(account.getAccountNumber(), account.getBalance());
    }

    public Page<TransactionResponse> getTransactionHistory(String accountNumber, Long requestingCustomerId,
                                                             Pageable pageable) {
        Account account = loadOwnedAccount(accountNumber, requestingCustomerId);
        return transactionRepository.findByAccountId(account.getId(), pageable)
                .map(t -> new TransactionResponse(
                        t.getReferenceId(),
                        t.getFromAccount() != null ? t.getFromAccount().getAccountNumber() : null,
                        t.getToAccount() != null ? t.getToAccount().getAccountNumber() : null,
                        t.getAmount(),
                        t.getTransactionType(),
                        t.getStatus(),
                        t.getDescription(),
                        t.getCreatedAt()));
    }

    @Transactional
    public BeneficiaryResponse addBeneficiary(String accountNumber, Long requestingCustomerId,
                                               BeneficiaryRequest request) {
        Account account = loadOwnedAccount(accountNumber, requestingCustomerId);

        Beneficiary beneficiary = new Beneficiary();
        beneficiary.setOwnerAccount(account);
        beneficiary.setBeneficiaryAccountNumber(request.getBeneficiaryAccountNumber());
        beneficiary.setBeneficiaryName(request.getBeneficiaryName());
        beneficiary.setNickname(request.getNickname());

        Beneficiary saved = beneficiaryRepository.save(beneficiary);
        return toResponse(saved);
    }

    public List<BeneficiaryResponse> listBeneficiaries(String accountNumber, Long requestingCustomerId) {
        Account account = loadOwnedAccount(accountNumber, requestingCustomerId);
        return beneficiaryRepository.findByOwnerAccountId(account.getId()).stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    /**
     * Loads an account and verifies it belongs to the requesting customer.
     * Ownership is enforced here (not just at the JWT layer) so that a valid
     * token for customer A can never read or act on customer B's account
     * just by guessing an account number.
     */
    private Account loadOwnedAccount(String accountNumber, Long requestingCustomerId) {
        Account account = accountRepository.findByAccountNumber(accountNumber)
                .orElseThrow(() -> new AccountNotFoundException("Account not found: " + accountNumber));

        if (!account.getCustomerId().equals(requestingCustomerId)) {
            throw new AccountAccessDeniedException("Account does not belong to the authenticated customer");
        }

        return account;
    }

    private AccountResponse toResponse(Account a) {
        return new AccountResponse(a.getAccountNumber(), a.getAccountType(), a.getBalance(),
                a.getStatus(), a.getCreatedAt());
    }

    private BeneficiaryResponse toResponse(Beneficiary b) {
        return new BeneficiaryResponse(b.getId(), b.getBeneficiaryAccountNumber(), b.getBeneficiaryName(),
                b.getNickname(), b.getCreatedAt());
    }
}
