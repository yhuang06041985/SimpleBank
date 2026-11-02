package com.example.demo.Models;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import com.example.demo.Models.Transaction;

@Entity
@Table(name="TransactionType")
public class TransactionType {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @OneToMany(mappedBy = "transactionType")
    private List<Transaction> transactions;

    private  String description;
    private String typeName;

    public TransactionType()
    {

    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setTypeName(String typeName)
    {
        this.typeName = typeName;
    }

    public String getTypeName()
    {
        return typeName;
    }

    public void setTransactions(List<Transaction> transactions)
    {
        this.transactions = transactions;
    }

    public List<Transaction> getTransactions()
    {
        return  this.transactions;
    }

    public void setDescription(String description)
    {
        this.description = description;
    }

    public  String getDescription()
    {
        return this.description;
    }
}
