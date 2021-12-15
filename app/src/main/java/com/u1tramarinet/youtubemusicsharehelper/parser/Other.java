package com.u1tramarinet.youtubemusicsharehelper.parser;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;

public class Other implements Parser {

    @Override
    public boolean check(@NonNull Bundle bundle) {
        return true;
    }

    @NonNull
    @Override
    public Music parse(@NonNull Bundle bundle) {
        String subject = bundle.getString(Intent.EXTRA_SUBJECT, "");
        String text = bundle.getString(Intent.EXTRA_TEXT, "");
        Music music = new Music();
        music.title = subject;
        music.url = text;
        return music;
    }
}
