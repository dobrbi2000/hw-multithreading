package com.example.task1;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

public class Task1PerformanceBenchmark {
    private static volatile boolean running = true;

    private static final long TEST_DURATION_MS = 5000;

    private static final int INITIAL_SIZE = 10000;

    public static void main(String[] args) throws InterruptedException {

        testSyncMap();
        testConcurrentHashMap();
        testCustomThreadSafeMap();

    }

    static class CustomThreadSafeMap {
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

    private static void printResult(String testName, long writeOps, long readOps) {
        long totalOps = writeOps + readOps;
        double seconds = TEST_DURATION_MS / 1000.0;
        double opsPerSec = totalOps / seconds;

        System.out.println("=== " + testName + " ===");
        System.out.println("Write ops: " + writeOps);
        System.out.println("Read ops: " + readOps);
        System.out.println("Total ops: " + totalOps);
        System.out.println("Ops/sec: " + opsPerSec);
        System.out.println();
    }

    private static void testSyncMap() throws InterruptedException {
        running = true;

        Map<Integer, Integer> map = Collections.synchronizedMap(new HashMap<Integer, Integer>());

        for (int i = 0; i < INITIAL_SIZE; i++) {
            map.put(i, i);
        }

        AtomicLong writeOps = new AtomicLong(0);
        AtomicLong readOps = new AtomicLong(0);

        Thread writerThread = new Thread(new Runnable() {
            @Override
            public void run() {
                int i = INITIAL_SIZE;

                while (running) {
                    map.put(i, i);
                    i++;
                    writeOps.incrementAndGet();
                }
            }
        });

        Thread readerThread = new Thread(new Runnable() {
            @Override
            public void run() {
                while (running) {
                    long sum = 0;

                    synchronized (map) {
                        for (Integer value : map.values()) {
                            sum += value;
                        }
                    }

                    readOps.incrementAndGet();
                }
            }
        });

        writerThread.start();
        readerThread.start();

        Thread.sleep(TEST_DURATION_MS);

        running = false;

        writerThread.join();
        readerThread.join();

        printResult("Collections.synchronizedMap", writeOps.get(), readOps.get());
    }

    private static void testConcurrentHashMap() throws InterruptedException {
        running = true;

        Map<Integer, Integer> map = new ConcurrentHashMap<Integer, Integer>();

        for (int i = 0; i < INITIAL_SIZE; i++) {
            map.put(i, i);
        }

        AtomicLong writeOps = new AtomicLong(0);
        AtomicLong readOps = new AtomicLong(0);

        Thread writerThread = new Thread(new Runnable() {
            @Override
            public void run() {
                int i = INITIAL_SIZE;

                while (running) {
                    map.put(i, i);
                    i++;
                    writeOps.incrementAndGet();
                }
            }
        });

        Thread readerThread = new Thread(new Runnable() {
            @Override
            public void run() {
                while (running) {
                    long sum = 0;

                    for (Integer value : map.values()) {
                        sum += value;
                    }

                    readOps.incrementAndGet();
                }
            }
        });

        writerThread.start();
        readerThread.start();

        Thread.sleep(TEST_DURATION_MS);

        running = false;

        writerThread.join();
        readerThread.join();

        printResult("ConcurrentHashMap", writeOps.get(), readOps.get());
    }

    private static void testCustomThreadSafeMap() throws InterruptedException {
        running = true;

        CustomThreadSafeMap map = new CustomThreadSafeMap();

        for (int i = 0; i < INITIAL_SIZE; i++) {
            map.put(i, i);
        }

        AtomicLong writeOps = new AtomicLong(0);
        AtomicLong readOps = new AtomicLong(0);

        Thread writerThread = new Thread(new Runnable() {
            @Override
            public void run() {
                int i = INITIAL_SIZE;

                while (running) {
                    map.put(i, i);
                    i++;
                    writeOps.incrementAndGet();
                }
            }
        });

        Thread readerThread = new Thread(new Runnable() {
            @Override
            public void run() {
                while (running) {
                    long sum = map.sumValues();
                    readOps.incrementAndGet();
                }
            }
        });

        writerThread.start();
        readerThread.start();

        Thread.sleep(TEST_DURATION_MS);

        running = false;

        writerThread.join();
        readerThread.join();

        printResult("CustomThreadSafeMap", writeOps.get(), readOps.get());
    }

}
