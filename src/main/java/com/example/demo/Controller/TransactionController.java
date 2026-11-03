package com.example.demo.Controller;

import com.example.demo.Models.Account;
import com.example.demo.Models.Transaction;
import com.example.demo.Models.ValidBalance;
import com.example.demo.Services.AccountService;
import com.example.demo.Services.TransactionService;

import com.example.demo.Services.TransactionTypeService;
import org.springframework.web.bind.annotation.*;
import org.springframework.ui.Model;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.time.LocalDateTime;
import java.time.LocalDate;

import com.example.demo.Models.TransactionType;
import com.example.demo.Services.TransactionTypeService;
import java.math.BigDecimal;
import java.lang.Long;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Size;

import jakarta.validation.Valid;
import org.springframework.validation.BindingResult;
import com.example.demo.Models.ValidBalance;
import com.example.demo.Repository.AccountRepository;

@Controller
@RequestMapping("/transaction")
public class TransactionController {

    private final TransactionService transactionService;
    private final TransactionTypeService transactionTypeService;
    private final AccountService accountService;
    private final AccountRepository accountRepository;

    public TransactionController(TransactionService transactionService, AccountRepository accountRepository,
                                 TransactionTypeService transactionTypeService, AccountService accountService) {
        this.transactionService = transactionService;
        this.transactionTypeService = transactionTypeService;
        this.accountService = accountService;
        this.accountRepository = accountRepository;
    }

    @GetMapping("/new/{id}")
    public String newTransaction(
            @PathVariable Long id,
            Model model) {

        Transaction transaction = new Transaction();
        Account account = accountRepository.findById(id)
                .orElseThrow(() ->
                        new IllegalArgumentException("Account not found"));


        model.addAttribute("transaction", transaction);
        model.addAttribute("accountId", id);
        List<TransactionType> transactionType =
                transactionTypeService.getAllTransactionType();
        if (account.getAccountType().getTitle()
                .equalsIgnoreCase("Debit Card")) {

            transactionType = transactionType.stream()
                    .filter(t -> List.of("Deposit","Withdrow","Purchase","Fee")
                                    .contains(t.getTypeName()))
                            .toList();

        } else if (account.getAccountType().getTitle()
                .equalsIgnoreCase("Credit Card")) {
            transactionType = transactionType.stream()
                    .filter(t -> List.of("Purchase", "Payment", "Refund")
                            .contains(t.getTypeName()))
                    .toList();
        }

        model.addAttribute("transactionTypes", transactionType);

        return "transaction";
    }

    // GET: Get transactions by ID
    @GetMapping("/getlist/{id}")
    public String getTransactionById(
            @PathVariable("id") Long accountId,
            Model model) {

        List<Transaction> transactions = transactionService.getTransactionsByAccountId(accountId);

        Account account = accountService.getAccountById(accountId);
        String accountInfo = account.getAccountType().getDescription();
        model.addAttribute("accountId", accountId);
        model.addAttribute("accountInfo", accountInfo);
        model.addAttribute("transactions", transactions);

        return "transactionList";
    }

    @GetMapping("/list/{id}")
    public String getTransactionByIdAndSearch(
            @PathVariable("id") Long accountId,
            @RequestParam(required = false) LocalDateTime fromDate,
            @RequestParam(required = false) LocalDateTime toDate,
            @RequestParam(required = false) BigDecimal amount,
            Model model) {

        List<Transaction> transactions = transactionService
                .searchTransactions(accountId, fromDate, toDate, amount);

        Account account = accountService.getAccountById(accountId);
        String accountInfo = account.getAccountType().getDescription();
        model.addAttribute("accountId", accountId);
        model.addAttribute("accountInfo", accountInfo);
        model.addAttribute("transactions", transactions);

        return "transactionList";
    }

/*
    @PostMapping("/save")
    public String save(
            @Valid @ModelAttribute("transaction") Transaction transaction,
            BindingResult result,
            @RequestParam Long accountId,
            Model model) {

        if (result.hasErrors()) {

            model.addAttribute("transactionTypes",
                    transactionTypeService.getAllTransactionType());

            model.addAttribute("accountId",
                    accountId);

            return "Transaction";
        }

       // transactionService.saveTransaction(transaction);

        return "redirect:/transactions";
    } */


    // POST: Create a new transaction
    @PostMapping("/save")
    public String createTransaction(
            @ModelAttribute Transaction transaction,
            @RequestParam Long accountId,
            Model model) {
        try {
            transactionService.saveTransaction(transaction, accountId);
            model.addAttribute("accountId", accountId);
            List<Transaction> transactions = transactionService.getTransactionsByAccountId(accountId);
            model.addAttribute("transactions", transactions);
            return "redirect:/transaction/getlist/" + accountId;
        }
        catch (IllegalArgumentException ex) {

            model.addAttribute("errorMessage", ex.getMessage());

            model.addAttribute("transaction", transaction);

            model.addAttribute("transactionTypes",
                    transactionTypeService.getAllTransactionType());

            return "transaction";
        }

    }

    @PostMapping("/transfer")
    public String getTransfer(
            @RequestParam Long fromAccountId,
            @RequestParam Long toAccountId,
            @RequestParam BigDecimal amount,
            Model model) {

        try {
            transactionService.validateEnoughFund(fromAccountId, amount);
            transactionService.UpdateTransferProcess(
                    fromAccountId, toAccountId, amount);

            return "redirect:/";
        }
        catch (IllegalArgumentException ex) {

            model.addAttribute("errorMessage", ex.getMessage());
            model.addAttribute("accounts", accountService.getAllAccount());

            return "transfer";
        }
    }


}