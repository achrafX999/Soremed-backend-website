package com.soremed.backend.entity;

import jakarta.persistence.*;

@Entity //mappée cette classe a une table
@Table(name = "order_item")
public class OrderItem {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "order_id")
    private Order order;          // la commande parent

    @ManyToOne
    @JoinColumn(name = "medication_id")
    private Medication medication; // le médicament commandé

    private Integer quantity;

    public OrderItem() {}
    public OrderItem(Order order, Medication med, Integer quantity) {
        this.order = order;
        this.medication = med;
        this.quantity = quantity;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Order getOrder() {
        return order;
    }

    public void setOrder(Order order) {
        this.order = order;
    }

    public Medication getMedication() {
        return medication;
    }

    public void setMedication(Medication medication) {
        this.medication = medication;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }
}
