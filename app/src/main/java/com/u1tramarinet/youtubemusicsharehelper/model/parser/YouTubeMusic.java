package com.u1tramarinet.youtubemusicsharehelper.model.parser;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.u1tramarinet.youtubemusicsharehelper.model.parser.result.Music;

import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class YouTubeMusic implements Parser {
    public static final String EXTRA_ARTIST = "com.u1tramarinet.youtubemusicsharehelper.artist";
    private static final YouTubeMusicRegex[] REGEXES = {
            new YouTubeMusicRegex("(\")(.*?)(\" を YouTube で見る)", 2, -1),
            new YouTubeMusicRegex("(YouTube Music で )(.*?)( をご覧ください)", 2, -1),
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
        String title = subject;
        for (YouTubeMusicRegex regex : REGEXES) {
            Matcher matcher = obtainMatcher(subject, regex);
            if (matcher.matches()) {
                title = obtainTitle(matcher, regex, title);
                artist = obtainArtist(matcher, regex, artist);
                break;
            }
        }
        return new Music(title, artist, text);
    }

    @NonNull
    private String obtainTitle(@NonNull Matcher matcher, YouTubeMusicRegex regex, @Nullable String defaultValue) {
        if (defaultValue == null) {
            defaultValue = "";
        }
        if (matcher.groupCount() == 0) {
            return defaultValue;
        }
        if (regex.titleGroupPosition <= 0 || matcher.groupCount() < regex.titleGroupPosition) {
            return defaultValue;
        }
        String title = matcher.group(regex.titleGroupPosition);
        return (title != null) ? title : defaultValue;
    }

    @NonNull
    private String obtainArtist(@NonNull Matcher matcher, YouTubeMusicRegex regex, @Nullable String defaultValue) {
        if (defaultValue == null) {
            defaultValue = "";
        }
        if (matcher.groupCount() == 0) {
            return defaultValue;
        }
        if (regex.artistGroupPosition <= 0 || matcher.groupCount() < regex.artistGroupPosition) {
            return defaultValue;
        }
        String artist = matcher.group(regex.artistGroupPosition);
        return (artist != null) ? artist : defaultValue;
    }

    private Matcher obtainMatcher(@NonNull String input, YouTubeMusicRegex regex) {
        Pattern pattern = Pattern.compile(regex.text);
        return pattern.matcher(input);
    }

    private static class YouTubeMusicRegex {
        final String text;
        final int titleGroupPosition;
        final int artistGroupPosition;

        YouTubeMusicRegex(String text, int titleGroupPosition, int artistGroupPosition) {
            this.text = text;
            this.titleGroupPosition = titleGroupPosition;
            this.artistGroupPosition = artistGroupPosition;
        }
    }
}
