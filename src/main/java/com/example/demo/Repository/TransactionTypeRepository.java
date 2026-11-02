package com.example.demo.Repository;
import com.example.demo.Models.Transaction;
import com.example.demo.Models.TransactionType;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.util.List;

public interface TransactionTypeRepository extends JpaRepository<TransactionType, Long> {

    @Query("""
    SELECT t FROM TransactionType t
    WHERE t.typeName = :typename
    """)
    TransactionType findTransactionTypeByTitle(
            @Param("typename") String title);

}
