package com.example.demo.Controller;

import com.example.demo.Models.Account;
import com.example.demo.Services.AccountService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class AccountController {

    private final AccountService accountService;

    public AccountController(AccountService accountService) {

        this.accountService = accountService;
    }

    @GetMapping("/")
    public String index(Model model) {
        model.addAttribute("accounts", accountService.getAll());
        return "index";
    }
    @GetMapping("/account/transfer")
    public String getAllAccount(Model model)
    {
        model.addAttribute("accounts", accountService.getAllAccount());
        return "Transfer";
    }

}
