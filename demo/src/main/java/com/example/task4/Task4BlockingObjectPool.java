package com.example.task4;

public class Task4BlockingObjectPool {
    public static void main(String[] args) throws InterruptedException {
        final BlockingObjectPool pool = new BlockingObjectPool(2);

        Runnable worker = new Runnable() {
            @Override
            public void run() {
                for (int i = 0; i <= 3; i++) {
                    String threadName = Thread.currentThread().getName();

                    System.out.println(threadName + "wants object");

                    Object object = pool.get();

                    System.out.println(threadName + " got object " + System.identityHashCode(object));

                }
            }
        };

    }
}
