package com.example.demo.Services;

import com.example.demo.Models.Account;
import com.example.demo.Repository.AccountRepository;
import com.example.demo.Repository.AccountTransactionRepository;
import org.springframework.stereotype.Service;
import com.example.demo.Models.AccountTransaction;
import com.example.demo.Repository.TransactionTypeRepository;
import com.example.demo.Models.Transaction;

import java.util.List;
@Service
public class AccountTransactionService {
    private final AccountTransactionRepository accountTransactionRepository;

    public AccountTransactionService(AccountTransactionRepository accountTransactionRepository) {
        this.accountTransactionRepository = accountTransactionRepository;
    }

    public List<AccountTransaction> getAccountTransaction() {
        return accountTransactionRepository.findAll();
    }

    public AccountTransaction saveAccountTransaction(AccountTransaction accountTransaction)
    {
        return this.accountTransactionRepository.save(accountTransaction);
    }

    public void deleteAccountTransaction(Transaction transaction)
    {
        this.accountTransactionRepository.deleteByTransactionId(transaction.getId());
    }

}
