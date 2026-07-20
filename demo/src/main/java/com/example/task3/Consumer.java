package com.example.task3;

public class Consumer implements Runnable {

    private MessageBus bus;

    private final String consumerName;

    private final String topic;

    public Consumer(MessageBus bus, String consumerName, String topic) {
        this.bus = bus;
        this.consumerName = consumerName;
        this.topic = topic;
    }

    @Override
    public void run() {
        while (!Thread.currentThread().isInterrupted()) {
            Message message;
            try {
                message = bus.consume(topic);
                System.out.println(consumerName + " consumed payload: " + message.getPayload());
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }

        }
    }

}
