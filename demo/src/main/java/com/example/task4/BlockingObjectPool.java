package com.example.task4;

public class BlockingObjectPool {

    private final Object[] pool;
    private int count;

    public BlockingObjectPool(int size) {

        if (size <= 0) {
            throw new IllegalArgumentException("Pool sixe must be > 0");
        }

        pool = new Object[size];

        for (int i = 0; i < size; i++) {
            pool[i] = new Object();

        }
        count = size;
    }

    public synchronized Object get() {
        while (count == 0) {
            try {
                wait();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }

        count--;
        Object object = pool[count];
        pool[count] = null;

        notifyAll();

        return object;
    }

    public synchronized void take(Object object) {
        while (count == pool.length) {
            try {
                wait();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }

        pool[count] = object;
        count++;

        notifyAll();

    }

}
