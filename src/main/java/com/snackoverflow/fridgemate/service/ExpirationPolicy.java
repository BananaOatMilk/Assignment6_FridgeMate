package com.snackoverflow.fridgemate.service;

import com.snackoverflow.fridgemate.model.FoodItem;

import java.time.LocalDate;

public interface ExpirationPolicy {
    boolean isExpiringSoon(FoodItem item, LocalDate today);
}
