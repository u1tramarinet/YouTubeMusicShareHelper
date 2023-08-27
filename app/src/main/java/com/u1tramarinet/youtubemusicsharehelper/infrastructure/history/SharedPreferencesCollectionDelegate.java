package com.u1tramarinet.youtubemusicsharehelper.infrastructure.history;

import android.content.SharedPreferences;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

class SharedPreferencesCollectionDelegate<T> {
    @NonNull
    private final SharedPreferences sharedPreferences;
    @NonNull
    private final String key;
    @NonNull
    private final Function<T, String> convertToString;
    @NonNull
    private final Function<String, T> convertFromString;
    private final boolean addToLast;

    SharedPreferencesCollectionDelegate(@NonNull SharedPreferences sharedPreferences, @NonNull String key, boolean addToLast,
                                        @NonNull Function<T, String> convertToString, @NonNull Function<String, T> convertFromString) {
        this.sharedPreferences = sharedPreferences;
        this.key = key;
        this.addToLast = addToLast;
        this.convertToString = convertToString;
        this.convertFromString = convertFromString;
    }

    void set(@NonNull List<T> values) {
        applyArrayList(values);
    }

    void add(@NonNull T value) {
        List<T> values = get();
        if (values.contains(value)) {
            return;
        }
        if (addToLast) {
            values.add(value);
        } else {
            values.add(0, value);
        }
        set(values);
    }

    void remove(@NonNull T value) {
        List<T> values = get();
        if (values.contains(value)) {
            values.remove(value);
            set(values);
        }
    }

    void clear() {
        applyArrayList(null);
    }

    @NonNull
    List<T> get() {
        return getValues();
    }

    private void applyArrayList(@Nullable List<T> values) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        if (values != null && !values.isEmpty()) {
            editor.putString(key, convertValuesToString(values));
        } else {
            editor.remove(key);
        }
        editor.apply();

    }

    @NonNull
    private List<T> getValues() {
        String value = sharedPreferences.getString(key, null);
        if (value == null) {
            return new ArrayList<>();
        }
        return convertStringToValues(value);
    }

    @NonNull
    private String convertValuesToString(@NonNull List<T> values) {
        JSONArray jsonArray = new JSONArray();
        for (T history : values) {
            jsonArray.put(convertToString.apply(history));
        }
        return jsonArray.toString();
    }

    @NonNull
    private List<T> convertStringToValues(@NonNull String value) {
        List<T> values = new ArrayList<>();
        try {
            JSONArray jsonArray = new JSONArray(value);
            for (int i = 0; i < jsonArray.length(); i++) {
                values.add(convertFromString.apply(jsonArray.getString(i)));
            }
        } catch (JSONException e) {
            return new ArrayList<>();
        }
        return values;
    }
}
