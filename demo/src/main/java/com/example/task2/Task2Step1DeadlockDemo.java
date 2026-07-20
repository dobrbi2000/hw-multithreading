package com.example.task2;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Task2Step1DeadlockDemo {

    private static final List<Integer> nums = new ArrayList<Integer>();

    private static final Object collectionLock = new Object();

    private static final Object printLock = new Object();

    private static volatile boolean running = true;

    public static void main(String[] args) throws InterruptedException {

        Thread writerThread = new Thread(new Writer(), "writer-thread");
        Thread sumThread = new Thread(new SumPrinter(), "sum-thread");
        Thread sqrtThread = new Thread(new SqrtPrinter(), "writer-thread");

        writerThread.setDaemon(true);
        sumThread.setDaemon(true);
        sqrtThread.setDaemon(true);

        writerThread.start();
        sumThread.start();
        sqrtThread.start();

        Thread.sleep(10000);

        running = false;

        System.out.println("Main finished. If output stopped before this message, deadlock likely happened.");
    }

    private static class Writer implements Runnable {
        @Override
        public void run() {
            Random random = new Random();

            while (running) {
                synchronized (collectionLock) {
                    nums.add(random.nextInt(100));
                }
                sleepQuietly(50);
            }
        }

    }

    private static class SumPrinter implements Runnable {
        @Override
        public void run() {
            while (running) {
                synchronized (printLock) {
                    sleepQuietly(100);

                    synchronized (collectionLock) {
                        long sum = 0;

                        for (Integer num : nums) {
                            sum += num;
                        }

                        System.out.println("Sum = " + sum);
                    }
                }

                sleepQuietly(100);
            }
        }
    }

    private static class SqrtPrinter implements Runnable {
        @Override
        public void run() {
            while (running) {
                synchronized (collectionLock) {
                    double sumOfSquares = 0;

                    for (Integer num : nums) {
                        sumOfSquares += (double) num * num;
                    }

                    sleepQuietly(10);

                    synchronized (printLock) {
                        System.out.println("Sqrt(sum of squares) = " + Math.sqrt(sumOfSquares));
                    }
                }

                sleepQuietly(100);
            }
        }
    }

    private static void sleepQuietly(long ms) {
        try {
            Thread.sleep(ms);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
