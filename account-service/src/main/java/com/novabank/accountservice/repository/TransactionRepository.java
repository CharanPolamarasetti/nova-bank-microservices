package com.novabank.accountservice.repository;

import com.novabank.accountservice.entity.Transaction;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {

    @Query("select t from Transaction t where t.fromAccount.id = :accountId or t.toAccount.id = :accountId " +
           "order by t.createdAt desc")
    Page<Transaction> findByAccountId(@Param("accountId") Long accountId, Pageable pageable);
}
