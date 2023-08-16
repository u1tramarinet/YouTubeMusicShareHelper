package com.u1tramarinet.youtubemusicsharehelper.screen.input;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavBackStackEntry;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.u1tramarinet.youtubemusicsharehelper.R;
import com.u1tramarinet.youtubemusicsharehelper.databinding.FragmentInputBinding;

public class InputFragment extends Fragment {
    private FragmentInputBinding binding;
    private InputViewModel viewModel;
    private String contentKey;

    public InputFragment() {
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
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_input, container, false);
        binding.setLifecycleOwner(getViewLifecycleOwner());
        binding.setViewModel(viewModel);
        Bundle arguments = getArguments();
        contentKey = "";
        String title = "";
        String initialValue = "";
        if (arguments != null) {
            InputFragmentArgs args = InputFragmentArgs.fromBundle(arguments);
            contentKey = args.getContentKey();
            title = args.getContentTitle();
            initialValue = args.getInitialValue();
        }

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

    private void restoreConfirmedInput() {
        NavBackStackEntry navBackStackEntry = findNavController().getPreviousBackStackEntry();
        if (navBackStackEntry != null) {
            navBackStackEntry.getSavedStateHandle().set(contentKey, viewModel.input.getValue());
        }
    }

    private NavController findNavController() {
        return Navigation.findNavController(requireActivity(), R.id.nav_host_fragment);
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
            restoreConfirmedInput();
        }
        findNavController().popBackStack();
    }
}