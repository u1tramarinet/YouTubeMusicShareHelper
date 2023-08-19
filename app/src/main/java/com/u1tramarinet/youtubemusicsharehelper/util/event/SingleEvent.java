package com.u1tramarinet.youtubemusicsharehelper.util.event;

public class SingleEvent<T> {
    private final T value;
    private boolean isAlreadyAcquired = false;

    public SingleEvent(T value) {
        this.value = value;
    }

    public T get() {
        synchronized (this) {
            if (isAlreadyAcquired) {
                throw new IllegalStateException("Value can only be acquired once and is already acquired.");
            }
            isAlreadyAcquired = true;
        }
        return value;
    }

    /** @noinspection unused*/
    public boolean isAlreadyAcquired() {
        synchronized (this) {
            return isAlreadyAcquired;
        }
    }
}
