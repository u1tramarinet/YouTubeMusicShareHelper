package com.u1tramarinet.youtubemusicsharehelper.screen.main;

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

import com.u1tramarinet.youtubemusicsharehelper.parser.result.Music;
import com.u1tramarinet.youtubemusicsharehelper.parser.Other;
import com.u1tramarinet.youtubemusicsharehelper.parser.Parser;
import com.u1tramarinet.youtubemusicsharehelper.parser.YouTubeMusic;
import com.u1tramarinet.youtubemusicsharehelper.parser.result.Text;
import com.u1tramarinet.youtubemusicsharehelper.util.TextUtil;

import java.util.Optional;

public class MainViewModel extends ViewModel {
    @NonNull
    private final MutableLiveData<Bundle> plainTextBundleData = new MutableLiveData<>();

    @NonNull
    private final MutableLiveData<String> musicArtistTextData = new MutableLiveData<>("");

    @NonNull
    private final MutableLiveData<String> textSuffixData = new MutableLiveData<>("");

    @NonNull
    private final MediatorLiveData<String> textData = new MediatorLiveData<>();

    @NonNull
    private final MutableLiveData<Uri> imageUriData = new MutableLiveData<>();

    @NonNull
    private final MutableLiveData<Boolean> isPreviewTextRawData = new MutableLiveData<>();

    @NonNull
    private final MediatorLiveData<Boolean> clearTextButtonEnabledData = new MediatorLiveData<>();

    @NonNull
    private final MediatorLiveData<Boolean> clearImageButtonEnabledData = new MediatorLiveData<>();

    @NonNull
    private final MediatorLiveData<Boolean> shareButtonEnabledData = new MediatorLiveData<>();

    @NonNull
    private final MutableLiveData<Bundle> shareEventData = new MutableLiveData<>();

    public MainViewModel() {
        textData.addSource(plainTextBundleData, (s) -> updatePreviewText());
        textData.addSource(musicArtistTextData, (s) -> updatePreviewText());
        textData.addSource(textSuffixData, (s) -> updatePreviewText());
        textData.addSource(isPreviewTextRawData, (b) -> updatePreviewText());
        clearTextButtonEnabledData.addSource(textData, this::updateClearTextButtonEnabled);
        clearImageButtonEnabledData.addSource(imageUriData, this::updateClearImageButtonEnabled);
        shareButtonEnabledData.addSource(textData, (s) -> updateShareButtonEnabled());
        shareButtonEnabledData.addSource(imageUriData, (uri) -> updateShareButtonEnabled());
    }

    @NonNull
    public LiveData<String> suffixText() {
        return textSuffixData;
    }

    @NonNull
    public LiveData<String> musicArtistText() {
        return musicArtistTextData;
    }

    @NonNull
    public LiveData<String> previewText() {
        return textData;
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
    public LiveData<Boolean> shareButtonEnabled() {
        return shareButtonEnabledData;
    }

    @NonNull
    public LiveData<Bundle> shareEvent() {
        return shareEventData;
    }

    public void updateIsPreviewTextRaw(boolean isRaw) {
        isPreviewTextRawData.postValue(isRaw);
    }

    public void updateSuffix(@NonNull String suffix) {
        textSuffixData.postValue(suffix);
    }

    public void updateArtist(@NonNull String artist) {
        musicArtistTextData.postValue(artist);
    }

    public void clearText() {
        plainTextBundleData.postValue(null);
    }

    public void clearImage() {
        imageUriData.postValue(null);
    }

    public void handlePlainText(Bundle extras) {
        musicArtistTextData.postValue("");
        plainTextBundleData.postValue(extras);
    }

    public void handleImage(Uri uri) {
        imageUriData.postValue(uri);
    }

    public void shareContent() {
        String text = textData.getValue();
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

    private void updatePreviewText() {
        Bundle extra = plainTextBundleData.getValue();
        if (extra == null) {
            textData.postValue("");
            return;
        }
        String artist = musicArtistTextData.getValue();
        extra.putString(YouTubeMusic.EXTRA_ARTIST, artist);
        boolean isRaw = Optional.ofNullable(isPreviewTextRawData.getValue()).orElse(false);
        String musicInfo = obtainText(extra, isRaw);
        String suffix = Optional.ofNullable(textSuffixData.getValue()).orElse("");
        String spacer = "";
        if (!musicInfo.isEmpty() && !suffix.isEmpty()) {
            spacer = "\n";
        }
        textData.postValue(musicInfo + spacer + suffix);
    }

    private String obtainText(Bundle extras, boolean isRaw) {
        if (!isRaw) {
            Parser[] parsers = {new YouTubeMusic(), new Other()};
            for (Parser parser : parsers) {
                if (parser.check(extras)) {
                    Text text = parser.parse(extras);
                    String value = text.value;
                    if (text instanceof Music) {
                        Music music = (Music) text;
                        value = TextUtil.combineTextsIfNeed(music.title, music.artist, Music.DEFAULT_DIVIDER);
                    }
                    return TextUtil.combineTextsIfNeed(value, text.url, "\n");
                }
            }
        }
        Text text = new Other().parse(extras);
        return TextUtil.combineTextsIfNeed(text.value, text.url, "\n");
    }

    private void updateClearTextButtonEnabled(@Nullable String text) {
        clearTextButtonEnabledData.postValue((!TextUtils.isEmpty(text)));
    }

    private void updateClearImageButtonEnabled(@Nullable Uri imageUri) {
        clearImageButtonEnabledData.postValue((imageUri != null));
    }

    private void updateShareButtonEnabled() {
        String text = textData.getValue();
        Uri imageUri = imageUriData.getValue();
        boolean canShare = !TextUtils.isEmpty(text) || (imageUri != null);
        shareButtonEnabledData.postValue(canShare);
    }
}
