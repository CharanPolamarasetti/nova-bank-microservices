package com.novabank.accountservice.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class TransactionResponse {

    private String referenceId;
    private String fromAccountNumber;
    private String toAccountNumber;
    private BigDecimal amount;
    private String transactionType;
    private String status;
    private String description;
    private LocalDateTime createdAt;

    public TransactionResponse(String referenceId, String fromAccountNumber, String toAccountNumber,
                                BigDecimal amount, String transactionType, String status,
                                String description, LocalDateTime createdAt) {
        this.referenceId = referenceId;
        this.fromAccountNumber = fromAccountNumber;
        this.toAccountNumber = toAccountNumber;
        this.amount = amount;
        this.transactionType = transactionType;
        this.status = status;
        this.description = description;
        this.createdAt = createdAt;
    }

    public String getReferenceId() { return referenceId; }
    public String getFromAccountNumber() { return fromAccountNumber; }
    public String getToAccountNumber() { return toAccountNumber; }
    public BigDecimal getAmount() { return amount; }
    public String getTransactionType() { return transactionType; }
    public String getStatus() { return status; }
    public String getDescription() { return description; }
    public LocalDateTime getCreatedAt() { return createdAt; }
}
