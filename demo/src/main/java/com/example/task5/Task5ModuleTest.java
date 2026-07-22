package com.example.task5;

import com.example.task5.model.CurrencyCode;
import com.example.task5.model.UserAccount;
import com.example.task5.module.ExchangeModule;

import java.io.File;
import java.math.BigDecimal;

public class Task5ModuleTest {

    public static void main(String[] args) throws Exception {
        ExchangeModule module = new ExchangeModule(new File("data/accounts"));

        if (!module.exists("acc-200")) {
            UserAccount account = new UserAccount("acc-200");
            account.setBalance(CurrencyCode.USD, new BigDecimal("1000.00"));
            account.setBalance(CurrencyCode.EUR, new BigDecimal("50.00"));
            module.createAccount(account);
        }

        module.addExchangeRate(CurrencyCode.USD, CurrencyCode.EUR, new BigDecimal("0.92"));

        BigDecimal converted = module.exchange(
                "acc-200",
                CurrencyCode.USD,
                CurrencyCode.EUR,
                new BigDecimal("100.00"));

        System.out.println("Converted amount: " + converted);
        System.out.println("Updated account: " + module.getAccount("acc-200"));
    }
}