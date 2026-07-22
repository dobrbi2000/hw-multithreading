package com.example.task5;

import com.example.task5.dao.AccountDao;
import com.example.task5.dao.FileAccountDao;
import com.example.task5.model.CurrencyCode;
import com.example.task5.model.UserAccount;

import java.io.File;
import java.math.BigDecimal;

public class Task5DaoTest {

    public static void main(String[] args) throws Exception {
        AccountDao accountDao = new FileAccountDao(new File("data/accounts"));

        UserAccount account = new UserAccount("acc-1");
        account.setBalance(CurrencyCode.USD, new BigDecimal("1000.00"));
        account.setBalance(CurrencyCode.EUR, new BigDecimal("250.00"));

        accountDao.save(account);

        UserAccount loaded = accountDao.load("acc-1");

        System.out.println(loaded);
    }
}