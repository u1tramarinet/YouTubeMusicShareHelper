package com.u1tramarinet.youtubemusicsharehelper;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.u1tramarinet.youtubemusicsharehelper.parser.Music;
import com.u1tramarinet.youtubemusicsharehelper.parser.Other;
import com.u1tramarinet.youtubemusicsharehelper.parser.Parser;
import com.u1tramarinet.youtubemusicsharehelper.parser.YouTubeMusic;

import java.util.Optional;

public class MainViewModel extends ViewModel {

    @NonNull
    private final MutableLiveData<Bundle> originalPlainDataData = new MutableLiveData<>();

    @NonNull
    private final MutableLiveData<String> previewMusicInfoData = new MutableLiveData<>("");

    @NonNull
    private final MutableLiveData<String> previewMusicArtistData = new MutableLiveData<>("");

    @NonNull
    private final MutableLiveData<String> previewSuffixData = new MutableLiveData<>("");

    @NonNull
    private final MediatorLiveData<String> previewTextData = new MediatorLiveData<>();

    @NonNull
    private final MutableLiveData<Uri> previewImageUriData = new MutableLiveData<>();

    @NonNull
    private final MutableLiveData<Boolean> suffixInputEnabledData = new MutableLiveData<>(false);

    @NonNull
    private final MutableLiveData<Bundle> shareEventData = new MutableLiveData<>();

    @NonNull
    private final MutableLiveData<Boolean> isPreviewTextRawData = new MutableLiveData<>();

    @NonNull
    private final MediatorLiveData<Boolean> clearTextButtonEnabledData = new MediatorLiveData<>();

    @NonNull
    private final MediatorLiveData<Boolean> clearImageButtonEnabledData = new MediatorLiveData<>();

    @NonNull
    private final MediatorLiveData<Boolean> shareButtonEnabledData = new MediatorLiveData<>();

    public MainViewModel() {
        previewTextData.addSource(originalPlainDataData, (s) -> updatePreviewText());
        previewTextData.addSource(previewMusicArtistData, (s) -> updatePreviewText());
        previewTextData.addSource(previewSuffixData, (s) -> updatePreviewText());
        previewTextData.addSource(isPreviewTextRawData, (b) -> updatePreviewText());
        clearTextButtonEnabledData.addSource(previewMusicInfoData, this::updateClearTextButtonEnabled);
        clearImageButtonEnabledData.addSource(previewImageUriData, this::updateClearImageButtonEnabled);
        shareButtonEnabledData.addSource(previewMusicInfoData, (s) -> updateShareButtonEnabled());
        shareButtonEnabledData.addSource(previewImageUriData, (uri) -> updateShareButtonEnabled());
    }

    @NonNull
    public MutableLiveData<String> previewSuffix() {
        return previewSuffixData;
    }

    @NonNull
    public MutableLiveData<String> previewArtist() {
        return previewMusicArtistData;
    }

    @NonNull
    public LiveData<String> previewText() {
        return previewTextData;
    }

    @NonNull
    public LiveData<Uri> previewImageUri() {
        return previewImageUriData;
    }

    @NonNull
    public MutableLiveData<Boolean> suffixInputEnabled() {
        return suffixInputEnabledData;
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
    public LiveData<Boolean> shareButtonEnabled() {
        return shareButtonEnabledData;
    }

    @NonNull
    public LiveData<Bundle> shareEvent() {
        return shareEventData;
    }

    public void updateSuffixEditing(boolean editing) {
        suffixInputEnabledData.postValue(editing);
    }

    public void updateIsPreviewTextRaw(boolean isRaw) {
        isPreviewTextRawData.postValue(isRaw);
    }

    public void updateSuffix(@NonNull String suffix) {
        previewSuffixData.postValue(suffix);
    }

    public void clearText() {
        previewMusicInfoData.postValue("");
    }

    public void clearImage() {
        previewImageUriData.postValue(null);
    }

    public void handlePlainText(Bundle extras) {
        previewMusicArtistData.postValue("");
        originalPlainDataData.postValue(extras);
    }

    public void handleImage(Uri uri) {
        previewImageUriData.postValue(uri);
    }

    public void shareContent() {
        String text = previewTextData.getValue();
        Uri uri = previewImageUriData.getValue();
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

    @NonNull
    private String combineMusic(@NonNull Music music) {
        return combineText(combineText(music.title, music.artist, " / "), music.url, "\n");
    }

    private void updatePreviewText() {
        Bundle extra = originalPlainDataData.getValue();
        if (extra == null) {
            previewTextData.postValue("");
            return;
        }
        String artist = previewMusicArtistData.getValue();
        extra.putString(YouTubeMusic.EXTRA_ARTIST, artist);
        boolean isRaw = Optional.ofNullable(isPreviewTextRawData.getValue()).orElse(false);
        String musicInfo = obtainMusicInfo(extra, isRaw);
        String suffix = Optional.ofNullable(previewSuffixData.getValue()).orElse("");
        Log.d(MainViewModel.class.getSimpleName(), "updatePreviewText() musicInfo=" + musicInfo + ", suffix=" + suffix);
        String spacer = "";
        if (!musicInfo.isEmpty() && !suffix.isEmpty()) {
            spacer = "\n";
        }
        previewTextData.postValue(musicInfo + spacer + suffix);
    }

    private String obtainMusicInfo(Bundle extras, boolean isRaw) {
        if (!isRaw) {
            Parser[] parsers = {new YouTubeMusic(), new Other()};
            for (Parser parser : parsers) {
                if (parser.check(extras)) {
                    Music music = parser.parse(extras);
                    Log.d(MainViewModel.class.getSimpleName(), music.toString());
                    return combineMusic(music);
                }
            }
        }
        return combineMusic(new Other().parse(extras));
    }

    private void updateClearTextButtonEnabled(@Nullable String musicInfo) {
        clearTextButtonEnabledData.postValue((!TextUtils.isEmpty(musicInfo)));
    }

    private void updateClearImageButtonEnabled(@Nullable Uri imageUri) {
        clearImageButtonEnabledData.postValue((imageUri != null));
    }

    private void updateShareButtonEnabled() {
        String musicInfo = previewMusicInfoData.getValue();
        Uri imageUri = previewImageUriData.getValue();
        boolean canShare = !TextUtils.isEmpty(musicInfo) || (imageUri != null);
        shareButtonEnabledData.postValue(canShare);
    }

    private String combineText(@Nullable String one, @Nullable String another, @NonNull String delimiter) {
        if (!TextUtils.isEmpty(one) && !TextUtils.isEmpty(another))
            return one + delimiter + another;
        return one + another;
    }
}
