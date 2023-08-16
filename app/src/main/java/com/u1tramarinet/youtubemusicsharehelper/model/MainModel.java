package com.u1tramarinet.youtubemusicsharehelper.model;

import android.os.Bundle;

import com.u1tramarinet.youtubemusicsharehelper.model.parser.Other;
import com.u1tramarinet.youtubemusicsharehelper.model.parser.Parser;
import com.u1tramarinet.youtubemusicsharehelper.model.parser.YouTubeMusic;
import com.u1tramarinet.youtubemusicsharehelper.model.parser.result.Music;
import com.u1tramarinet.youtubemusicsharehelper.model.parser.result.Text;
import com.u1tramarinet.youtubemusicsharehelper.util.TextUtil;

public class MainModel {
    public static final String EXTRA_ARTIST = "com.u1tramarinet.youtubemusicsharehelper.artist";
    private static final Parser DEFAULT_PARSER = new Other();
    private static final Parser[] PARSERS = {new YouTubeMusic(), DEFAULT_PARSER};

    public String obtainText(Bundle extras, boolean isRaw) {
        if (!isRaw) {
            for (Parser parser : PARSERS) {
                if (parser.check(extras)) {
                    Text text = parser.parse(extras);
                    String value = text.value;
                    if (text instanceof Music) {
                        Music music = (Music) text;
                        value = TextUtil.combineTextsIfNeed(music.title, music.artist, Music.DEFAULT_DIVIDER);
                    }
                    return TextUtil.combineTextsIfNeed(value, text.url, "\n");
                }
            }
        }
        Text text = DEFAULT_PARSER.parse(extras);
        return TextUtil.combineTextsIfNeed(text.value, text.url, "\n");
    }
}
