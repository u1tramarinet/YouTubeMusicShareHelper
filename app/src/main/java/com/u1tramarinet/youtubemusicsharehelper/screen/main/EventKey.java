package com.u1tramarinet.youtubemusicsharehelper.screen.main;

import androidx.annotation.NonNull;

import com.u1tramarinet.youtubemusicsharehelper.R;

public enum EventKey {
    Suffix("SUFFIX", R.string.suffix),
    Artist("ARTIST", R.string.artist),
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
}
