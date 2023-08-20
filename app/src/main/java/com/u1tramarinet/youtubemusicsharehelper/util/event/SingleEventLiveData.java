package com.u1tramarinet.youtubemusicsharehelper.util.event;

import android.util.Log;

import androidx.annotation.MainThread;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;

import java.util.HashMap;
import java.util.Map;

/**
 * @noinspection unused
 */
public class SingleEventLiveData<T> {
    private final MutableLiveData<SingleEvent<T>> innerLiveData;
    @NonNull
    private final Map<Observer<? super T>, Observer<SingleEvent<T>>> observerMap = new HashMap<>();

    public SingleEventLiveData() {
        innerLiveData = new MutableLiveData<>();
    }

    public SingleEventLiveData(T value) {
        innerLiveData = new MutableLiveData<>(wrapValue(value));
    }

    /**
     * @see LiveData#observe(LifecycleOwner, Observer)
     */
    @MainThread
    public void observe(@NonNull LifecycleOwner owner, @NonNull Observer<? super T> observer) {
        innerLiveData.observe(owner, wrapObserver(observer));
    }

    /**
     * @see LiveData#observeForever(Observer)
     */
    @MainThread
    public void observeForever(@NonNull Observer<? super T> observer) {
        innerLiveData.observeForever(wrapObserver(observer));
    }

    /**
     * @see LiveData#removeObserver(Observer)
     */
    @MainThread
    public void removeObserver(@NonNull final Observer<? super T> observer) {
        Observer<SingleEvent<T>> wrapObserver = observerMap.get(observer);
        if (wrapObserver != null) {
            innerLiveData.removeObserver(wrapObserver);
        }
    }

    /**
     * @see LiveData#removeObservers(LifecycleOwner)
     */
    @MainThread
    public void removeObservers(@NonNull final LifecycleOwner owner) {
        innerLiveData.removeObservers(owner);
    }

    protected void postValue(T value) {
        innerLiveData.postValue(wrapValue(value));
    }

    protected void setValue(T value) {
        innerLiveData.setValue(wrapValue(value));
    }

    /**
     * @see LiveData#getValue()
     */
    @Nullable
    public SingleEvent<T> getValue() {
        return innerLiveData.getValue();
    }

    /**
     * @see LiveData#isInitialized()
     */
    public boolean isInitialized() {
        return innerLiveData.isInitialized();
    }

    /**
     * @see LiveData#hasObservers()
     */
    public boolean hasObservers() {
        return innerLiveData.hasObservers();
    }

    /**
     * @see LiveData#hasActiveObservers()
     */
    public boolean hasActiveObservers() {
        return innerLiveData.hasActiveObservers();
    }

    @NonNull
    protected Observer<SingleEvent<T>> wrapObserver(@NonNull Observer<? super T> observer) {
        Observer<SingleEvent<T>> wrapObserver = t -> {
            try {
                T value = t.get(observerMap.size());
                observer.onChanged(value);
                Log.d(SingleEventLiveData.class.getSimpleName(), "onChanged() notified");
            } catch (IllegalStateException e) {
                // NOP
                Log.d(SingleEventLiveData.class.getSimpleName(), "onChanged() not notified e=" + e);
            }
        };
        observerMap.put(observer, wrapObserver);
        return wrapObserver;
    }

    @NonNull
    protected SingleEvent<T> wrapValue(@NonNull T value) {
        return new SingleEvent<>(value);
    }
}
