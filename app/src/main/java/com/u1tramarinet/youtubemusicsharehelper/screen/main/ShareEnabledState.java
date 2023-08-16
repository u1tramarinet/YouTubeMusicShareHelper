package com.u1tramarinet.youtubemusicsharehelper.screen.main;

public enum ShareEnabledState {
    None(false, false),
    Text(true, false),
    Image(false, true),
    Both(true, true),
    ;
    public final boolean text;
    public final boolean image;

    ShareEnabledState(boolean text, boolean image) {
        this.text = text;
        this.image = image;
    }

    static ShareEnabledState getState(boolean text, boolean image) {
        for (ShareEnabledState state : ShareEnabledState.values()) {
            if (state.text == text && state.image == image) {
                return state;
            }
        }
        return ShareEnabledState.None;
    }
}
