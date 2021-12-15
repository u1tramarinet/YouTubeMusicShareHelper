package com.u1tramarinet.youtubemusicsharehelper.parser;

import androidx.annotation.NonNull;

public class YouTubeMusicV2 extends YouTubeMusic {

    @NonNull
    @Override
    protected String getRegex() {
        return "(YouTube Music で )(.*?)( をご覧ください)";
    }

    @Override
    protected int getTitleGroupIndex() {
        return 2;
    }
}
