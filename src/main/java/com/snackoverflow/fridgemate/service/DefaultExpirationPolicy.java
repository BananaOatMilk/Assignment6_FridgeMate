package com.snackoverflow.fridgemate.service;

import com.snackoverflow.fridgemate.model.FoodItem;

import java.time.LocalDate;

public class DefaultExpirationPolicy implements ExpirationPolicy {
    private final int warningDays;

    public DefaultExpirationPolicy(int warningDays) {
        if (warningDays < 0) {
            throw new IllegalArgumentException("warningDays cannot be negative");
        }
        this.warningDays = warningDays;
    }

    @Override
    public boolean isExpiringSoon(FoodItem item, LocalDate today) {
        long daysLeft = item.daysUntilExpiration(today);
        return daysLeft >= 0 && daysLeft <= warningDays;
    }
}
