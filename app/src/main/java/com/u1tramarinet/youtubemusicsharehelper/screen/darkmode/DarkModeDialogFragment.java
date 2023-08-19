package com.u1tramarinet.youtubemusicsharehelper.screen.darkmode;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.u1tramarinet.youtubemusicsharehelper.R;

import java.util.ArrayList;
import java.util.List;

public class DarkModeDialogFragment extends DialogFragment {
    private static final String KEY_INITIAL_CHOICE_VALUE = "default_choice";

    private DarkModeViewModel viewModel;

    public static DarkModeDialogFragment newInstance(@NonNull DarkMode mode) {
        DarkModeDialogFragment dialog = new DarkModeDialogFragment();
        Bundle arguments = new Bundle();
        arguments.putInt(DarkModeDialogFragment.KEY_INITIAL_CHOICE_VALUE, mode.value);
        dialog.setArguments(arguments);
        return dialog;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        viewModel = new ViewModelProvider(requireActivity()).get(DarkModeViewModel.class);
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        int initialChoiceValue = DarkMode.FollowSystem.ordinal();
        Bundle arguments = getArguments();
        if (arguments != null) {
            initialChoiceValue = arguments.getInt(KEY_INITIAL_CHOICE_VALUE, initialChoiceValue);
        }
        DarkMode initialChoice = DarkMode.findByValue(initialChoiceValue);
        List<String> choices = new ArrayList<>();
        for (DarkMode mode : DarkMode.values()) {
            choices.add(getString(mode.titleResId));
        }
        return new MaterialAlertDialogBuilder(requireActivity())
                .setTitle(R.string.dark_mode)
                .setSingleChoiceItems(choices.toArray(new String[]{}),
                        initialChoice.ordinal(), (dialog, which) -> viewModel.updateDarkMode(DarkMode.values()[which]))
                .setPositiveButton(android.R.string.ok, null)
                .create();
    }
}
