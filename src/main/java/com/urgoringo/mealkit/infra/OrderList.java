package com.urgoringo.mealkit.infra;

import com.urgoringo.mealkit.subscription.domain.Order;

import java.util.*;

public class OrderList {

    public static <T extends Order> List<T> with(List<T> existing, T orderToAddOrReplace) {
        List<T> result = new ArrayList<>(existing);
        result.removeIf(existingOrder -> existingOrder.id().equals(orderToAddOrReplace.id()));
        result.add(orderToAddOrReplace);
        return result.stream().sorted(Comparator.comparing(Order::deliveryDate)).toList();
    }
}
