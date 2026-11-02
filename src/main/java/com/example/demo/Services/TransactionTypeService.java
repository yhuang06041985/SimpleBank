package com.example.demo.Services;

import com.example.demo.Models.TransactionType;
import com.example.demo.Repository.TransactionTypeRepository;
import org.springframework.stereotype.Service;
import java.util.List;


@Service
public class TransactionTypeService {
    private final TransactionTypeRepository  transactionTypeRepository;
    public TransactionTypeService(TransactionTypeRepository transactionTypeRepository)
    {
        this.transactionTypeRepository = transactionTypeRepository;
    }

    public List<TransactionType> getAllTransactionType()
    {
        return this.transactionTypeRepository.findAll();
    }

    public TransactionType getTransactionTypeById(Long id)
    {
        return transactionTypeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException(
                        "TransactionType not found: " + id));
    }

    public TransactionType getTransactionTypeByTitle(String name)
    {
        return transactionTypeRepository.findTransactionTypeByTitle(name);
    }
}
