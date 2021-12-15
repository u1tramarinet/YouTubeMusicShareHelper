package com.u1tramarinet.youtubemusicsharehelper.parser;

public class Music {
    public String title = "";
    public String artist = "";
    public String url = "";

    @Override
    public String toString() {
        return "Music{" +
                "title='" + title + '\'' +
                ", artist='" + artist + '\'' +
                ", url='" + url + '\'' +
                '}';
    }
}
