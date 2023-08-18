package com.u1tramarinet.youtubemusicsharehelper.screen;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.u1tramarinet.youtubemusicsharehelper.R;

import java.util.ArrayList;
import java.util.List;

public class DarkModeDialogFragment extends DialogFragment {
    static final String KEY_INITIAL_CHOICE_VALUE = "default_choice";

    @Nullable
    private DarkModeDialogListener listener;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (!(requireActivity() instanceof DarkModeDialogListener)) {
            throw new ClassCastException(requireActivity() + " must implement DarkModeDialogListener");
        }
        listener = (DarkModeDialogListener) requireActivity();
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        int initialChoiceValue = 0;
        Bundle arguments = getArguments();
        if (arguments != null) {
            initialChoiceValue = getArguments().getInt(KEY_INITIAL_CHOICE_VALUE, initialChoiceValue);
        }
        DarkMode initialChoice = DarkMode.findByValue(initialChoiceValue);
        List<String> choices = new ArrayList<>();
        for (DarkMode mode : DarkMode.values()) {
            choices.add(getString(mode.titleResId));
        }
        return new MaterialAlertDialogBuilder(requireActivity())
                .setTitle(R.string.dark_mode)
                .setSingleChoiceItems(choices.toArray(new String[]{}),
                        initialChoice.ordinal(), (dialog, which) -> switchDarkMode(DarkMode.values()[which]))
                .setPositiveButton(android.R.string.ok, null)
                .create();
    }

    private void switchDarkMode(@NonNull DarkMode mode) {
        if (listener != null) {
            listener.switchDarkMode(mode);
        }
    }

    public interface DarkModeDialogListener {
        void switchDarkMode(@NonNull DarkMode mode);
    }
}
