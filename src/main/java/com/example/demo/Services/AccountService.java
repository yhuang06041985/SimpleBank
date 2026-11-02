package com.example.demo.Services;

import com.example.demo.Models.Account;
import com.example.demo.Repository.AccountRepository;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AccountService {

    private final AccountRepository accountRepository;

    public AccountService(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    public List<Account> getAll() {
        return accountRepository.findAll();
    }
    public Account getAccountById(Long accountId)
    {
        return accountRepository.findById(accountId)
                .orElseThrow(() -> new RuntimeException(
                        "TransactionType not found: " + accountId));

    }

    public List<Account> getAllAccount()
    {
        return accountRepository.findAll(Sort.by(Sort.Direction.ASC, "id"));
    }

    public Account saveAccount(Account account)
    {
        return accountRepository.save(account);
    }
}