package com.u1tramarinet.youtubemusicsharehelper.parser;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;

import com.u1tramarinet.youtubemusicsharehelper.parser.result.Music;

import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class YouTubeMusic implements Parser {
    public static final String EXTRA_ARTIST = "com.u1tramarinet.youtubemusicsharehelper.artist";
    private static final YouTubeMusicRegex[] REGEXES = {
            new YouTubeMusicRegex("(\")(.*?)(\" を YouTube で見る)", 2),
            new YouTubeMusicRegex("(YouTube Music で )(.*?)( をご覧ください)", 2),
    };

    @Override
    public boolean check(@NonNull Bundle bundle) {
        String subject = bundle.getString(Intent.EXTRA_SUBJECT, "");
        return Arrays.stream(REGEXES).anyMatch(regex -> obtainMatcher(subject, regex).matches());
    }

    @NonNull
    @Override
    public Music parse(@NonNull Bundle bundle) {
        String subject = bundle.getString(Intent.EXTRA_SUBJECT, "");
        String artist = bundle.getString(EXTRA_ARTIST, "");
        String text = bundle.getString(Intent.EXTRA_TEXT, "");
        Log.d(YouTubeMusic.class.getSimpleName(), "bundle's subject=" + subject);
        Log.d(YouTubeMusic.class.getSimpleName(), "bundle's text=" + text);
        String title = subject;
        for (YouTubeMusicRegex regex : REGEXES) {
            Matcher matcher = obtainMatcher(subject, regex);
            if (matcher.matches()) {
                title = obtainTitle(matcher, regex);
                break;
            }
        }
        return new Music(title, artist, text);
    }

    @NonNull
    private String obtainTitle(@NonNull Matcher matcher, YouTubeMusicRegex regex) {
        if (matcher.groupCount() < regex.titleGroupIndex + 1) {
            return "";
        }
        String title = matcher.group(regex.titleGroupIndex);
        return (title != null) ? title : "";
    }

    private Matcher obtainMatcher(@NonNull String input, YouTubeMusicRegex regex) {
        Pattern pattern = Pattern.compile(regex.text);
        return pattern.matcher(input);
    }

    private static class YouTubeMusicRegex {
        final String text;
        final int titleGroupIndex;

        YouTubeMusicRegex(String text, int titleGroupIndex) {
            this.text = text;
            this.titleGroupIndex = titleGroupIndex;
        }
    }
}
