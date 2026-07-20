package com.example.task3;

public class Task3MessageBus {

    public static void main(String[] args) throws InterruptedException {

        MessageBus bus = new MessageBus();

        String[] topics = { "news", "sport", "weather" };

        Thread producer1 = new Thread(new Producer(bus, "producer-1", topics));
        Thread producer2 = new Thread(new Producer(bus, "producer-2", topics));

        Thread consumerNews = new Thread(new Consumer(bus, "consumer-news", "news"));
        Thread consumerSport = new Thread(new Consumer(bus, "consumer-sport", "sport"));
        Thread consumerWeather = new Thread(new Consumer(bus, "consumer-weather", "weather"));

        producer1.start();
        producer2.start();

        consumerNews.start();
        consumerSport.start();
        consumerWeather.start();

        Thread.sleep(5000);

        producer1.interrupt();
        producer2.interrupt();

        consumerNews.interrupt();
        consumerSport.interrupt();
        consumerWeather.interrupt();

        producer1.join();
        producer2.join();

        consumerNews.join();
        consumerSport.join();
        consumerWeather.join();

        System.out.println("Task 3 finished");
    }

}
