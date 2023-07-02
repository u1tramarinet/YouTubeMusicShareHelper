package com.u1tramarinet.youtubemusicsharehelper.screen.input;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class InputViewModel extends ViewModel {
    @NonNull
    public final MutableLiveData<String> title = new MutableLiveData<>("");
    @NonNull
    public final MutableLiveData<String> input = new MutableLiveData<>();

    public void initialize(String title, String initialValue) {
        this.title.postValue(title);
        input.postValue(initialValue);
    }
}
