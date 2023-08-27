package com.u1tramarinet.youtubemusicsharehelper.model.history;

import androidx.annotation.NonNull;

import java.util.List;

public interface HistoryDataSource {
    void setArtistHistory(@NonNull List<History> histories);

    void addArtistHistory(@NonNull History history);

    void removeArtistHistory(@NonNull History history);

    void clearArtistHistory();

    @NonNull
    List<History> getArtistHistory();

    void setSuffixHistory(@NonNull List<History> histories);

    void addSuffixHistory(@NonNull History history);

    void removeSuffixHistory(@NonNull History history);

    void clearSuffixHistory();

    @NonNull
    List<History> getSuffixHistory();

    void addCallback(@NonNull Callback callback);

    void removeCallback(@NonNull Callback callback);

    interface Callback {
        void onArtistHistoryChanged(@NonNull List<History> histories);

        void onSuffixHistoryChanged(@NonNull List<History> histories);
    }
}
