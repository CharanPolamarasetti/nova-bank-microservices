package com.novabank.accountservice.dto;

import java.time.LocalDateTime;

public class BeneficiaryResponse {

    private Long id;
    private String beneficiaryAccountNumber;
    private String beneficiaryName;
    private String nickname;
    private LocalDateTime createdAt;

    public BeneficiaryResponse(Long id, String beneficiaryAccountNumber, String beneficiaryName,
                                String nickname, LocalDateTime createdAt) {
        this.id = id;
        this.beneficiaryAccountNumber = beneficiaryAccountNumber;
        this.beneficiaryName = beneficiaryName;
        this.nickname = nickname;
        this.createdAt = createdAt;
    }

    public Long getId() { return id; }
    public String getBeneficiaryAccountNumber() { return beneficiaryAccountNumber; }
    public String getBeneficiaryName() { return beneficiaryName; }
    public String getNickname() { return nickname; }
    public LocalDateTime getCreatedAt() { return createdAt; }
}
