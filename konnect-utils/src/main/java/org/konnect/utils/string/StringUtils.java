package org.konnect.utils.string;

import java.util.Arrays;

public class StringUtils {

    public static boolean isNoneBlank(CharSequence... css) {
        return !isAnyBlank(css);
    }

    public static boolean isAnyBlank(CharSequence... css) {
        if (css == null || css.length == 0) {
            return false;
        }
        return Arrays.stream(css).anyMatch(StringUtils::isBlank);
    }

    public static boolean isNotBlank(CharSequence cs) {
        return !isBlank(cs);
    }

    public static boolean isBlank(CharSequence cs) {
        int len = length(cs);
        if (len == 0) {
            return true;
        }
        for(int i = 0; i < len; i++) {
            if (!Character.isWhitespace(cs.charAt(i))) {
                return false;
            }
        }
        return true;
    }

    public static int length(CharSequence cs) {
        return cs == null ? 0 : cs.length();
    }
}
