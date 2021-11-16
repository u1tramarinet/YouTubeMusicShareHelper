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

import java.sql.Struct;
import java.util.Optional;

public class MainViewModel extends ViewModel {

    @NonNull
    private final MutableLiveData<String> previewMusicInfoData = new MutableLiveData<>("");

    @NonNull
    private final MutableLiveData<String> previewMusicRawData = new MutableLiveData<>("");

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
        previewTextData.addSource(previewMusicInfoData, (s) -> updatePreviewText());
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

    public void handleIntent(@NonNull Intent intent) {
        String action = intent.getAction();
        String type = intent.getType();
        Log.d(MainActivity.class.getSimpleName(), "handleIntent() action=" + action + ", type=" + type);

        if (!Intent.ACTION_SEND.equals(action)) {
            return;
        }
        if (type == null) {
            return;
        }

        if ("text/plain".equals(type)) {
            Bundle extras = intent.getExtras();
            previewMusicInfoData.postValue(parseText(extras, true));
            previewMusicRawData.postValue(parseText(extras, false));
        } else if (type.startsWith("image/")) {
            previewImageUriData.postValue(intent.getParcelableExtra(Intent.EXTRA_STREAM));
        }
    }

    public void shareText() {
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

    @Nullable
    private String parseText(@NonNull Bundle bundle, boolean cutoff) {
        String subject = bundle.getString(Intent.EXTRA_SUBJECT, "");
        String text = bundle.getString(Intent.EXTRA_TEXT, "");

        if (cutoff) {
            subject = cutoffWasteWordsFromSubject(subject);
        }

        if (subject.isEmpty() && text.isEmpty()) return null;
        if (!subject.isEmpty() && !text.isEmpty()) return subject + "\n" + text;
        return subject + text;
    }

    private String cutoffWasteWordsFromSubject(@NonNull String subject) {
        String key = "\"";
        int beginIndex = 1;
        int endIndex = subject.lastIndexOf(key);
        return (beginIndex <= endIndex) ? subject.substring(beginIndex, endIndex) : subject;
    }

    private void updatePreviewText() {
        boolean isRaw = Optional.ofNullable(isPreviewTextRawData.getValue()).orElse(false);
        String musicInfo = Optional.ofNullable(((isRaw) ? previewMusicRawData.getValue() : previewMusicInfoData.getValue())).orElse("");
        String suffix = Optional.ofNullable(previewSuffixData.getValue()).orElse("");
        Log.d(MainViewModel.class.getSimpleName(), "updatePreviewText() musicInfo=" + musicInfo + ", suffix=" + suffix);
        String spacer = "";
        if (!musicInfo.isEmpty() && !suffix.isEmpty()) {
            spacer = "\n";
        }
        previewTextData.postValue(musicInfo + spacer + suffix);
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
}
