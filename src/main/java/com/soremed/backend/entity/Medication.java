package com.soremed.backend.entity;

import jakarta.persistence.*;

@Entity
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
}