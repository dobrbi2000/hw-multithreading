package com.example.task1;

import java.util.ConcurrentModificationException;
import java.util.HashMap;
import java.util.Map;

public class Task1Step5CustomThreadSafeMap {

    private static volatile boolean running = true;

    public static void main(String[] args) throws InterruptedException {
        runUnsafe();

        runSafe();

    }

    static class CustomMapWithoutSync {
        private final Map<Integer, Integer> map = new HashMap<Integer, Integer>();

        public void put(Integer key, Integer value) {
            map.put(key, value);
        }

        public long sumValues() {
            long sum = 0;

            for (Integer value : map.values()) {
                sum += value;
            }
            return sum;
        }
    }

    static class CustomMapSync {
        private final Map<Integer, Integer> map = new HashMap<Integer, Integer>();

        public synchronized void put(Integer key, Integer value) {
            map.put(key, value);
        }

        public synchronized long sumValues() {
            long sum = 0;

            for (Integer value : map.values()) {
                sum += value;
            }

            return sum;
        }
    }

    private static void runUnsafe() throws InterruptedException {
        System.out.println("Unsafe method started");

        running = true;

        CustomMapWithoutSync customMap = new CustomMapWithoutSync();

        for (int i = 0; i < 1000; i++) {
            customMap.put(i, i);
        }

        Thread writerThread = new Thread(new Runnable() {
            @Override
            public void run() {
                System.out.println("Unsafe writer thread started");

                int i = 1000;
                while (running) {
                    customMap.put(i, i);
                    i++;
                }
                System.out.println("Unsafe writer stopped");
            }

        });

        Thread readerThread = new Thread(new Runnable() {
            @Override
            public void run() {
                System.out.println("Unsafe reader thread started");

                try {
                    while (running) {
                        long sum = customMap.sumValues();
                        System.out.println("Unsafe sum = " + sum);
                    }

                } catch (ConcurrentModificationException e) {
                    System.out.println("Unsafe reader caught ConcurrentModificationException");
                } finally {
                    running = false;
                }
            }
        });

        writerThread.start();
        readerThread.start();

        readerThread.join();
        running = false;
        writerThread.join();
        System.out.println("Unsafe method finished");
    }

    public static void runSafe() throws InterruptedException {
        System.out.println("Safe method started");

        running = true;

        CustomMapSync customMap = new CustomMapSync();

        for (int i = 0; i < 1000; i++) {
            customMap.put(i, i);
        }

        Thread writerThread = new Thread(new Runnable() {
            @Override
            public void run() {
                System.out.println("Safe writer thread started");

                int i = 1000;
                while (running) {
                    customMap.put(i, i);
                    i++;
                }
                System.out.println("Safe writer stopped");
            }

        });

        Thread readerThread = new Thread(new Runnable() {
            @Override
            public void run() {
                System.out.println("Safe reader thread started");

                while (running) {
                    long sum = customMap.sumValues();
                    System.out.println("Safe sum = " + sum);
                }

                System.out.println("Safe reader stopped");
            }
        });

        writerThread.start();
        readerThread.start();

        Thread.sleep(2000);

        running = false;

        writerThread.join();
        readerThread.join();

        System.out.println("Safe method finished");

    }
}
