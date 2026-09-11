package com.poc.orderprocessing.messaging;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.poc.orderprocessing.dto.OrderMessage;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import software.amazon.awssdk.services.sqs.SqsClient;
import software.amazon.awssdk.services.sqs.model.SendMessageRequest;

@Component
public class OrderProducer {

    private final SqsClient sqsClient;
    // private final ObjectMapper objectMapper;

    @Value("${app.aws.sqs.order-queue-url}")
    private String queueUrl;
private final ObjectMapper objectMapper =
new ObjectMapper();
public OrderProducer(

SqsClient sqsClient) {
this.sqsClient = sqsClient;

}

    public void publish(
            OrderMessage orderMessage) {

        try {

            String payload =
                    objectMapper.writeValueAsString(
                            orderMessage);

            SendMessageRequest request =
                    SendMessageRequest.builder()
                            .queueUrl(queueUrl)
                            .messageBody(payload)
                            .build();

            sqsClient.sendMessage(request);

        } catch (Exception ex) {

            throw new RuntimeException(
                    "Failed to publish message",
                    ex);
        }
    }
}