package com.u1tramarinet.youtubemusicsharehelper.screen.darkmode;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class DarkModeViewModel extends ViewModel {

    @NonNull
    private final MutableLiveData<DarkMode> darkModeData = new MutableLiveData<>();

    @NonNull
    public LiveData<DarkMode> darkMode() {
        return darkModeData;
    }

    public void updateDarkMode(@NonNull DarkMode mode) {
        darkModeData.postValue(mode);
    }
}
