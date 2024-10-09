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

            channel.queueDeclare("myQueue", true, false, false, null);

            while (true) {
                System.out.println(
                    "Press Enter to send a message to the queue. To exit press CTRL+C");
                int length = sc.nextInt();

                for(int i =0;i<length;i++){
                    channel.basicPublish("", "myQueue", MessageProperties.PERSISTENT_TEXT_PLAIN, ("Hello World " + i).getBytes());
                }

                System.out.println(" [x] Sent '");
            }
        } catch (Exception e) {
            e.printStackTrace();

        }
    }
}