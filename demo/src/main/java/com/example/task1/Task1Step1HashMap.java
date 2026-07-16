package com.example.task1;

import java.util.ConcurrentModificationException;
import java.util.HashMap;
import java.util.Map;

public class Task1Step1HashMap {

    private static volatile boolean running = true;

    public static void main(String[] args) throws InterruptedException {
        Map<Integer, Integer> map = new HashMap<Integer, Integer>();

        for (int i = 0; i < 1000; i++) {
            map.put(i, i);
        }

        Thread writerThread = new Thread(new Runnable() {
            @Override
            public void run() {
                System.out.println("Writer thread started");

                int i = 1000;
                while (running) {
                    map.put(i, i);
                    i++;
                }

                System.out.println("Writer stopped");
            }
        });

        Thread readerThread = new Thread(new Runnable() {
            @Override
            public void run() {
                System.out.println("Reader thread started");

                try {
                    while (running) {
                        long sum = 0;

                        for (Integer value : map.values()) {
                            sum += value;
                        }

                        System.out.println("Sum = " + sum);
                    }
                } catch (ConcurrentModificationException e) {
                    System.out.println("Reader caught ConcurrentModificationException");
                    e.printStackTrace();
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

        System.out.println("Program finished");
    }
}