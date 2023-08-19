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

import com.u1tramarinet.youtubemusicsharehelper.model.MainModel;
import com.u1tramarinet.youtubemusicsharehelper.screen.main.EventKey;
import com.u1tramarinet.youtubemusicsharehelper.screen.main.ShareEnabledState;
import com.u1tramarinet.youtubemusicsharehelper.util.event.SingleEventLiveData;
import com.u1tramarinet.youtubemusicsharehelper.util.event.SingleEventMutableLiveData;

import java.util.Optional;

public class MainViewModel extends ViewModel {
    @NonNull
    private final MainModel model = new MainModel();
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
    private final MediatorLiveData<ShareEnabledState> shareButtonEnabledStateData = new MediatorLiveData<>();

    @NonNull
    private final MutableLiveData<Bundle> shareEventData = new MutableLiveData<>();

    @NonNull
    private final SingleEventMutableLiveData<EventKey> eventKeyData = new SingleEventMutableLiveData<>();

    public MainViewModel() {
        textData.addSource(plainTextBundleData, (s) -> updatePreviewText());
        textData.addSource(musicArtistTextData, (s) -> updatePreviewText());
        textData.addSource(textSuffixData, (s) -> updatePreviewText());
        textData.addSource(isPreviewTextRawData, (b) -> updatePreviewText());
        clearTextButtonEnabledData.addSource(textData, this::updateClearTextButtonEnabled);
        clearImageButtonEnabledData.addSource(imageUriData, this::updateClearImageButtonEnabled);
        shareButtonEnabledStateData.addSource(textData, (s) -> updateShareButtonEnabled());
        shareButtonEnabledStateData.addSource(imageUriData, (uri) -> updateShareButtonEnabled());
    }

    @NonNull
    public LiveData<String> suffixText() {
        return textSuffixData;
    }

    @NonNull
    public LiveData<String> artistText() {
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
    public LiveData<ShareEnabledState> shareButtonEnabledState() {
        return shareButtonEnabledStateData;
    }

    @NonNull
    public LiveData<Bundle> shareEvent() {
        return shareEventData;
    }

    @NonNull
    public SingleEventLiveData<EventKey> eventKey() {
        return eventKeyData;
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

    public void navigate(@NonNull EventKey eventKey) {
        eventKeyData.postValue(eventKey);
    }

    private void updatePreviewText() {
        Bundle extra = plainTextBundleData.getValue();
        if (extra == null) {
            textData.postValue("");
            return;
        }
        String artist = musicArtistTextData.getValue();
        extra.putString(MainModel.EXTRA_ARTIST, artist);
        boolean isRaw = Optional.ofNullable(isPreviewTextRawData.getValue()).orElse(false);
        String musicInfo = model.obtainText(extra, isRaw);
        String suffix = Optional.ofNullable(textSuffixData.getValue()).orElse("");
        String spacer = "";
        if (!musicInfo.isEmpty() && !suffix.isEmpty()) {
            spacer = "\n";
        }
        textData.postValue(musicInfo + spacer + suffix);
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
        ShareEnabledState enabledState = ShareEnabledState.getState(!TextUtils.isEmpty(text), (imageUri != null));
        shareButtonEnabledStateData.postValue(enabledState);
    }
}
