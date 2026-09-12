# POC-4: Serverless Order Fulfillment

## Overview

Java AWS Lambda that consumes order events from Amazon SQS
and stores fulfillment results in Amazon DynamoDB.

## Architecture

Order API
→ Amazon SQS
→ AWS Lambda
→ Amazon DynamoDB

## AWS Resources

- SQS queue: order-processing-queue
- Lambda: order-fulfillment-lambda
- DynamoDB table: order-fulfillment-table
- DynamoDB partition key: orderId, String

## Technology Stack

- Java 21
- Maven
- AWS Lambda Java Core
- AWS Lambda Java Events
- AWS SDK for Java 2.x
- Amazon SQS
- Amazon DynamoDB
- Jackson

## Build

```bash
mvn clean package