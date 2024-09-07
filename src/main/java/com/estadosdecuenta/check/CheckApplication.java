package com.estadosdecuenta.check;

import java.io.IOException;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.DeliverCallback;

@SpringBootApplication
public class CheckApplication {

	private final static String TASK_QUEUE_NAME = "notify";

	public static void main(String[] args) throws Exception {
		SpringApplication.run(CheckApplication.class, args);

		ConnectionFactory factory = new ConnectionFactory();
		factory.setHost("rabbitmq");
		factory.setPort(5672);
		Connection connection = factory.newConnection();
		Channel channel = connection.createChannel();

		channel.queueDeclare(TASK_QUEUE_NAME, false, false, false, null);
		System.out.println(" [*] Waiting for messages. To exit press CTRL+C");

		DeliverCallback deliverCallback = (consumerTag, delivery) -> {
			String message = new String(delivery.getBody(), "UTF-8");

			System.out.println(" [x] Received '" + message + "'");
			try {
				doWork(message);
			} catch (InterruptedException e) {
				e.printStackTrace();
			} finally {
				System.out.println(" [x] Done");
			}
		};
		boolean autoAck = true; // acknowledgment is covered below
		channel.basicConsume(TASK_QUEUE_NAME, autoAck, deliverCallback, consumerTag -> {
		});

	}

	public static void doWork(String task) throws InterruptedException {
		for (char ch : task.toCharArray()) {
			if (ch == '2' || ch == '4' || ch == '6')
				Thread.sleep(2000);
		}
	}
}