package com.soremed.backend.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "order_item")
public class OrderItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "medication_id", nullable = false)
    private Medication medication;

    // Nouveau champ pour conserver le nom du médicament au moment de la commande
    @Column(name = "medication_name", nullable = false)
    private String medicationName;

    private Integer quantity;

    public OrderItem() {
    }

    public OrderItem(Order order, Medication med, Integer quantity) {
        this.order = order;
        this.medication = med;
        this.medicationName = med.getName();
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
        this.medicationName = medication.getName();
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
}
