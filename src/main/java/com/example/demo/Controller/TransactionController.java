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

@Controller
@RequestMapping("/transaction")
public class TransactionController {

    private final TransactionService transactionService;
    private final TransactionTypeService transactionTypeService;
    private final AccountService accountService;

    public TransactionController(TransactionService transactionService, TransactionTypeService transactionTypeService, AccountService accountService) {
        this.transactionService = transactionService;
        this.transactionTypeService = transactionTypeService;
        this.accountService = accountService;
    }

    @GetMapping("/new/{id}")
    public String newTransaction(
            @PathVariable Long id,
            Model model) {

        Transaction transaction = new Transaction();

        model.addAttribute("transaction", transaction);
        model.addAttribute("accountId", id);
        List<TransactionType> types =
                transactionTypeService.getAllTransactionType();

        model.addAttribute("transactionTypes", types);

        //model.addAttribute("transactionTypes",
        //        transactionTypeService.getAllTransactionType());

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
        ValidBalance validBalance = transactionService
                .isEnoughFunction(accountId, transaction);
        if (validBalance.isValid ) {

            BigDecimal amount = transaction.getAmount();

            if (validBalance.balance.compareTo(amount) < 0) {

                model.addAttribute("errorMessage",
                        "You don't have enough funds. Your available balance is "
                                + validBalance.balance);

                model.addAttribute("transaction", transaction);

                return "transaction";
            }
        }
        transactionService.saveTransaction(transaction, accountId);
            model.addAttribute("accountId", accountId);
            List<Transaction> transactions = transactionService.getTransactionsByAccountId(accountId);
            model.addAttribute("transactions", transactions);
        return "redirect:/transaction/getlist/" + accountId;

    }

        @PostMapping("/transfer")
        public String getTransfer(
                @RequestParam Long fromAccountId,
                @RequestParam Long toAccountId,
                @RequestParam BigDecimal amount,
                Model model) {

            try {
                transactionService.UpdateTransferProcess(
                        fromAccountId, toAccountId, amount);

                return "redirect:/transaction/list";
            }
            catch (IllegalArgumentException ex) {

                model.addAttribute("errorMessage", ex.getMessage());
                model.addAttribute("accounts", accountService.getAllAccount());

                return "index";  // Return your transfer page
            }
        }


}