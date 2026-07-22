package com.example.task5.util;

import java.math.BigDecimal;
import java.math.RoundingMode;

public final class BigDecimalUtils {

    public static final int SCALE = 2;

    public static final RoundingMode ROUNDING_MODE = RoundingMode.HALF_UP;

    private BigDecimalUtils() {
    }

    public static BigDecimal normalize(BigDecimal value) {
        return value.setScale(SCALE, ROUNDING_MODE);
    }

    public static BigDecimal multiply(BigDecimal amount, BigDecimal rate) {
        return amount.multiply(rate).setScale(SCALE, ROUNDING_MODE);
    }

}
