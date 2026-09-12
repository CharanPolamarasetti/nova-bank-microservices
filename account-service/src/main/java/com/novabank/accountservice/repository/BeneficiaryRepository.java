package com.novabank.accountservice.repository;

import com.novabank.accountservice.entity.Beneficiary;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BeneficiaryRepository extends JpaRepository<Beneficiary, Long> {

    List<Beneficiary> findByOwnerAccountId(Long ownerAccountId);
}
