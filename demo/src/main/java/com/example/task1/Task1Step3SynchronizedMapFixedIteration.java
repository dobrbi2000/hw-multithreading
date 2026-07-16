package com.example.task1;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class Task1Step3SynchronizedMapFixedIteration {

    private static volatile boolean running = true;

    public static void main(String[] args) throws InterruptedException {
        Map<Integer, Integer> map = Collections.synchronizedMap(new HashMap<Integer, Integer>());

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

                while (running) {
                    long sum = 0;

                    synchronized (map) {
                        for (Integer value : map.values()) {
                            sum += value;
                        }
                    }

                    System.out.println("Sum = " + sum);
                }

                System.out.println("Reader stopped");
            }
        });

        writerThread.start();
        readerThread.start();

        Thread.sleep(2000);

        running = false;

        writerThread.join();
        readerThread.join();

        System.out.println("Program finished");
    }
}