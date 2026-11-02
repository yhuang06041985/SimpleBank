package com.example.demo.Repository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.domain.Sort;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import com.example.demo.Models.Account;
import org.springframework.data.repository.query.Param;
import org.springframework.data.jpa.repository.Query;
import com.example.demo.Models.AccountTransaction;
import org.springframework.stereotype.Repository;


@Repository
public interface AccountTransactionRepository extends JpaRepository<AccountTransaction, Long> {
    void deleteByTransactionId(Long transactionId);

}
