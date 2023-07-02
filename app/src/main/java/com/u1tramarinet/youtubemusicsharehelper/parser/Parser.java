package com.u1tramarinet.youtubemusicsharehelper.parser;

import android.os.Bundle;

import androidx.annotation.NonNull;

import com.u1tramarinet.youtubemusicsharehelper.parser.result.Text;

public interface Parser {

    boolean check(@NonNull Bundle bundle);

    @NonNull
    Text parse(@NonNull Bundle bundle);
}
