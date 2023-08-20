package com.u1tramarinet.youtubemusicsharehelper.util;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class TextUtil {
    private TextUtil() {
    }

    @NonNull
    public static String combineTextsIfNeed(@Nullable String one, @Nullable String another, @NonNull String divider) {
        if (one == null || one.isEmpty()) {
            return (another != null) ? another : "";
        }
        if (another == null || another.isEmpty()) {
            return one;
        }
        return one + divider + another;
    }

    @NonNull
    public static String createHashTag(@NonNull String input) {
        String result = input.replaceAll("\\s", "");
        //noinspection RegExpSimplifiable
        result = result.replaceAll("[\\p{Punct}&&[^&]]", "_");
        return "#" + result.replaceAll("&", " #");
    }
}
