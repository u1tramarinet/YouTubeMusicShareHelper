package com.u1tramarinet.youtubemusicsharehelper.parser;

import android.os.Bundle;

import androidx.annotation.NonNull;

public interface Parser {

    boolean check(@NonNull Bundle bundle);

    @NonNull
    Music parse(@NonNull Bundle bundle);
}
