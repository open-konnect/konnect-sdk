package org.konnect.utils;

import org.konnect.utils.string.StringUtils;

import java.util.Arrays;

public class Validations {

    private static final String MESSAGE_PREFIX = "Validation failed: %s";

    public static void validateNotNull(String error, Object o) {
        validate(error, o != null);
    }

    public static void validateNotBlank(String error, String s) {
        validate(error, StringUtils.isNotBlank(s));
    }

    public static void validateIsAnyBlank(String error, CharSequence... css) {
        validate(error, StringUtils.isAnyBlank(css));
    }

    public static void validateIsNoneBlank(String error, CharSequence... css) {
        validate(error, StringUtils.isNoneBlank(css));
    }

    public static void validate(String error, boolean condition) {
        if (!condition) {
            throw new IllegalArgumentException(String.format(MESSAGE_PREFIX, error));
        }
    }
}
