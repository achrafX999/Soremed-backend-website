package com.soremed.backend.dto;

public class OrderItemDTO {
    private Long id;
    private Long medicationId;
    private String medicationName;  // ← nouveau
    private Integer quantity;
    private Double price;

    public OrderItemDTO() {
    }

    public OrderItemDTO(Long id, Long medicationId, String medicationName, Integer quantity, Double price) {
        this.id = id;
        this.medicationId = medicationId;
        this.medicationName = medicationName;
        this.quantity = quantity;
        this.price = price;
    }

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

    public String getMedicationName() {
        return medicationName;
    }

    public void setMedicationName(String medicationName) {
        this.medicationName = medicationName;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    // ← nouveau

   }