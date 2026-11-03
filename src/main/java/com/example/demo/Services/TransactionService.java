package com.example.demo.Services;
import com.example.demo.Models.Account;

import com.example.demo.Repository.AccountTransactionRepository;
import org.springframework.cglib.core.Local;
import org.springframework.http.HttpStatus;

import com.example.demo.Repository.TransactionRepository;
import com.example.demo.Models.Transaction;
import org.springframework.cglib.core.Local;

import com.example.demo.Services.AccountService;
import com.example.demo.Models.AccountTransaction;
import com.example.demo.Models.TransactionType;
import com.example.demo.Services.TransactionTypeService;
import com.example.demo.Services.TransactionTypeService;
import org.springframework.transaction.annotation.Transactional;
import com.example.demo.Models.AccountType;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import java.math.BigDecimal;
import java.util.Optional;
import java.math.BigDecimal;
import  com.example.demo.Repository.AccountRepository;
import com.example.demo.Models.ValidBalance;

@Service
public class TransactionService {

private final TransactionRepository transactionRepository;
private final AccountService accountService;
private final AccountTransactionService accountTransactionService;
private final TransactionTypeService transactionTypeService;
private final AccountTransactionRepository accountTransactionRepository;
private final AccountRepository accountRepository;

public TransactionService(TransactionRepository transactionRepository,
                          AccountRepository accountRepository,
                          AccountService accountService, AccountTransactionService accountTransactionService,
                          TransactionTypeService transactionTypeService, AccountTransactionRepository accountTransactionRepository)
{
    this.transactionRepository = transactionRepository;
    this.accountService = accountService;
    this.accountTransactionService = accountTransactionService;
    this.transactionTypeService = transactionTypeService;
    this.accountTransactionRepository = accountTransactionRepository;
    this.accountRepository = accountRepository;
}

@Transactional
public void saveTransaction(Transaction transaction, Long accountId)
  {
        Account account = accountService.getAccountById(accountId);
    TransactionType transactionType = transactionTypeService
            .getTransactionTypeById(transaction.getTransactionType().getId());

    transaction.setAccount(account);
    transaction.setTransactionType(transactionType);
    transaction = transactionRepository.save(transaction);
    transaction = saveAccount(transaction);

    saveAccountTransaction(transaction);

  }

private Transaction saveAccount(Transaction transaction) {

    AccountType accountType = transaction.getAccount()
            .getAccountType();
    String accountTypes = accountType.getTitle();

    if ("Debit Card".equals(accountTypes)) {
        return saveChecking(transaction);
    }
    else {
        return saveCreditCard(transaction);
    }
}

    private Transaction saveChecking(Transaction transaction) {

        Account account = transaction.getAccount();

        String typeName = transaction.getTransactionType().getTypeName();

        BigDecimal balance = account.getBalance();
        BigDecimal amount = transaction.getAmount();
        BigDecimal finalBalance = balance;

        switch (typeName) {

            case "Deposit":
                finalBalance = balance.add(amount);
                break;

            case "Withdrow":
                if (balance.compareTo(amount) < 0) {
                    throw new IllegalArgumentException(
                            "Insufficient balance. Your available balance is $" + balance);
                }
                    finalBalance = balance.subtract(amount);
                break;

            case "Purchase":
                if (balance.compareTo(amount) < 0) {
                    throw new IllegalArgumentException(
                            "Insufficient balance. Your available balance is $" + balance);
                }
                    finalBalance = balance.subtract(amount);
                break;

            case "Transfer":
                if (balance.compareTo(amount) < 0) {
                    throw new IllegalArgumentException(
                            "Insufficient balance. Your available balance is $" + balance);
                }
                    finalBalance = balance.subtract(amount);
                break;

            default:
                throw new IllegalArgumentException(
                        "Invalid checking transaction type: " + typeName);
        }

        account.setBalance(finalBalance);
        LocalDateTime date = transaction.getTransactionDate();
        account.setLastUpdate(date);
        accountService.saveAccount(account);

        return transaction;
    }

    private Transaction saveCreditCard(Transaction transaction) {

        Account account = transaction.getAccount();

        String typeName = transaction.getTransactionType().getTypeName();

        BigDecimal balance = account.getBalance();
        BigDecimal amount = transaction.getAmount();
        BigDecimal finalBalance = balance;

        switch (typeName) {

            case "Purchase":
                finalBalance = balance.add(amount);
                break;

            case "Payment":
                finalBalance = balance.subtract(amount);
                break;

            case "Refund":
                finalBalance = balance.subtract(amount);
                break;

            default:
                throw new IllegalArgumentException(
                        "Invalid credit card transaction type: " + typeName);
        }

        account.setBalance(finalBalance);
        LocalDateTime date = transaction.getTransactionDate();
        account.setLastUpdate(date);
        accountService.saveAccount(account);

        return transaction;
    }

