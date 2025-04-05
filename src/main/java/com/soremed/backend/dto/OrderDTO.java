package com.soremed.backend.dto;

import java.util.Date;
import java.util.List;

public class OrderDTO {
    private Long id;
    private Date orderDate;
    private String status;
    private Long userId;
    private List<OrderItemDTO> items;

    public OrderDTO() {}

    public OrderDTO(Long id, Date orderDate, String status, Long userId, List<OrderItemDTO> items) {
        this.id = id;
        this.orderDate = orderDate;
        this.status = status;
        this.userId = userId;
        this.items = items;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Date getOrderDate() {
        return orderDate;
    }

    public void setOrderDate(Date orderDate) {
        this.orderDate = orderDate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public List<OrderItemDTO> getItems() {
        return items;
    }

    public void setItems(List<OrderItemDTO> items) {
        this.items = items;
    }
} 