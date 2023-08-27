package com.u1tramarinet.youtubemusicsharehelper.screen;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.u1tramarinet.youtubemusicsharehelper.model.HistoryRepository;
import com.u1tramarinet.youtubemusicsharehelper.model.MainRepository;
import com.u1tramarinet.youtubemusicsharehelper.model.history.History;
import com.u1tramarinet.youtubemusicsharehelper.model.history.HistoryDataSource;
import com.u1tramarinet.youtubemusicsharehelper.screen.main.EventKey;
import com.u1tramarinet.youtubemusicsharehelper.screen.main.ShareEnabledState;
import com.u1tramarinet.youtubemusicsharehelper.util.TextUtil;
import com.u1tramarinet.youtubemusicsharehelper.util.event.SingleEventLiveData;
import com.u1tramarinet.youtubemusicsharehelper.util.event.SingleEventMutableLiveData;

import java.util.List;
import java.util.Optional;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;

@HiltViewModel
public class MainViewModel extends ViewModel {
    @Inject
    MainRepository repository;
    private final HistoryRepository historyRepository;
    @NonNull
    private final MutableLiveData<Bundle> plainTextBundleData = new MutableLiveData<>();

    @NonNull
    private final MutableLiveData<String> artistTextData = new MutableLiveData<>("");

    @NonNull
    private final MutableLiveData<String> suffixTextData = new MutableLiveData<>("");

    @NonNull
    private final MediatorLiveData<String> previewTextData = new MediatorLiveData<>();

    @NonNull
    private final MutableLiveData<Uri> imageUriData = new MutableLiveData<>();

    @NonNull
    private final MutableLiveData<Boolean> isPreviewTextRawData = new MutableLiveData<>();

    @NonNull
    private final MediatorLiveData<Boolean> clearTextButtonEnabledData = new MediatorLiveData<>();

    @NonNull
    private final MediatorLiveData<Boolean> clearImageButtonEnabledData = new MediatorLiveData<>();

    @NonNull
    private final MediatorLiveData<ShareEnabledState> shareButtonEnabledStateData = new MediatorLiveData<>();

    @NonNull
    private final SingleEventMutableLiveData<Bundle> shareEventData = new SingleEventMutableLiveData<>();

    @NonNull
    private final SingleEventMutableLiveData<EventKey> eventKeyData = new SingleEventMutableLiveData<>();
    @NonNull
    private final MutableLiveData<List<History>> suffixHistoryData = new MutableLiveData<>();
    @NonNull
    private final MutableLiveData<List<History>> artistHistoryData = new MutableLiveData<>();

    @Inject
    public MainViewModel(HistoryRepository historyRepository) {
        super();
        previewTextData.addSource(plainTextBundleData, (s) -> updatePreviewText());
        previewTextData.addSource(artistTextData, (s) -> updatePreviewText());
        previewTextData.addSource(suffixTextData, (s) -> updatePreviewText());
        previewTextData.addSource(isPreviewTextRawData, (b) -> updatePreviewText());
        clearTextButtonEnabledData.addSource(previewTextData, this::updateClearTextButtonEnabled);
        clearImageButtonEnabledData.addSource(imageUriData, this::updateClearImageButtonEnabled);
        shareButtonEnabledStateData.addSource(previewTextData, (s) -> updateShareButtonEnabled());
        shareButtonEnabledStateData.addSource(imageUriData, (uri) -> updateShareButtonEnabled());

        this.historyRepository = historyRepository;
        historyRepository.addCallback(new HistoryDataSource.Callback() {
            @Override
            public void onArtistHistoryChanged(@NonNull List<History> histories) {
                artistHistoryData.postValue(histories);
            }

            @Override
            public void onSuffixHistoryChanged(@NonNull List<History> histories) {
                suffixHistoryData.postValue(histories);
            }
        });
        artistHistoryData.postValue(historyRepository.getArtistHistory());
        suffixHistoryData.postValue(historyRepository.getSuffixHistory());
    }

    @NonNull
    public LiveData<String> suffixText() {
        return suffixTextData;
    }

    @NonNull
    public LiveData<String> artistText() {
        return artistTextData;
    }

    @NonNull
    public LiveData<String> previewText() {
        return previewTextData;
    }

    @NonNull
    public LiveData<Uri> previewImageUri() {
        return imageUriData;
    }

    @NonNull
    public MutableLiveData<Boolean> isPreviewTextRaw() {
        return isPreviewTextRawData;
    }

