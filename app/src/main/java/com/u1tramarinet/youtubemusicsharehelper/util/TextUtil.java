package com.u1tramarinet.youtubemusicsharehelper.util;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class TextUtil {
    private TextUtil() {
    }

    public static String combineTextsIfNeed(@Nullable String one, @Nullable String another, @NonNull String divider) {
        if (one == null || one.isEmpty()) {
            return another;
        }
        if (another == null || another.isEmpty()) {
            return one;
        }
        return one + divider + another;
    }
}
