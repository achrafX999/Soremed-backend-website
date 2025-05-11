package com.soremed.backend.entity;

import jakarta.persistence.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@EntityListeners(AuditingEntityListener.class)
@Table(name = "medication")
public class Medication {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @Column(length = 1000)
    private String description;

    // Nouveau champs
    private String dosage;
    private String form;
    private String manufacturer;

    private Double price;

    // Stock disponible
    @Column(nullable = false)
    private int quantity;

    @CreatedDate
    @Column(updatable = false, nullable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(nullable = false)
    private LocalDateTime updatedAt;

    @Transient
    private Integer _previousQuantity;

    @Transient
    private Double _previousPrice;

    @PostLoad
    public void cachePreviousValues() {
        // À chaque chargement depuis la BDD, on mémorise l’état courant
        this._previousQuantity = this.quantity;
        this._previousPrice    = this.price;
    }

    // Constructors
    public Medication() {}

    public Medication(String name, String description, Double price, int quantity,
                      String dosage, String form, String manufacturer) {
        this.name = name;
        this.description = description;
        this.price = price;
        this.quantity = quantity;
        this.dosage = dosage;
        this.form = form;
        this.manufacturer = manufacturer;
    }

    // Getters & Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDosage() {
        return dosage;
    }

    public void setDosage(String dosage) {
        this.dosage = dosage;
    }

    public String getForm() {
        return form;
    }

    public void setForm(String form) {
        this.form = form;
    }

    public String getManufacturer() {
        return manufacturer;
    }

    public void setManufacturer(String manufacturer) {
        this.manufacturer = manufacturer;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public Integer get_previousQuantity() {
        return _previousQuantity;
    }

    public void set_previousQuantity(Integer _previousQuantity) {
        this._previousQuantity = _previousQuantity;
    }

    public Double get_previousPrice() {
        return _previousPrice;
    }

    public void set_previousPrice(Double _previousPrice) {
        this._previousPrice = _previousPrice;
    }
}