package com.poc.orderprocessing.service;
import com.poc.orderprocessing.dto.OrderMessage;
import com.poc.orderprocessing.messaging.OrderProducer;
import com.poc.orderprocessing.dto.CreateOrderRequest;
import com.poc.orderprocessing.entity.Order;
import com.poc.orderprocessing.enums.OrderStatus;
import com.poc.orderprocessing.repository.OrderRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Service
public class OrderService {

private final OrderRepository orderRepository;
private final OrderProducer orderProducer;

public OrderService(
OrderRepository orderRepository,
OrderProducer orderProducer) {
this.orderRepository = orderRepository;
this.orderProducer = orderProducer;
}
    public Order createOrder(
            CreateOrderRequest request) {

        Order order = new Order();

        order.setCustomerName(
                request.getCustomerName());

        order.setProductName(
                request.getProductName());

        order.setQuantity(
                request.getQuantity());

        order.setUnitPrice(
                request.getUnitPrice());

        order.setTotalAmount(
                request.getUnitPrice()
                        .multiply(
                            BigDecimal.valueOf(
                                request.getQuantity()
                            )));

        order.setStatus(
                OrderStatus.CREATED);

        order.setCreatedAt(
                LocalDateTime.now());

        Order savedOrder =
        orderRepository.save(order);

OrderMessage message =
        new OrderMessage(
                savedOrder.getId(),
                savedOrder.getCustomerName(),
                savedOrder.getProductName());

orderProducer.publish(message);

savedOrder.setStatus(
        OrderStatus.QUEUED);

savedOrder =
        orderRepository.save(savedOrder);

return savedOrder;
    }
}