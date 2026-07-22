package com.example.task5.service;

import java.math.BigDecimal;
import java.util.logging.Logger;

import com.example.task5.exception.ApplicationException;
import com.example.task5.exception.InsufficientFundsException;
import com.example.task5.exception.ValidationException;
import com.example.task5.model.CurrencyCode;
import com.example.task5.model.ExchangeRate;
import com.example.task5.model.UserAccount;
import com.example.task5.util.AccountLockManager;
import com.example.task5.util.BigDecimalUtils;

public class ExchangeService {

    private static final Logger LOGGER = Logger.getLogger(ExchangeService.class.getName());

    private final AccountService accountService;
    private final ExchangeRateService exchangeRateService;
    private final AccountLockManager lockManager;

    public ExchangeService(AccountService accountService,
            ExchangeRateService exchangeRateService,
            AccountLockManager lockManager) {
        this.accountService = accountService;
        this.exchangeRateService = exchangeRateService;
        this.lockManager = lockManager;
    }

    public BigDecimal exchange(
            String accountId,
            CurrencyCode fromCurrency,
            CurrencyCode toCurrency,
            BigDecimal amount) throws ApplicationException {

        validateExchangeInput(accountId, fromCurrency, toCurrency, amount);

        Object lock = lockManager.getLock(accountId);

        synchronized (lock) {
            LOGGER.info("Exchange started. Account=" + accountId
                    + ", from=" + fromCurrency
                    + ", to=" + toCurrency
                    + ", amount=" + amount);

            UserAccount account = accountService.getAccount(accountId);

            BigDecimal fromBalance = account.getBalance(fromCurrency);

            if (fromBalance.compareTo(fromBalance) < 0) {
                throw new InsufficientFundsException(
                        "Insufficient funds for account " + accountId
                                + ". Available " + fromCurrency + ": " + fromBalance
                                + ", requested: " + amount);
            }

            ExchangeRate exchangeRate = exchangeRateService.getRate(fromCurrency, toCurrency);
            BigDecimal rate = exchangeRate.getRate();

            BigDecimal convertedAmount = BigDecimalUtils.multiply(amount, rate);

            BigDecimal toBalance = account.getBalance(toCurrency);

            BigDecimal newFromBalance = BigDecimalUtils.normalize(fromBalance.subtract(amount));

            BigDecimal newToBalance = BigDecimalUtils.normalize(toBalance.add(convertedAmount));

            account.setBalance(fromCurrency, newFromBalance);
            account.setBalance(toCurrency, newToBalance);

            accountService.saveAccount(account);

            LOGGER.info("Exchange completed. Account=" + accountId
                    + ", debited " + amount + " " + fromCurrency
                    + ", credited " + convertedAmount + " " + toCurrency
                    + ", new balances: "
                    + fromCurrency + "=" + newFromBalance + ", "
                    + toCurrency + "=" + newToBalance);

            return convertedAmount;
        }
    }

    public void validateExchangeInput(String accountId,
            CurrencyCode fromCurrency,
            CurrencyCode toCurrency,
            BigDecimal amount) throws ValidationException {

        if (accountId == null || accountId.trim().isEmpty()) {
            throw new ValidationException("Account id must not be empty");
        }

        if (fromCurrency == null) {
            throw new ValidationException("From currency must not be null");
        }

        if (toCurrency == null) {
            throw new ValidationException("To currency must not be null");
        }

        if (fromCurrency == toCurrency) {
            throw new ValidationException("From and to currencies must be different");
        }

        if (amount == null) {
            throw new ValidationException("Amount must not be null");
        }

        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new ValidationException("Amount must be greater than zero");
        }

    }
}