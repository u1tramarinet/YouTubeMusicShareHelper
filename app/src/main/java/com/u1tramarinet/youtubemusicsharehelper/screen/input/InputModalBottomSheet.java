package com.u1tramarinet.youtubemusicsharehelper.screen.input;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.u1tramarinet.youtubemusicsharehelper.R;
import com.u1tramarinet.youtubemusicsharehelper.databinding.ModalBottomSheetInputBinding;

public class InputModalBottomSheet extends BottomSheetDialogFragment {
    private static final String KEY_REQUEST_KEY = "requestKey";
    private static final String KEY_CONTENT_TITLE = "contentTitle";
    private static final String KEY_INITIAL_VALUE = "initialValue";
    private static final String KEY_CONFIRMED_VALUE = "confirmedValue";
    private ModalBottomSheetInputBinding binding;
    private InputViewModel viewModel;
    private String requestKey;

    public static InputModalBottomSheet newInstance(@NonNull String requestKey, @NonNull String contentTitle, @NonNull String initialValue,
                                                    @NonNull FragmentManager fragmentManager, @NonNull LifecycleOwner lifecycleOwner, @NonNull InputModalBottomSheetListener listener) {
        Bundle arguments = new Bundle();
        arguments.putString(KEY_REQUEST_KEY, requestKey);
        arguments.putString(KEY_CONTENT_TITLE, contentTitle);
        arguments.putString(KEY_INITIAL_VALUE, initialValue);
        InputModalBottomSheet modalBottomSheet = new InputModalBottomSheet();
        modalBottomSheet.setArguments(arguments);

        fragmentManager.setFragmentResultListener(requestKey, lifecycleOwner, (reqKey, result) -> {
            String confirmedValue = result.getString(KEY_CONFIRMED_VALUE);
            if (confirmedValue != null) {
                listener.onValueConfirmed(reqKey, confirmedValue);
            }
        });

        return modalBottomSheet;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = new ViewModelProvider(this).get(InputViewModel.class);
        viewModel.eventKey().observe(this, this::navigate);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.modal_bottom_sheet_input, container, false);
        binding.setLifecycleOwner(getViewLifecycleOwner());
        binding.setViewModel(viewModel);

        Bundle arguments = getArguments();
        requestKey = getStringFromArguments(arguments, KEY_REQUEST_KEY, null);
        String title = getStringFromArguments(arguments, KEY_CONTENT_TITLE, "");
        String initialValue = getStringFromArguments(arguments, KEY_INITIAL_VALUE, "");

        viewModel.initialize(title, initialValue);
        return binding.getRoot();
    }

    @Override
    public void onResume() {
        super.onResume();
        focusAndShowIme(binding.input);
    }

    @Override
    public void onPause() {
        super.onPause();
        clearFocusAndHideIme(binding.input);
    }

    private void focusAndShowIme(View view) {
        view.requestFocus();
        WindowCompat.getInsetsController(requireActivity().getWindow(), view).show(WindowInsetsCompat.Type.ime());
    }

    private void clearFocusAndHideIme(View view) {
        WindowCompat.getInsetsController(requireActivity().getWindow(), view).hide(WindowInsetsCompat.Type.ime());
        view.clearFocus();
    }

    private void navigate(@NonNull EventKey eventKey) {
        if (eventKey == EventKey.Ok) {
            notifyOnConfirmedValue();
        }
        dismissAllowingStateLoss();
    }

    private void notifyOnConfirmedValue() {
        Bundle result = new Bundle();
        String value = viewModel.input.getValue();
        result.putString(KEY_CONFIRMED_VALUE, (value != null) ? value : "");
        getParentFragmentManager().setFragmentResult(requestKey, result);
    }

    @Nullable
    private String getStringFromArguments(@Nullable Bundle arguments, @NonNull String key, @Nullable String defaultValue) {
        if (arguments == null) {
            return defaultValue;
        }
        return arguments.getString(key, defaultValue);
    }

    public interface InputModalBottomSheetListener {
        void onValueConfirmed(@NonNull String requestKey, @NonNull String value);
    }
}