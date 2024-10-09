package com.nav;

import com.rabbitmq.client.*;

import java.nio.charset.StandardCharsets;
import java.util.Scanner;

public class Receiver {
    final String name;
    Receiver(String name){
        this.name = name;
    }

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        System.out.println("Enter Receiver : ");
        int length = sc.nextInt();
        for (int i = 0; i < length; i++) {
            new Receiver(i+"th Worker").run();
        }
    }


    private void run(){
        String requestQueue = "requestQueue";
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");
        try {
            final Connection connection = factory.newConnection();
            final Channel channel = connection.createChannel();
            channel.queuePurge(requestQueue);
            channel.basicQos(1);
            DeliverCallback callback = (consumerTag, delivery) -> {
                String message = new String(delivery.getBody(), "UTF-8");
                System.out.println(name+" Receive " + message);
                AMQP.BasicProperties props = new AMQP.BasicProperties
                    .Builder()
                    .correlationId(delivery.getProperties().getCorrelationId())
                    .build();

                String response = "Hello from "+name;
                channel.basicPublish("", delivery.getProperties().getReplyTo(), props, response.getBytes(StandardCharsets.UTF_8));
            };

            channel.basicConsume(requestQueue, true, callback, consumerTag -> {
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}
