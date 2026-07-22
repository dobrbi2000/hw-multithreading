package com.example.task5.service;

import java.util.logging.Logger;

import com.example.task5.dao.AccountDao;
import com.example.task5.exception.AccountAlreadyExistsException;
import com.example.task5.exception.ApplicationException;
import com.example.task5.exception.ValidationException;
import com.example.task5.model.UserAccount;

public class AccountService {

    private static final Logger LOGGER = Logger.getLogger(AccountService.class.getName());

    private final AccountDao accountDao;

    public AccountService(AccountDao accountDao) {
        this.accountDao = accountDao;
    }

    public void createAccount(UserAccount account) throws ApplicationException {
        if (account == null) {
            throw new ValidationException("Account must not be null");
        }

        validateAccountId(account.getAccountId());

        LOGGER.info("Creating account: " + account.getAccountId());

        if (accountDao.exists(account.getAccountId())) {
            throw new AccountAlreadyExistsException(
                    "Account already exists: " + account.getAccountId());
        }

        accountDao.save(account);

        LOGGER.info("Account created: " + account.getAccountId());
    }

    public UserAccount getAccount(String accountId) throws ApplicationException {
        validateAccountId(accountId);

        LOGGER.info("Loading account: " + accountId);

        return accountDao.load(accountId);

    }

    public void saveAccount(UserAccount account) throws ApplicationException {
        if (account == null) {
            throw new ValidationException("Account must not be null");
        }

        validateAccountId(account.getAccountId());

        LOGGER.info("Saving account: " + account.getAccountId());

        accountDao.save(account);

    }

    public boolean exists(String accountId) throws ApplicationException {

        validateAccountId(accountId);

        return accountDao.exists(accountId);
    }

    public void validateAccountId(String accountId) throws ValidationException {
        if (accountId == null || accountId.trim().isEmpty()) {
            throw new ValidationException("Account id must not be empty");
        }
    }

}
