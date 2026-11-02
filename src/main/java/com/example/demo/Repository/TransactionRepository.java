package com.example.demo.Repository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Page;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import com.example.demo.Models.Transaction;
import java.time.LocalDateTime;
import java.time.LocalDate;
import org.springframework.data.repository.query.Param;
import org.springframework.data.jpa.repository.Query;

import org.springframework.stereotype.Repository;

@Repository
public interface TransactionRepository extends  JpaRepository<Transaction, Long> {
    List<Transaction> findByAccount_Id(Long accountId);

    // Get transactions for one account, newest first
    List<Transaction> findByAccount_IdOrderByTransactionDateDesc(
            Long accountId
    );

    List<Transaction> findByAccount_IdAndAmountGreaterThan(
            Long accountId,
            BigDecimal amount
    );

    //Find  transaction based on account id, and start date and end date
    @Query("""
    SELECT t
    FROM Transaction t
    WHERE t.account.id = :accountId
      AND t.transactionDate BETWEEN :startDate AND :endDate
    ORDER BY t.transactionDate DESC
    """)
    List<Transaction> findTransactionsByAccountAndDateRange(
            @Param("accountId") Long accountId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate
    );

    @Query("""
    SELECT t
    FROM Transaction t
    WHERE t.account.id = :accountId
      AND t.amount > :amount
    ORDER BY t.transactionDate DESC
    """)
    List<Transaction> findTransactionsByAccountAndAmount(
            @Param("accountId") Long accountId,
            @Param("amount") BigDecimal amount

    );

    @Query("""
    SELECT t FROM Transaction t
    WHERE t.account.id = :accountId
      AND (:fromDate IS NULL OR t.transactionDate >= :fromDate)
      AND (:toDate IS NULL OR t.transactionDate <= :toDate)
      AND (:amount IS NULL OR t.amount >= :amount)
    ORDER BY t.transactionDate DESC
    """)
    List<Transaction> searchTransactions(
            Long accountId,
            LocalDateTime fromDate,
            LocalDateTime toDate,
            BigDecimal amount
    );
}