    private void saveAccountTransaction(Transaction transaction) {

        AccountTransaction accountTransaction = new AccountTransaction();
        Account account = transaction.getAccount();

        Long transactionId = transaction.getId();
        BigDecimal balance = account.getBalance();
        LocalDateTime date = transaction.getTransactionDate();

        accountTransaction.setBalance(balance);
        accountTransaction.setCreateDate(date);
        accountTransaction.setAccount(account);
        accountTransaction.setTransaction(transaction);

        accountTransactionService.saveAccountTransaction(accountTransaction);
    }

    public List<Transaction> getTransactionsByAccountId(Long accountId) {
        return transactionRepository
                .findByAccount_IdOrderByTransactionDateDesc(accountId);
    }

    public List<Transaction> getTransactionByAccountIdAndAmountGreater(Long accountId, BigDecimal amount) {
        return transactionRepository.findByAccount_IdAndAmountGreaterThan(accountId, amount);
    }

    public Transaction updateTransaction(Long accountId, Transaction input) {
        Transaction existing = transactionRepository.findById(accountId)
                .orElseThrow(() ->
                        new RuntimeException("Transaction not found"));

        existing.setCompany(input.getCompany());
        existing.setAmount(input.getAmount());
        existing.setTransactionDate(input.getTransactionDate());

        return transactionRepository.save(existing);
    }


    public Transaction getTransactionById(Long id)
    {
        Transaction transaction =  transactionRepository.findById(id)
         .orElseThrow(() ->
            new RuntimeException("Transaction not found"));
         return transaction;
    }

    public List<Transaction> searchTransactions(
            Long accountId,
            LocalDateTime fromDate,
            LocalDateTime toDate,
            BigDecimal amount
    )
    {
        return transactionRepository.searchTransactions(accountId, fromDate, toDate, amount);
    }
@Transactional
public void UpdateTransferProcess(Long fromAccountId, Long toAccountId, BigDecimal amount)
{
    Account fromAccount = accountService.getAccountById(fromAccountId);
    Account toAccount = accountService.getAccountById(toAccountId);
    UpdateBothAccont(fromAccount, toAccount, amount);
    return;
}

private void UpdateBothAccont(Account fromAccount, Account toAccount, BigDecimal amount)
{
    updateTransferOneAccount(fromAccount, amount);
    updateTransferOneAccount(toAccount, amount);
}

private void updateTransferOneAccount(Account account, BigDecimal amount)
{
    account = updateBankBalance(account, amount);
    Transaction transaction = createTransaction(account, amount);
    AccountTransaction accountTransaction = createAccountTransaction(account, transaction);
}

private Account updateBankBalance(Account account, BigDecimal amount)
{
    BigDecimal balance = account.getBalance();
        balance = balance.subtract(amount);
    account.setBalance(balance);
    LocalDateTime date = LocalDateTime.now();
    account.setLastUpdate(date);
    accountService.saveAccount(account);
    return account;
}

    private Transaction createTransaction(Account account, BigDecimal amount)
    {
        Transaction transaction = new Transaction();
        transaction.setAccount(account);

        if (account.getAccountType().getTitle()
                .equalsIgnoreCase("Debit Card")) {

            TransactionType transactionType =
                    transactionTypeService.getTransactionTypeByTitle("Withdrow");

            transaction.setTransactionType(transactionType);
        }
        else {

            TransactionType transactionType =
                    transactionTypeService.getTransactionTypeByTitle("Payment");

            transaction.setTransactionType(transactionType);
        }

        LocalDateTime date = LocalDateTime.now();

        transaction.setTransactionDate(date);
        transaction.setAmount(amount);

        return transactionRepository.save(transaction);
    }

private AccountTransaction createAccountTransaction(Account account, Transaction transaction)
{
    AccountTransaction accountTransaction = new AccountTransaction();
    accountTransaction.setAccount(account);
    accountTransaction.setTransaction(transaction);
    LocalDateTime date = LocalDateTime.now();
    accountTransaction.setCreateDate(date);
    accountTransaction.setBalance(account.getBalance());
    return accountTransactionRepository.save(accountTransaction);
}

public  ValidBalance isEnoughFunction(Long accountId, Transaction transaction)
{
    ValidBalance validBalance = new ValidBalance();
    validBalance.isValid = false;
    Account account = accountService.getAccountById(accountId);
    String accountType = account.getAccountType().getTitle();
    if (accountType == "Debit Card") {
            String transactionType = transaction.getTransactionType().getTypeName();
            if (transactionType == "Withdrow")
            {
                BigDecimal balance = transaction.getAccount().getBalance();
                validBalance.isValid = true;
                validBalance.balance = balance;

            }
    }
    return validBalance;
}

public void validateEnoughFund(Long accountId, BigDecimal amount) {

    Account account = accountRepository.findById(accountId)
            .orElseThrow(() ->
                    new IllegalArgumentException("Account not found"));

    // Validate withdrawal balance
    if (amount.compareTo(account.getBalance()) > 0) {
        throw new IllegalArgumentException(
                "Insufficient funds. Available balance: "
                        + account.getBalance());

    }
}


}
