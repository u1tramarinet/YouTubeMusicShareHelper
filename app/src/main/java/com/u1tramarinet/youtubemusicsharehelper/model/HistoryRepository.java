package com.u1tramarinet.youtubemusicsharehelper.model;

import androidx.annotation.NonNull;

import com.u1tramarinet.youtubemusicsharehelper.model.history.History;
import com.u1tramarinet.youtubemusicsharehelper.model.history.HistoryDataSource;

import java.util.List;

import javax.inject.Inject;

public class HistoryRepository implements HistoryDataSource {
    @NonNull
    private final HistoryDataSource dataSource;

    @Inject
    HistoryRepository(@NonNull HistoryDataSource localDataSource) {
        this.dataSource = localDataSource;
    }

    @Override
    public void setArtistHistory(@NonNull List<History> histories) {
        dataSource.setArtistHistory(histories);
    }

    @Override
    public void addArtistHistory(@NonNull History history) {
        dataSource.addArtistHistory(history);
    }

    @Override
    public void removeArtistHistory(@NonNull History history) {
        dataSource.removeArtistHistory(history);
    }

    @Override
    public void clearArtistHistory() {
        dataSource.clearArtistHistory();
    }

    @NonNull
    @Override
    public List<History> getArtistHistory() {
        return dataSource.getArtistHistory();
    }

    @Override
    public void setSuffixHistory(@NonNull List<History> histories) {
        dataSource.setSuffixHistory(histories);
    }

    @Override
    public void addSuffixHistory(@NonNull History history) {
        dataSource.addSuffixHistory(history);
    }

    @Override
    public void removeSuffixHistory(@NonNull History history) {
        dataSource.removeSuffixHistory(history);
    }

    @Override
    public void clearSuffixHistory() {
        dataSource.clearSuffixHistory();
    }

    @NonNull
    @Override
    public List<History> getSuffixHistory() {
        return dataSource.getSuffixHistory();
    }

    @Override
    public void addCallback(@NonNull Callback callback) {
        dataSource.addCallback(callback);
    }

    @Override
    public void removeCallback(@NonNull Callback callback) {
        dataSource.removeCallback(callback);
    }
}
