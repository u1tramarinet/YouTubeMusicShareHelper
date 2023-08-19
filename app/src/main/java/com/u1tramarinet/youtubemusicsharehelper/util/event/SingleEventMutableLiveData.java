package com.u1tramarinet.youtubemusicsharehelper.util.event;

public class SingleEventMutableLiveData<T> extends SingleEventLiveData<T> {
    public SingleEventMutableLiveData() {
        super();
    }

    /** @noinspection unused*/
    public SingleEventMutableLiveData(T value) {
        super(value);
    }

    @Override
    public void postValue(T value) {
        super.postValue(value);
    }

    @Override
    public void setValue(T value) {
        super.setValue(value);
    }
}
