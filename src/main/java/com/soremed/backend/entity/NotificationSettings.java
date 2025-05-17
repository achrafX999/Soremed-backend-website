// src/main/java/com/soremed/backend/entity/NotificationSettings.java
package com.soremed.backend.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "notification_settings")
public class NotificationSettings {
    @Id
    private Long id = 1L;

    private boolean lowStock;
    private boolean newOrder;
    private boolean orderStatusChange;
    private boolean newUser;
    private boolean systemUpdates;

    private int lowStockThreshold;        // seuil de stock faible
    private int orderDelayThreshold;      // délai d'expiration des commandes en heures

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public boolean isLowStock() {
        return lowStock;
    }

    public void setLowStock(boolean lowStock) {
        this.lowStock = lowStock;
    }

    public boolean isNewOrder() {
        return newOrder;
    }

    public void setNewOrder(boolean newOrder) {
        this.newOrder = newOrder;
    }

    public boolean isOrderStatusChange() {
        return orderStatusChange;
    }

    public void setOrderStatusChange(boolean orderStatusChange) {
        this.orderStatusChange = orderStatusChange;
    }

    public boolean isNewUser() {
        return newUser;
    }

    public void setNewUser(boolean newUser) {
        this.newUser = newUser;
    }

    public boolean isSystemUpdates() {
        return systemUpdates;
    }

    public void setSystemUpdates(boolean systemUpdates) {
        this.systemUpdates = systemUpdates;
    }

    public int getLowStockThreshold() {
        return lowStockThreshold;
    }

    public void setLowStockThreshold(int lowStockThreshold) {
        this.lowStockThreshold = lowStockThreshold;
    }

    public int getOrderDelayThreshold() {
        return orderDelayThreshold;
    }

    public void setOrderDelayThreshold(int orderDelayThreshold) {
        this.orderDelayThreshold = orderDelayThreshold;
    }
}
