package com.example.task5.model;

import java.math.BigDecimal;
import java.util.EnumMap;
import java.util.Map;

public class UserAccount {

    private final String accountId;
    private final Map<CurrencyCode, BigDecimal> balances;

    public UserAccount(String accountId) {
        this.accountId = accountId;
        this.balances = new EnumMap<CurrencyCode, BigDecimal>(CurrencyCode.class);
    }

    public String getAccountId() {
        return accountId;
    }

    public BigDecimal getBalance(CurrencyCode currencyCode) {
        BigDecimal balance = balances.get(currencyCode);
        return balance != null ? balance : BigDecimal.ZERO;
    }

    public void setBalance(CurrencyCode currencyCode, BigDecimal amount) {
        balances.put(currencyCode, amount);
    }

    public Map<CurrencyCode, BigDecimal> getBalances() {
        return new EnumMap<CurrencyCode, BigDecimal>(balances);
    }

    @Override
    public String toString() {
        return "UserAccount{" +
                "accountId='" + accountId + '\'' +
                ", balances=" + balances +
                '}';
    }

}
