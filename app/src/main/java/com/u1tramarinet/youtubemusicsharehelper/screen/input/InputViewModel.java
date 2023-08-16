package com.u1tramarinet.youtubemusicsharehelper.screen.input;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class InputViewModel extends ViewModel {
    @NonNull
    public final MutableLiveData<String> input = new MutableLiveData<>();
    @NonNull
    private final MutableLiveData<String> titleData = new MutableLiveData<>("");

    private final MutableLiveData<EventKey> eventKeyData = new MutableLiveData<>();

    public void initialize(String title, String initialValue) {
        titleData.postValue(title);
        input.postValue(initialValue);
    }

    public void navigate(@NonNull EventKey eventKey) {
        eventKeyData.postValue(eventKey);
    }

    @NonNull
    public LiveData<String> title() {
        return titleData;
    }

    @NonNull
    public LiveData<EventKey> eventKey() {
        return eventKeyData;
    }
}
