package com.u1tramarinet.youtubemusicsharehelper.util;

import androidx.databinding.BindingAdapter;

import com.google.android.material.textfield.TextInputLayout;

public class BindingAdapters {
    @BindingAdapter("helperTextEnabled")
    public static void setHelperTextEnabled(TextInputLayout view, boolean enabled) {
        view.setHelperTextEnabled(enabled);
    }

    @BindingAdapter("helperText")
    public static void setHelperText(TextInputLayout view, CharSequence text) {
        view.setHelperText(text);
    }
}
