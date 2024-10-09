package com.nav;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.DeliverCallback;

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
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");
        try {
            final Connection connection = factory.newConnection();
            final Channel channel = connection.createChannel();
            String queueName = channel.queueDeclare().getQueue();
            channel.queueBind(queueName, "logs", "");
            DeliverCallback callback = (consumerTag, delivery) -> {
                String message = new String(delivery.getBody(), "UTF-8");
                System.out.println(name+" Receive " + message + "'");
                channel.basicAck(delivery.getEnvelope().getDeliveryTag(),false);
            };

            channel.basicConsume(queueName, false, callback, consumerTag -> {
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}
