
package com.example.demo.Models;

import jakarta.persistence.*;
import org.springframework.lang.NonNull;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.LocalDate;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;

@Entity
@Table(name = "[Transaction]")
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "account_id")
    private Account account;

    @NotNull(message = "Amount is required")
    @DecimalMin(value = "0.01", message = "Amount must be greater than zero")
    private BigDecimal amount;
    private String company;
    private String description;

    @NotNull(message = "Transaction type  is required")
    @ManyToOne
    @JoinColumn(name = "transaction_type_id")
    private TransactionType transactionType;

    @Column(name = "company_phone_number")
    private String companyPhoneNbr;

    @NotNull(message = "Transaction date is required")
    @Column(name = "transaction_date")
    private LocalDateTime transactionDate;

    @OneToOne(mappedBy = "transaction")
    private AccountTransaction accountTransaction;


    public Transaction() {
        this.account = new Account();
        this.transactionType = new TransactionType();
    }

    public void setAccountTransaction(AccountTransaction accountTransaction)
    {
        this.accountTransaction = accountTransaction;
    }

    public AccountTransaction getAccountTransaction()
    {
        return  this.accountTransaction;
    }
    public Long getId() {
        return id;
    }

    public Account getAccount() {
        return account;
    }

    public void setAccount(Account account) {
        this.account = account;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public TransactionType getTransactionType() {
        return transactionType;
    }

    public void setTransactionType(TransactionType transactionType) {
        this.transactionType = transactionType;
    }

    public String getCompanyPhoneNbr() {
        return companyPhoneNbr;
    }

    public void setCompanyPhoneNbr(String companyPhoneNbr) {
        this.companyPhoneNbr = companyPhoneNbr;
    }

    public LocalDateTime getTransactionDate() {
        return transactionDate;
    }

    public void setTransactionDate(LocalDateTime transactionDate) {
        this.transactionDate = transactionDate;
    }
}