package com.example.task5.dao;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.Currency;
import java.util.Map;
import java.util.Properties;
import java.util.logging.Logger;

import com.example.task5.exception.AccountNotFoundException;
import com.example.task5.exception.DaoException;
import com.example.task5.model.CurrencyCode;
import com.example.task5.model.UserAccount;

public class FileAccountDao implements AccountDao {

    private static final Logger LOGGER = Logger.getLogger(FileAccountDao.class.getName());

    private final File storageDir;

    public FileAccountDao(File storageDir) throws DaoException {
        this.storageDir = storageDir;

        if (!storageDir.exists()) {
            boolean created = storageDir.mkdirs();

            if (!created) {
                throw new DaoException("Failed to create storage directory " + storageDir.getAbsolutePath());
            }
        }

        if (!storageDir.isDirectory()) {
            throw new DaoException("Storage path is not a directory: " + storageDir.getAbsolutePath());
        }
    }

    @Override
    public UserAccount load(String accountId) throws DaoException, AccountNotFoundException {

        File file = getAccountFile(accountId);

        if (!file.exists()) {
            throw new AccountNotFoundException("Account not found: " + accountId);
        }

        LOGGER.info("Loading account from file: " + file.getAbsolutePath());

        Properties properties = new Properties();

        try (FileInputStream inputStream = new FileInputStream(file)) {
            properties.load(inputStream);
        } catch (IOException e) {
            throw new DaoException("Failed to load account: " + accountId, e);
        }

        String storedAccountId = properties.getProperty("accountId", accountId);

        UserAccount account = new UserAccount(storedAccountId);

        for (CurrencyCode currencyCode : CurrencyCode.values()) {
            String value = properties.getProperty(currencyCode.name());

            if (value != null) {
                account.setBalance(currencyCode, new BigDecimal(value));
            }
        }
        return account;

    }

    @Override
    public boolean exists(String accountId) {
        File file = getAccountFile(accountId);
        return file.exists();
    }

    @Override
    public void save(UserAccount account) throws DaoException {
        File file = getAccountFile(account.getAccountId());

        LOGGER.info("Saving account to file: " + file.getAbsolutePath());

        Properties properties = new Properties();

        properties.setProperty("accountId", account.getAccountId());

        for (Map.Entry<CurrencyCode, BigDecimal> entry : account.getBalances().entrySet()) {
            String key = entry.getKey().name();
            String value = entry.getValue().toPlainString();
            properties.setProperty(key, value);
        }

        try (FileOutputStream outputStream = new FileOutputStream(file)) {
            properties.store(outputStream, "User account data");
        } catch (Exception e) {
            throw new DaoException("Failed to save account: " + account.getAccountId(), e);
        }

    }

    private File getAccountFile(String accountId) {
        return new File(storageDir, accountId + ".properties");
    }

}
