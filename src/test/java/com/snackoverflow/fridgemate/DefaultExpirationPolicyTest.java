package com.snackoverflow.fridgemate;

import com.snackoverflow.fridgemate.model.FoodCategory;
import com.snackoverflow.fridgemate.model.FoodItem;
import com.snackoverflow.fridgemate.model.StorageLocation;
import com.snackoverflow.fridgemate.service.DefaultExpirationPolicy;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class DefaultExpirationPolicyTest {
    @Test
    void negativeWarningDaysThrows() {
        assertThrows(IllegalArgumentException.class, () -> new DefaultExpirationPolicy(-1));
    }

    @Test
    void policyCorrectlyFlagsInRangeItems() {
        DefaultExpirationPolicy policy = new DefaultExpirationPolicy(7);
        FoodItem soon = new FoodItem("Milk", "", FoodCategory.DAIRY, StorageLocation.FRIDGE, 1,
                LocalDate.of(2026, 5, 1), LocalDate.of(2026, 5, 6));
        FoodItem past = new FoodItem("Old Bread", "", FoodCategory.GRAINS, StorageLocation.PANTRY, 1,
                LocalDate.of(2026, 5, 1), LocalDate.of(2026, 4, 30));

        assertTrue(policy.isExpiringSoon(soon, LocalDate.of(2026, 5, 1)));
        assertFalse(policy.isExpiringSoon(past, LocalDate.of(2026, 5, 1)));
    }
}