    @NonNull
    public LiveData<Boolean> clearTextButtonEnabled() {
        return clearTextButtonEnabledData;
    }

    @NonNull
    public LiveData<Boolean> clearImageButtonEnabled() {
        return clearImageButtonEnabledData;
    }

    @NonNull
    public LiveData<ShareEnabledState> shareButtonEnabledState() {
        return shareButtonEnabledStateData;
    }

    @NonNull
    public SingleEventLiveData<Bundle> shareEvent() {
        return shareEventData;
    }

    @NonNull
    public SingleEventLiveData<EventKey> eventKey() {
        return eventKeyData;
    }

    @NonNull
    public LiveData<List<History>> suffixHistory() {
        return suffixHistoryData;
    }

    @NonNull
    public LiveData<List<History>> artistHistory() {
        return artistHistoryData;
    }

    public void updateIsPreviewTextRaw(boolean isRaw) {
        isPreviewTextRawData.postValue(isRaw);
    }

    public void updateSuffix(@NonNull String suffix) {
        suffixTextData.postValue(suffix);
        if (!suffix.isEmpty()) {
            historyRepository.addSuffixHistory(new History(suffix));
        }
    }

    public void updateArtist(@NonNull String artist) {
        artistTextData.postValue(artist);
        if (!artist.isEmpty()) {
            historyRepository.addArtistHistory(new History(artist));
        }
    }

    public void updateParameter(@NonNull String key, @NonNull String value) {
        EventKey eventKey = EventKey.findByKey(key);
        if (eventKey == null) {
            return;
        }
        switch (eventKey) {
            case Suffix:
                updateSuffix(value);
                break;
            case Artist:
                updateArtist(value);
                break;
        }
    }

    public void clearText() {
        plainTextBundleData.postValue(null);
    }

    public void clearImage() {
        imageUriData.postValue(null);
    }

    public void updatePlainTextFromBundle(Bundle extras) {
        artistTextData.postValue("");
        plainTextBundleData.postValue(extras);
    }

    public void updateImageFromUri(Uri uri) {
        imageUriData.postValue(uri);
    }

    public void shareContent() {
        String text = previewTextData.getValue();
        Uri uri = imageUriData.getValue();
        if (text == null && uri == null) return;

        Bundle bundle = new Bundle();
        if (text != null) {
            bundle.putString(Intent.EXTRA_TEXT, text);
        }
        if (uri != null) {
            bundle.putParcelable(Intent.EXTRA_STREAM, uri);
        }
        shareEventData.postValue(bundle);
    }

    public void navigate(@NonNull EventKey eventKey) {
        eventKeyData.postValue(eventKey);
    }

    private void updatePreviewText() {
        Bundle extra = plainTextBundleData.getValue();
        if (extra == null) {
            previewTextData.postValue("");
            return;
        }
        String artist = artistTextData.getValue();
        artist = (artist != null) ? artist : "";
        extra.putString(MainRepository.EXTRA_ARTIST, artist);
        boolean isRaw = Optional.ofNullable(isPreviewTextRawData.getValue()).orElse(false);
        String musicInfo = repository.parseAndAssembleText(extra, isRaw);
        String suffix = Optional.ofNullable(suffixTextData.getValue()).orElse("");
        previewTextData.postValue(formatMusicInfo(musicInfo, artist, suffix));
    }

    private void updateClearTextButtonEnabled(@Nullable String text) {
        clearTextButtonEnabledData.postValue((!TextUtils.isEmpty(text)));
    }

    private void updateClearImageButtonEnabled(@Nullable Uri imageUri) {
        clearImageButtonEnabledData.postValue((imageUri != null));
    }

    private void updateShareButtonEnabled() {
        String text = previewTextData.getValue();
        Uri imageUri = imageUriData.getValue();
        ShareEnabledState enabledState = ShareEnabledState.getState(!TextUtils.isEmpty(text), (imageUri != null));
        shareButtonEnabledStateData.postValue(enabledState);
    }

    @NonNull
    private String formatMusicInfo(String musicInfo, String artist, String suffix) {
        String artistTag = (artist.isEmpty()) ? "" : TextUtil.createHashTag(artist);
        String newLine = "";
        String spacer = "";
        if (!musicInfo.isEmpty() && !suffix.isEmpty()) {
            newLine = "\n";
        }
        if (!suffix.isEmpty() && !artistTag.isEmpty()) {
            spacer = " ";
        }
        return musicInfo + newLine + suffix + spacer + artistTag;
    }
}
