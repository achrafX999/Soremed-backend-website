// src/main/java/com/soremed/backend/dto/MedicationDTO.java
package com.soremed.backend.dto;

public record MedicationDTO(
        Long   id,
        String name,
        String  description,      // <— ajouté
        String form,
        String dosage,
        String manufacturer,
        Double price,
        Integer quantity
) {}
