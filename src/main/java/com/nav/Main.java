package com.nav;

import com.rabbitmq.client.*;

import java.nio.charset.StandardCharsets;
import java.util.Scanner;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class Main {
    public static void main(String[] args) {
        ConnectionFactory connectionFactory = new ConnectionFactory();
        connectionFactory.setHost("localhost");

        String requestQueue = "requestQueue";


        Scanner sc = new Scanner(System.in);
        try(final Connection connection = connectionFactory.newConnection();
            final Channel channel = connection.createChannel()
            ) {

            channel.queueDeclare(requestQueue, true, false, false, null);
            String replyTo = channel.queueDeclare().getQueue();
            while (true) {
                System.out.println(
                    "Press Enter to send a message to the queue. To exit press CTRL+C");
                String message = sc.nextLine();
                String correlationId = UUID.randomUUID().toString();
                final AMQP.BasicProperties props = new AMQP.BasicProperties
                    .Builder()
                    .correlationId(correlationId)
                    .replyTo(replyTo)
                    .build();
                channel.basicPublish("", requestQueue, props, message.getBytes(StandardCharsets.UTF_8));
                System.out.println(" [x] Sent '");


                channel.basicConsume(replyTo, true, (consumerTag, delivery) -> {
                    if (delivery.getProperties().getCorrelationId().equals(correlationId)) {
                        System.out.println(" [.] Got '" + new String(delivery.getBody(), StandardCharsets.UTF_8) + "'");
                    }
                }, consumerTag -> {
                });
            }
        } catch (Exception e) {
            e.printStackTrace();

        }
    }
}