package com.u1tramarinet.youtubemusicsharehelper.infrastructure.history;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.annotation.NonNull;

import com.u1tramarinet.youtubemusicsharehelper.model.history.History;
import com.u1tramarinet.youtubemusicsharehelper.model.history.HistoryDataSource;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Function;

import javax.inject.Inject;

public class SharedPreferencesHistoryDataSource implements HistoryDataSource {
    @NonNull
    private static final String KEY_ARTIST = "artist_history";
    @NonNull
    private static final String KEY_SUFFIX = "suffix_history";
    @NonNull
    private final SharedPreferencesCollectionDelegate<History> artistHistoryDelegate;
    @NonNull
    private final SharedPreferencesCollectionDelegate<History> suffixHistoryDelegate;
    @NonNull
    private final Set<Callback> callbacks = new HashSet<>();

    @Inject
    public SharedPreferencesHistoryDataSource(@NonNull Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(this.getClass().getName(), Context.MODE_PRIVATE);
        final Function<History, String> convertToString = history -> history.text;
        final Function<String, History> convertFromString = History::new;
        artistHistoryDelegate = new SharedPreferencesCollectionDelegate<>(sharedPreferences, KEY_ARTIST, false, convertToString, convertFromString);
        suffixHistoryDelegate = new SharedPreferencesCollectionDelegate<>(sharedPreferences, KEY_SUFFIX, false, convertToString, convertFromString);
        sharedPreferences.registerOnSharedPreferenceChangeListener((sharedPreferences1, key) -> {
            if (KEY_ARTIST.equals(key)) {
                notifyArtistHistoryChanged();
            } else if (KEY_SUFFIX.equals(key)) {
                notifySuffixHistoryChanged();
            }
        });
    }

    @Override
    public void addArtistHistory(@NonNull History history) {
        artistHistoryDelegate.add(history);
    }

    @Override
    public void removeArtistHistory(@NonNull History history) {
        artistHistoryDelegate.remove(history);
    }

    @Override
    public void clearArtistHistory() {
        artistHistoryDelegate.clear();
    }

    @NonNull
    @Override
    public List<History> getArtistHistory() {
        return artistHistoryDelegate.get();
    }

    @Override
    public void setArtistHistory(@NonNull List<History> histories) {
        artistHistoryDelegate.set(histories);
    }

    @Override
    public void addSuffixHistory(@NonNull History history) {
        suffixHistoryDelegate.add(history);
    }

    @Override
    public void removeSuffixHistory(@NonNull History history) {
        suffixHistoryDelegate.remove(history);
    }

    @Override
    public void clearSuffixHistory() {
        suffixHistoryDelegate.clear();
    }

    @NonNull
    @Override
    public List<History> getSuffixHistory() {
        return suffixHistoryDelegate.get();
    }

    @Override
    public void setSuffixHistory(@NonNull List<History> histories) {
        suffixHistoryDelegate.set(histories);
    }

    @Override
    public void addCallback(@NonNull Callback callback) {
        callbacks.add(callback);
    }

    @Override
    public void removeCallback(@NonNull Callback callback) {
        callbacks.remove(callback);
    }

    private void notifyArtistHistoryChanged() {
        List<History> histories = getArtistHistory();
        for (Callback callback : callbacks) {
            callback.onArtistHistoryChanged(histories);
        }
    }

    private void notifySuffixHistoryChanged() {
        List<History> histories = getSuffixHistory();
        for (Callback callback : callbacks) {
            callback.onSuffixHistoryChanged(histories);
        }
    }
}
