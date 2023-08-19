package com.u1tramarinet.youtubemusicsharehelper.screen.darkmode;

import androidx.annotation.IdRes;
import androidx.annotation.NonNull;
import androidx.annotation.StringRes;
import androidx.appcompat.app.AppCompatDelegate;

import com.u1tramarinet.youtubemusicsharehelper.R;

public enum DarkMode {
    Off(AppCompatDelegate.MODE_NIGHT_NO, R.string.off, R.id.action_light_mode),
    On(AppCompatDelegate.MODE_NIGHT_YES, R.string.on, R.id.action_dark_mode),
    FollowBatterySaver(AppCompatDelegate.MODE_NIGHT_AUTO_BATTERY, R.string.use_battery_saver_setting, R.id.action_battery_auto_mode),
    FollowSystem(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM, R.string.use_device_setting, R.id.action_system_auto_mode),
    ;
    public final @AppCompatDelegate.NightMode int value;
    public final @StringRes int titleResId;
    public final @IdRes int actionResId;

    DarkMode(@AppCompatDelegate.NightMode int value, @StringRes int titleResId, @IdRes int actionResId) {
        this.value = value;
        this.titleResId = titleResId;
        this.actionResId = actionResId;
    }

    @NonNull
    public static DarkMode findByValue(@AppCompatDelegate.NightMode int value) {
        for (DarkMode mode : DarkMode.values()) {
            if (mode.value == value) {
                return mode;
            }
        }
        return DarkMode.FollowSystem;
    }
}
