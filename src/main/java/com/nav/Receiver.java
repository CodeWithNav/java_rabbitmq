package com.nav;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.DeliverCallback;

import java.util.Scanner;

public class Receiver {
    final String name;
    final String[] logLevels;
    Receiver(){
        Scanner sc = new Scanner(System.in);
        System.out.print("Enter Receiver Name : ");
        name = sc.nextLine();
        System.out.print("Enter no. of log levels for this receiver : ");
        int length = sc.nextInt();
        logLevels = new String[length];
        for (int i = 0; i < length; i++) {
            System.out.print("Enter log level "+(i+1)+" : ");
            logLevels[i] = sc.next();
        }
    }

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        System.out.print("Enter no Receiver : ");
        int length = sc.nextInt();
        for (int i = 0; i < length; i++) {
            new Receiver().run();
        }
    }


    private void run(){
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");
        try {
            final Connection connection = factory.newConnection();
            final Channel channel = connection.createChannel();
            String queueName = channel.queueDeclare().getQueue();
            for(String level : logLevels){
                channel.queueBind(queueName, "logs", level);
            }
            DeliverCallback callback = (consumerTag, delivery) -> {
                String message = new String(delivery.getBody(), "UTF-8");
                System.out.println(name+" Receive " + message + "  " + consumerTag);
                channel.basicAck(delivery.getEnvelope().getDeliveryTag(),false);
            };
            channel.basicConsume(queueName, false, callback, consumerTag -> {
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}
