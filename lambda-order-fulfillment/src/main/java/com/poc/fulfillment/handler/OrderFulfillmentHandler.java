package com.poc.fulfillment.handler;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.SQSEvent;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.poc.fulfillment.model.OrderMessage;
import com.poc.fulfillment.service.DynamoDbOrderService;

import software.amazon.awssdk.services.dynamodb.DynamoDbClient;

public class OrderFulfillmentHandler
        implements RequestHandler<SQSEvent, Void> {

    private static final String DEFAULT_TABLE_NAME =
            "order-fulfillment-table";

    private final ObjectMapper objectMapper;
    private final DynamoDbOrderService dynamoDbOrderService;

    public OrderFulfillmentHandler() {

        this(
                new ObjectMapper(),
                DynamoDbClient.create(),
                resolveTableName());
    }

    OrderFulfillmentHandler(
            ObjectMapper objectMapper,
            DynamoDbClient dynamoDbClient,
            String tableName) {

        this.objectMapper = objectMapper;

        this.dynamoDbOrderService =
                new DynamoDbOrderService(
                        dynamoDbClient,
                        tableName);
    }

    @Override
    public Void handleRequest(
            SQSEvent sqsEvent,
            Context context) {

        if (sqsEvent == null
                || sqsEvent.getRecords() == null
                || sqsEvent.getRecords().isEmpty()) {

            context.getLogger().log(
                    "No SQS records received.");

            return null;
        }

        context.getLogger().log(
                "Received SQS record count: "
                        + sqsEvent.getRecords().size());

        for (SQSEvent.SQSMessage sqsMessage
                : sqsEvent.getRecords()) {

            processRecord(sqsMessage, context);
        }

        return null;
    }

    private void processRecord(
            SQSEvent.SQSMessage sqsMessage,
            Context context) {

        try {
            context.getLogger().log(
                    "Processing SQS message ID: "
                            + sqsMessage.getMessageId());

            context.getLogger().log(
                    "SQS message body: "
                            + sqsMessage.getBody());

            OrderMessage orderMessage =
                    objectMapper.readValue(
                            sqsMessage.getBody(),
                            OrderMessage.class);

            dynamoDbOrderService.saveFulfilledOrder(
                    orderMessage);

            context.getLogger().log(
                    "Order saved to DynamoDB. orderId="
                            + orderMessage.getOrderId());

        } catch (Exception exception) {

            context.getLogger().log(
                    "Order processing failed. messageId="
                            + sqsMessage.getMessageId()
                            + ", error="
                            + exception.getMessage());

            /*
             * Throwing the exception tells the Lambda/SQS
             * integration that processing did not succeed.
             * The message must not be treated as successful.
             */
            throw new RuntimeException(
                    "Unable to process SQS order message",
                    exception);
        }
    }

    private static String resolveTableName() {

        String configuredTableName =
                System.getenv("TABLE_NAME");

        if (configuredTableName == null
                || configuredTableName.isBlank()) {

            return DEFAULT_TABLE_NAME;
        }

        return configuredTableName;
    }
}