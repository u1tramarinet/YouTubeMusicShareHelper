package com.u1tramarinet.youtubemusicsharehelper.screen.main;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.u1tramarinet.youtubemusicsharehelper.R;

public enum EventKey {
    Suffix("SUFFIX", R.string.suffix),
    Artist("ARTIST", R.string.artist),
    ImagePicker("IMAGE_PICKER", R.string.image_picker),
    ;
    private static final String KEY_SUFFIX = "com.u1tramarinet.youtubemusicsharehelper.screen.main";
    public final int titleRes;
    private final String key;

    EventKey(@NonNull String key, int titleRes) {
        this.key = key;
        this.titleRes = titleRes;
    }

    public String getKey() {
        return KEY_SUFFIX + "." + this.key;
    }

    @Nullable
    public static EventKey findByKey(String key) {
        for (EventKey eventKey : EventKey.values()) {
            if (eventKey.getKey().equals(key)) {
                return eventKey;
            }
        }
        return null;
    }
}
