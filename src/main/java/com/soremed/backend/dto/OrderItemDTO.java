package com.soremed.backend.dto;

public class OrderItemDTO {
    private Long id;
    private Long medicationId;
    private Integer quantity;

    public OrderItemDTO() {}

    public OrderItemDTO(Long id, Long medicationId, Integer quantity) {
        this.id = id;
        this.medicationId = medicationId;
        this.quantity = quantity;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getMedicationId() {
        return medicationId;
    }

    public void setMedicationId(Long medicationId) {
        this.medicationId = medicationId;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }
} 