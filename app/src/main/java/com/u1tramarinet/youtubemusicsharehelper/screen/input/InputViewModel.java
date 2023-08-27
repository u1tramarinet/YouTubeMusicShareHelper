package com.u1tramarinet.youtubemusicsharehelper.screen.input;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.u1tramarinet.youtubemusicsharehelper.model.history.History;
import com.u1tramarinet.youtubemusicsharehelper.util.event.SingleEventLiveData;
import com.u1tramarinet.youtubemusicsharehelper.util.event.SingleEventMutableLiveData;

import java.util.List;

public class InputViewModel extends ViewModel {
    @NonNull
    public final MutableLiveData<String> input = new MutableLiveData<>();
    @NonNull
    private final MutableLiveData<String> titleData = new MutableLiveData<>("");
    @NonNull
    private final MutableLiveData<List<History>> historyData = new MutableLiveData<>();
    @NonNull
    private final SingleEventMutableLiveData<EventKey> eventKeyData = new SingleEventMutableLiveData<>();

    public InputViewModel() {
    }

    public void initialize(String title, String initialValue, List<History> histories) {
        titleData.postValue(title);
        input.postValue(initialValue);
        historyData.postValue(histories);
    }

    public void navigate(@NonNull EventKey eventKey) {
        eventKeyData.postValue(eventKey);
    }

    @NonNull
    public LiveData<String> title() {
        return titleData;
    }

    @NonNull
    public SingleEventLiveData<EventKey> eventKey() {
        return eventKeyData;
    }

    @NonNull
    public LiveData<List<History>> history() {
        return historyData;
    }
}
