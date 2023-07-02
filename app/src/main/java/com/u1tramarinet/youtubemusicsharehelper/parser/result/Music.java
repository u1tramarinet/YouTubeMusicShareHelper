package com.u1tramarinet.youtubemusicsharehelper.parser.result;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.u1tramarinet.youtubemusicsharehelper.util.TextUtil;

public class Music extends Text {
    public static final String DEFAULT_DIVIDER = " / ";
    public String title = "";
    public String artist = "";

    public Music() {
    }

    public Music(String title, String artist, String url) {
        super(TextUtil.combineTextsIfNeed(title, artist, DEFAULT_DIVIDER), url);
        this.title = title;
        this.artist = artist;
    }

    public void setValue(@Nullable String title, @Nullable String artist, @Nullable String divider) {
        if (divider == null) {
            divider = DEFAULT_DIVIDER;
        }
        value = TextUtil.combineTextsIfNeed(title, artist, divider);
    }

    public void updateValue() {
        setValue(title, artist, null);
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

    public static class Builder {
        private final Music music = new Music();

        public Builder title(String title) {
            music.title = title;
            return this;
        }

        @SuppressWarnings("unused")
        public Builder artist(String artist) {
            music.artist = artist;
            return this;
        }

        public Builder url(String url) {
            music.url = url;
            return this;
        }

        public Music build() {
            music.updateValue();
            return music;
        }
    }
}
