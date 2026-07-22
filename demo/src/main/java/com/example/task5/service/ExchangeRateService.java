package com.example.task5.service;

import java.util.Map;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.logging.Logger;

import com.example.task5.exception.ExchangeRateNotFoundException;
import com.example.task5.exception.ValidationException;
import com.example.task5.model.CurrencyCode;
import com.example.task5.model.ExchangeRate;

public class ExchangeRateService {

    private static final Logger LOGGER = Logger.getLogger(ExchangeRateService.class.getName());

    private final Map<String, ExchangeRate> rates = new HashMap<String, ExchangeRate>();

    public synchronized void addRate(CurrencyCode fromCurrency,
            CurrencyCode toCurrency,
            BigDecimal rate) throws ValidationException {

        if (fromCurrency == null) {
            throw new ValidationException("From currency must not be null");
        }

        if (toCurrency == null) {
            throw new ValidationException("To currency must not be null");
        }

        if (rate == null) {
            throw new ValidationException("Rate must not be null");
        }

        if (fromCurrency == toCurrency) {
            throw new ValidationException("From and to currencies must be different");
        }

        if (rate.compareTo(BigDecimal.ZERO) <= 0) {
            throw new ValidationException("Rate must be greater than zero");
        }

        ExchangeRate exchangeRate = new ExchangeRate(fromCurrency, toCurrency, rate);

        String key = buildKey(fromCurrency, toCurrency);

        rates.put(key, exchangeRate);

        LOGGER.info("Exchange rate added: " + fromCurrency + " -> " + toCurrency + " = " + rate);
    }

    public synchronized ExchangeRate getRate(CurrencyCode fromCurrency,
            CurrencyCode toCurrency) throws ValidationException, ExchangeRateNotFoundException {
        if (fromCurrency == null) {
            throw new ValidationException("From currency must not be null");
        }

        if (toCurrency == null) {
            throw new ValidationException("To currency must not be null");
        }

        if (fromCurrency == toCurrency) {
            throw new ValidationException("From and to currencies must be different");
        }

        String key = buildKey(fromCurrency, toCurrency);

        ExchangeRate exchangeRate = rates.get(key);

        if (exchangeRate == null) {
            throw new ExchangeRateNotFoundException(
                    "Exchange rate not found for " + fromCurrency + " -> " + toCurrency);
        }

        LOGGER.info("Exchange rate found: " + fromCurrency + " -> " + toCurrency
                + " = " + exchangeRate.getRate());

        return exchangeRate;
    }

    private String buildKey(CurrencyCode fromCurrency, CurrencyCode toCurrency) {
        return fromCurrency.name() + "_" + toCurrency.name();
    }

}
