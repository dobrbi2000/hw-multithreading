package com.example.task5.dao;

import com.example.task5.exception.AccountNotFoundException;
import com.example.task5.exception.DaoException;
import com.example.task5.model.UserAccount;

public interface AccountDao {

    boolean exists(String accountId) throws DaoException;

    UserAccount load(String accountId) throws DaoException, AccountNotFoundException;

    void save(UserAccount account) throws DaoException;

}
