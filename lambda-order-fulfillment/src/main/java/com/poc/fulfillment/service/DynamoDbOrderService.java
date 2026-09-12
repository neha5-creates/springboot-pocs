package com.poc.fulfillment.service;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

import com.poc.fulfillment.model.OrderMessage;

import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import software.amazon.awssdk.services.dynamodb.model.PutItemRequest;

public class DynamoDbOrderService {

    private final DynamoDbClient dynamoDbClient;
    private final String tableName;

    public DynamoDbOrderService(
            DynamoDbClient dynamoDbClient,
            String tableName) {

        this.dynamoDbClient = dynamoDbClient;
        this.tableName = tableName;
    }

    public void saveFulfilledOrder(
            OrderMessage orderMessage) {

        validateOrderMessage(orderMessage);

        Map<String, AttributeValue> item =
                new HashMap<>();

        item.put(
                "orderId",
                AttributeValue.builder()
                        .s(String.valueOf(
                                orderMessage.getOrderId()))
                        .build());

        item.put(
                "customerName",
                AttributeValue.builder()
                        .s(orderMessage.getCustomerName())
                        .build());

        item.put(
                "productName",
                AttributeValue.builder()
                        .s(orderMessage.getProductName())
                        .build());

        item.put(
                "status",
                AttributeValue.builder()
                        .s("FULFILLED")
                        .build());

        item.put(
                "processedAt",
                AttributeValue.builder()
                        .s(Instant.now().toString())
                        .build());

        PutItemRequest request =
                PutItemRequest.builder()
                        .tableName(tableName)
                        .item(item)
                        .build();

        dynamoDbClient.putItem(request);
    }

    private void validateOrderMessage(
            OrderMessage orderMessage) {

        if (orderMessage == null) {
            throw new IllegalArgumentException(
                    "Order message must not be null");
        }

        if (orderMessage.getOrderId() == null) {
            throw new IllegalArgumentException(
                    "Order ID must not be null");
        }

        if (orderMessage.getCustomerName() == null
                || orderMessage.getCustomerName().isBlank()) {

            throw new IllegalArgumentException(
                    "Customer name must not be empty");
        }

        if (orderMessage.getProductName() == null
                || orderMessage.getProductName().isBlank()) {

            throw new IllegalArgumentException(
                    "Product name must not be empty");
        }
    }
}