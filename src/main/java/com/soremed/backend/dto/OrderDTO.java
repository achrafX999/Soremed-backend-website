package com.soremed.backend.dto;

import java.util.Date;
import java.util.List;

public class OrderDTO {
    private Long id;
    private Date orderDate;
    private String status;
    private Long userId;
    private String username;           // ← ajouté
    private List<OrderItemDTO> items;
    private Double total;

    public OrderDTO() {}

    public OrderDTO(
            Long id,
            Date orderDate,
            String status,
            Long userId,
            String username,              // ← param ajouté
            List<OrderItemDTO> items,
            Double total
    ) {
        this.id         = id;
        this.orderDate  = orderDate;
        this.status     = status;
        this.userId     = userId;
        this.username   = username;   // ← initialisé
        this.items      = items;
        this.total      = total;
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

    public Double getTotal() {
        return total;
    }

    public void setTotal(Double total) {
        this.total = total;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}