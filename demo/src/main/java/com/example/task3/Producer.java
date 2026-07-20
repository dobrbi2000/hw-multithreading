package com.example.task3;

import java.util.Random;

public class Producer implements Runnable {

    private final MessageBus bus;

    private final String producerName;

    private String[] topics;

    private final Random random = new Random();

    public Producer(MessageBus bus, String producerName, String[] topics) {
        this.bus = bus;
        this.producerName = producerName;
        this.topics = topics;
    }

    @Override
    public void run() {
        int counter = 1;

        while (!Thread.currentThread().isInterrupted()) {
            String topic = topics[random.nextInt(topics.length)];
            String payload = producerName + " message #" + counter;

            Message message = new Message(topic, payload);
            bus.post(message);

            System.out.println(producerName + " posted: [" + topic + "] " + payload);

            counter++;

            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }

        }
    }

}
