package com.u1tramarinet.youtubemusicsharehelper.parser;

import androidx.annotation.NonNull;

public class Music {
    public String title = "";
    public String artist = "";
    public String url = "";

    Music() {
    }

    Music(String title, String artist, String url) {
        this.title = title;
        this.artist = artist;
        this.url = url;
    }

    @NonNull
    @Override
    public String toString() {
        return "Music{" +
                "title='" + title + '\'' +
                ", artist='" + artist + '\'' +
                ", url='" + url + '\'' +
                '}';
    }
}
