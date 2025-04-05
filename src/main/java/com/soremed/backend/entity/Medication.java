package com.soremed.backend.entity;

import jakarta.persistence.*;

@Entity//mappée cette classe a une table
@Table(name = "medication")//nommé medication
public class Medication {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) //
    private Long id;

    private String name;
    private String description;
    // ex: dosage, or manufacturer, etc. (selon besoins du projet)
    private Double price;

    // Constructors
    public Medication() {}
    public Medication(String name, String description, Double price) {
        this.name = name;
        this.description = description;
        this.price = price;
    }

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

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }
}
