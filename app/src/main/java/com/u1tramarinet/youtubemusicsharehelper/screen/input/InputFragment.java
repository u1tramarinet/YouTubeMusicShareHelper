package com.u1tramarinet.youtubemusicsharehelper.screen.input;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
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
    private InputViewModel viewModel;
    private String contentKey;

    public InputFragment() {
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = new ViewModelProvider(this).get(InputViewModel.class);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        FragmentInputBinding binding = DataBindingUtil.inflate(inflater, R.layout.fragment_input, container, false);
        binding.setLifecycleOwner(getViewLifecycleOwner());
        binding.setViewModel(viewModel);
        binding.okButton.setOnClickListener(v -> {
            restoreConfirmedInput();
            findNavController().popBackStack();
        });
        binding.cancelButton.setOnClickListener(v -> findNavController().popBackStack());
        Bundle arguments = getArguments();
        contentKey = (arguments != null) ? InputFragmentArgs.fromBundle(arguments).getContentKey() : "";
        String title = (arguments != null) ? InputFragmentArgs.fromBundle(arguments).getContentTitle() : "";
        String initialValue = (arguments != null) ? InputFragmentArgs.fromBundle(arguments).getInitialValue() : "";
        viewModel.initialize(title, initialValue);
        return binding.getRoot();
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
}