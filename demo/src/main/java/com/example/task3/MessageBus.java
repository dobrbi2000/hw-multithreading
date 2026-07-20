package com.example.task3;

import java.util.ArrayList;
import java.util.List;

public class MessageBus {

    private final List<Message> messages = new ArrayList<Message>();

    public void post(Message message) {
        synchronized (messages) {
            messages.add(message);
            messages.notifyAll();
        }
    }

    public Message consume(String topic) throws InterruptedException {
        synchronized (messages) {
            while (true) {
                for (int i = 0; i < messages.size(); i++) {
                    Message message = messages.get(i);

                    if (message.getTopic().equals(topic)) {
                        messages.remove(i);
                        return message;
                    }
                }

                messages.wait();
            }
        }
    }

}
