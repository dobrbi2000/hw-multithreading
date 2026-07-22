package com.example.task5.util;

import java.util.HashMap;
import java.util.Map;

public class AccountLockManager {
    private final Map<String, Object> locks = new HashMap<String, Object>();

    public synchronized Object getLock(String accountId) {
        Object lock = locks.get(accountId);

        if (lock == null) {
            lock = new Object();
            locks.put(accountId, lock);
        }

        return lock;
    }
}
