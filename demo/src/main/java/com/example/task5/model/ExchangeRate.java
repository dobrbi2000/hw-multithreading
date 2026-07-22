package com.example.task5.model;

import java.math.BigDecimal;

public class ExchangeRate {

    private final CurrencyCode fromCurrency;
    private final CurrencyCode toCurrency;
    private final BigDecimal rate;

    public ExchangeRate(CurrencyCode fromCurrency, CurrencyCode toCurrency, BigDecimal rate) {
        this.fromCurrency = fromCurrency;
        this.toCurrency = toCurrency;
        this.rate = rate;
    }

    public CurrencyCode getfromCurrency() {
        return fromCurrency;
    }

    public CurrencyCode getToCurrencyCode() {
        return toCurrency;
    }

    public BigDecimal getRate() {
        return rate;
    }

    @Override
    public String toString() {
        return "ExchangeRate{" +
                "fromCurrency=" + fromCurrency +
                ", toCurrency=" + toCurrency +
                ", rate=" + rate +
                '}';

    }

}
