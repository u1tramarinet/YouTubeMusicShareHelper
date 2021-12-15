package com.u1tramarinet.youtubemusicsharehelper.parser;

import androidx.annotation.NonNull;

public class YouTubeMusicV1 extends YouTubeMusic {

    @Override
    @NonNull
    public String getRegex() {
        return "(\")(.*?)(\" を YouTube で見る)";
    }

    @Override
    protected int getTitleGroupIndex() {
        return 2;
    }
}
