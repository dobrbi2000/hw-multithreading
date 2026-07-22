package com.example.task5.module;

import java.io.File;
import java.math.BigDecimal;

import com.example.task5.dao.AccountDao;
import com.example.task5.dao.FileAccountDao;
import com.example.task5.exception.ApplicationException;
import com.example.task5.model.CurrencyCode;
import com.example.task5.model.UserAccount;
import com.example.task5.service.AccountService;
import com.example.task5.service.ExchangeRateService;
import com.example.task5.service.ExchangeService;
import com.example.task5.util.AccountLockManager;

public class ExchangeModule {

    private final AccountService accountService;
    private final ExchangeRateService exchangeRateService;
    private final ExchangeService exchangeService;

    public ExchangeModule(File storageDir) throws ApplicationException {
        AccountDao accountDao = new FileAccountDao(storageDir);

        this.accountService = new AccountService(accountDao);
        this.exchangeRateService = new ExchangeRateService();
        AccountLockManager lockManager = new AccountLockManager();

        this.exchangeService = new ExchangeService(
                accountService,
                exchangeRateService,
                lockManager);

    }

    public void createAccount(String accountId) throws ApplicationException {
        UserAccount account = new UserAccount(accountId);
        accountService.createAccount(account);
    }

    public void createAccount(UserAccount account) throws ApplicationException {
        accountService.createAccount(account);
    }

    public UserAccount getAccount(String accountId) throws ApplicationException {
        return accountService.getAccount(accountId);
    }

    public boolean exists(String accountId) throws ApplicationException {
        return accountService.exists(accountId);
    }

    public void addExchangeRate(CurrencyCode fromCurrency,
            CurrencyCode toCurrency,
            BigDecimal rate) throws ApplicationException {
        exchangeRateService.addRate(fromCurrency, toCurrency, rate);
    }

    public BigDecimal exchange(String accountId,
            CurrencyCode fromCurrency,
            CurrencyCode toCurrency,
            BigDecimal amount) throws ApplicationException {
        return exchangeService.exchange(accountId, fromCurrency, toCurrency, amount);
    }
}
