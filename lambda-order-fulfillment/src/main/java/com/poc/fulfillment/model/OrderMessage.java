package com.poc.fulfillment.model;

public class OrderMessage {

    private Long orderId;
    private String customerName;
    private String productName;

    public OrderMessage() {
    }

    public OrderMessage(
            Long orderId,
            String customerName,
            String productName) {

        this.orderId = orderId;
        this.customerName = customerName;
        this.productName = productName;
    }

    public Long getOrderId() {
        return orderId;
    }

    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(
            String customerName) {

        this.customerName = customerName;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(
            String productName) {

        this.productName = productName;
    }

    @Override
    public String toString() {

        return "OrderMessage{" +
                "orderId=" + orderId +
                ", customerName='" + customerName + '\'' +
                ", productName='" + productName + '\'' +
                '}';
    }
}