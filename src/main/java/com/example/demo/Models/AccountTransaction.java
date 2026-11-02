package com.example.demo.Models;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import com.example.demo.Models.Transaction;
import com.example.demo.Models.Account;
import org.apache.el.lang.ELArithmetic;
import java.time.LocalDate;

@Entity
@Table(name="AccountTransaction")
public class AccountTransaction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "account_id", nullable = false)
    private Account account;

    private BigDecimal balance;
    @Column(name="create_date")
    private LocalDateTime createDate;
    @OneToOne
    @JoinColumn(name = "transaction_id", nullable = false, unique = true)
    private Transaction transaction;

    public AccountTransaction() {

    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setTransaction(Transaction transaction)
    {
        this.transaction = transaction;
    }
    public Transaction getTransaction()
    {
        return this.transaction;
    }
    public void setAccount(Account account)
    {
        this.account = account;
    }
    public Account getAccount()
    {
        return this.account;
    }

    public void setBalance(BigDecimal balance)
    {
        this.balance = balance;
    }
    public BigDecimal getBalance()
    {
        return  balance;
    }
    public void setCreateDate(LocalDateTime createDate)
    {
        this.createDate = createDate;
    }
    public LocalDateTime getCreateDate()
    {
        return this.createDate;
    }
}
