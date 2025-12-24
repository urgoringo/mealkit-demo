package com.urgoringo.mealkit.infra;

import java.util.ArrayList;
import java.util.List;

public class Lists {

    public static <T> List<T> of(List<T> existing, T element) {
        List<T> result = new ArrayList<>(existing);
        result.add(element);
        return result;
    }
}
