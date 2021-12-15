package com.u1tramarinet.youtubemusicsharehelper.parser;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public abstract class YouTubeMusic implements Parser {
    @Override
    public boolean check(@NonNull Bundle bundle) {
        String subject = bundle.getString(Intent.EXTRA_SUBJECT, "");
        return obtainMatcher(subject).matches();
    }

    @NonNull
    @Override
    public Music parse(@NonNull Bundle bundle) {
        String subject = bundle.getString(Intent.EXTRA_SUBJECT, "");
        String text = bundle.getString(Intent.EXTRA_TEXT, "");
        Log.d(YouTubeMusic.class.getSimpleName(), "bundle's subject=" + subject);
        Log.d(YouTubeMusic.class.getSimpleName(), "bundle's text=" + text);
        Matcher matcher = obtainMatcher(subject);
        if (matcher.matches()) {
            Music music = new Music();
            music.title = getTitle(matcher);
            music.artist = "";
            music.url = text;
            return music;
        }
        return new Music();
    }

    @NonNull
    protected abstract String getRegex();

    @NonNull
    protected String getTitle(@NonNull Matcher matcher) {
        int index = getTitleGroupIndex();
        if (matcher.groupCount() < index + 1) {
            return "";
        }
        String title = matcher.group(getTitleGroupIndex());
        return (title != null) ? title : "";
    }

    protected abstract int getTitleGroupIndex();

    @NonNull
    private Matcher obtainMatcher(@NonNull String input) {
        Pattern pattern = Pattern.compile(getRegex());
        return pattern.matcher(input);
    }
}
