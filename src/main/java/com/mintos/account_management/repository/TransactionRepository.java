package com.mintos.account_management.repository;

import com.mintos.account_management.model.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface TransactionRepository extends PagingAndSortingRepository<Transaction, Long>, JpaRepository<Transaction, Long> {

    @Query(value = "SELECT * FROM am_transactions WHERE account_id = :accountId ORDER BY timestamp DESC LIMIT :limit OFFSET :offset", nativeQuery = true)
    List<Transaction> findTransactionsWithOffsetAndLimit(@Param("accountId") Long accountId,
                                                         @Param("offset") int offset,
                                                         @Param("limit") int limit);

}
