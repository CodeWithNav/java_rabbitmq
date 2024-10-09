package com.nav;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.MessageProperties;

import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        ConnectionFactory connectionFactory = new ConnectionFactory();
        connectionFactory.setHost("localhost");
        Scanner sc = new Scanner(System.in);
        try(final Connection connection = connectionFactory.newConnection();
            final Channel channel = connection.createChannel()
            ) {

            channel.exchangeDeclare("logs", "direct");

            while (true) {
                System.out.println(
                    "Press Enter to send a message to the queue. To exit press CTRL+C");
                String message = sc.nextLine();
                System.out.println("Enter Log level from 1..3 :");
                String logLevel = sc.nextLine();
                channel.basicPublish("logs", logLevel, null, message.getBytes());
                System.out.println("------Sent------");
            }
        } catch (Exception e) {
            e.printStackTrace();

        }
    }
}