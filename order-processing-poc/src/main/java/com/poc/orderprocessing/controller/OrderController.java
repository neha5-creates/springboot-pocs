package com.poc.orderprocessing.controller;

import com.poc.orderprocessing.dto.CreateOrderRequest;
import com.poc.orderprocessing.entity.Order;
import com.poc.orderprocessing.service.OrderService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    private final OrderService orderService;

    public OrderController(
            OrderService orderService) {

        this.orderService =
                orderService;
    }

    @PostMapping
    public Order createOrder(
            @Valid
            @RequestBody
            CreateOrderRequest request) {

        return orderService
                .createOrder(request);
    }
}