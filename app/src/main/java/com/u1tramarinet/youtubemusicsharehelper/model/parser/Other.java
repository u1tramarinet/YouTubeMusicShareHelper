package com.u1tramarinet.youtubemusicsharehelper.model.parser;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;

import com.u1tramarinet.youtubemusicsharehelper.model.parser.result.Music;

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
        return new Music.Builder()
                .title(subject)
                .url(text)
                .build();
    }
}
