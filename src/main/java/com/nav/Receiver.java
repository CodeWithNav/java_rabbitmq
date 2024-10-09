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
            channel.queueDeclare("myQueue", true, false, false, null);
            channel.basicQos(1);
            DeliverCallback callback = (consumerTag, delivery) -> {
                String message = new String(delivery.getBody(), "UTF-8");
                System.out.println(name+" Receive " + message + "'");
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {

                }finally {
                    channel.basicAck(delivery.getEnvelope().getDeliveryTag(),false);
                }


            };

            channel.basicConsume("myQueue", false, callback, consumerTag -> {
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}
