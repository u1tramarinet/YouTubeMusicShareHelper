package com.u1tramarinet.youtubemusicsharehelper.model.history;

import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class History implements Parcelable {
    public final String text;

    public History(@NonNull String text) {
        this.text = text;
    }

    protected History(Parcel in) {
        this.text = in.readString();
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        if (obj instanceof History) {
            History another = (History) obj;
            return TextUtils.equals(this.text, another.text);
        }
        return false;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {
        dest.writeString(this.text);
    }

    public static final Parcelable.Creator<History> CREATOR = new Parcelable.Creator<History>() {

        @Override
        public History createFromParcel(Parcel source) {
            return new History(source);
        }

        @Override
        public History[] newArray(int size) {
            return new History[size];
        }
    };
}
