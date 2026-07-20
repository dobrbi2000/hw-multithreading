package com.example.task2;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Task2Step2DeadlockFixed {

    private static final List<Integer> nums = new ArrayList<Integer>();

    private static final Object lock = new Object();

    private static volatile boolean running = true;

    public static void main(String[] args) throws InterruptedException {
        Thread writerThread = new Thread(new Writer(), "writer-thread");
        Thread sumThread = new Thread(new SumPrinter(), "sum-thread");
        Thread sqrtThread = new Thread(new SqrtPrinter(), "sqrt-thread");

        writerThread.setDaemon(true);
        sumThread.setDaemon(true);
        sqrtThread.setDaemon(true);

        writerThread.start();
        sumThread.start();
        sqrtThread.start();

        Thread.sleep(5000);

        running = false;

        System.out.println("Main finished");
    }

    private static class Writer implements Runnable {
        @Override
        public void run() {
            Random random = new Random();

            while (running) {
                synchronized (lock) {
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
                long sum = 0;

                synchronized (lock) {
                    for (Integer num : nums) {
                        sum += num;
                    }
                }

                System.out.println("Sum = " + sum);

                sleepQuietly(100);
            }
        }
    }

    private static class SqrtPrinter implements Runnable {
        @Override
        public void run() {
            while (running) {
                double sumOfSquares = 0;

                synchronized (lock) {
                    for (Integer num : nums) {
                        sumOfSquares += (double) num * num;
                    }
                }

                double sqrt = Math.sqrt(sumOfSquares);
                System.out.println("Sqrt(sum of squares) = " + sqrt);

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