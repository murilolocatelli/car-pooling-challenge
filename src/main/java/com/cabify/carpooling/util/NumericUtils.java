package com.cabify.carpooling.util;

import java.util.Optional;

public abstract class NumericUtils {

    public static Long toLong(final String value) {
        return Optional.ofNullable(value)
            .map(t -> {
                try {
                    return Long.valueOf(t);
                } catch (Exception ex) {
                    return null;
                }
            })
            .orElse(null);
    }

}
