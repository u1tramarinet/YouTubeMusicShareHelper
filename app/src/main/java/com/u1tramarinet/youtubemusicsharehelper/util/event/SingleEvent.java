package com.u1tramarinet.youtubemusicsharehelper.util.event;

public class SingleEvent<T> {
    private final T value;
    private boolean isAlreadyAcquired = false;

    private int availableMaxCount = 1;
    private int acquiredCount = 0;

    public SingleEvent(T value) {
        this.value = value;
    }

    public T get() {
        synchronized (this) {
            if (isAlreadyAcquired) {
                throw new IllegalStateException("Value can only be acquired　" + availableMaxCount + " times.");
            }
            acquiredCount++;
            if (acquiredCount >= availableMaxCount) {
                isAlreadyAcquired = true;
            }
        }
        return value;
    }

    public T get(int maxCount) {
        synchronized (this) {
            availableMaxCount = maxCount;
        }
        return get();
    }

    /**
     * @noinspection unused
     */
    public boolean isAlreadyAcquired() {
        synchronized (this) {
            return isAlreadyAcquired;
        }
    }
}
