package com.poc.orderprocessing.messaging;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.poc.orderprocessing.dto.OrderMessage;
import com.poc.orderprocessing.entity.Order;
import com.poc.orderprocessing.enums.OrderStatus;
import com.poc.orderprocessing.repository.OrderRepository;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import software.amazon.awssdk.services.sqs.SqsClient;
import software.amazon.awssdk.services.sqs.model.DeleteMessageRequest;
import software.amazon.awssdk.services.sqs.model.Message;
import software.amazon.awssdk.services.sqs.model.ReceiveMessageRequest;

import java.util.List;

@Component
public class OrderConsumer {

    private final SqsClient sqsClient;
    private final ObjectMapper objectMapper;
    private final OrderRepository orderRepository;

    @Value("${app.aws.sqs.order-queue-url}")
    private String queueUrl;

    public OrderConsumer(
            SqsClient sqsClient,
            ObjectMapper objectMapper,
            OrderRepository orderRepository) {

        this.sqsClient = sqsClient;
        this.objectMapper = objectMapper;
        this.orderRepository = orderRepository;
    }

    @Scheduled(fixedDelay = 5000)
    public void consume() {

        try {

            ReceiveMessageRequest request =
                    ReceiveMessageRequest.builder()
                            .queueUrl(queueUrl)
                            .maxNumberOfMessages(5)
                            .build();

            List<Message> messages =
                    sqsClient.receiveMessage(request)
                             .messages();

            for (Message message : messages) {

                processMessage(message);

                sqsClient.deleteMessage(
                        DeleteMessageRequest.builder()
                                .queueUrl(queueUrl)
                                .receiptHandle(
                                        message.receiptHandle())
                                .build());
            }

        } catch (Exception ex) {

            ex.printStackTrace();
        }
    }

    private void processMessage(
            Message message) {

        Order order = null;

        try {

            OrderMessage orderMessage =
                    objectMapper.readValue(
                            message.body(),
                            OrderMessage.class);

            order =
                    orderRepository
                            .findById(
                                    orderMessage.getOrderId())
                            .orElseThrow();

            order.setStatus(
                    OrderStatus.PROCESSING);

            orderRepository.save(order);

            // Simulated failure
            throw new RuntimeException(
                    "Simulated Failure");

        } catch (Exception ex) {

            ex.printStackTrace();

            if (order != null) {

                order.setStatus(
                        OrderStatus.FAILED);

                orderRepository.save(order);

                System.out.println(
                        "Order Failed : "
                                + order.getId());
            }
        }
    }
}
