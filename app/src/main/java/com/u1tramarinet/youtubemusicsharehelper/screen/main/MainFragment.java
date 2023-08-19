package com.u1tramarinet.youtubemusicsharehelper.screen.main;

import android.content.ContentResolver;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.palette.graphics.Palette;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.u1tramarinet.youtubemusicsharehelper.R;
import com.u1tramarinet.youtubemusicsharehelper.databinding.FragmentMainBinding;
import com.u1tramarinet.youtubemusicsharehelper.screen.MainViewModel;
import com.u1tramarinet.youtubemusicsharehelper.screen.input.InputModalBottomSheet;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.util.function.Consumer;

public class MainFragment extends Fragment {
    private MainViewModel viewModel;
    private WeakReference<Context> contextRef;
    private FragmentMainBinding binding;

    public MainFragment() {
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        contextRef = new WeakReference<>(context);
        viewModel = new ViewModelProvider(requireActivity()).get(MainViewModel.class);
        viewModel.previewImageUri().observe(this, this::updateImage);
        viewModel.shareButtonEnabledState().observe(this, this::updateTextOfShareButton);
        viewModel.eventKey().observe(this, this::navigate);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_main, container, false);
        binding.setLifecycleOwner(getViewLifecycleOwner());
        binding.setViewModel(viewModel);
        return binding.getRoot();
    }

    @Override
    public void onResume() {
        super.onResume();
        updateImage(viewModel.previewImageUri().getValue());
    }

    private void updateTextOfShareButton(@NonNull ShareEnabledState shareEnabledState) {
        binding.share.setText(getTextOfShareButton(shareEnabledState.text, shareEnabledState.image));
    }

    private void updateImage(Uri uri) {
        final Bitmap image = (uri != null) ? createBitmapFromUri(uri) : null;
        binding.previewImage.setImageBitmap(image);
        generateBackgroundColorFromImage(image, imageBackground -> binding.previewImageBackground.setBackgroundColor(imageBackground));
    }

    @Nullable
    private Bitmap createBitmapFromUri(@NonNull Uri imageUri) {
        InputStream stream;
        String scheme = imageUri.getScheme();

        if (scheme != null && scheme.equals(ContentResolver.SCHEME_CONTENT)) {
            try {
                stream = contextRef.get().getContentResolver().openInputStream(imageUri);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
                return null;
            }
        } else {
            try {
                stream = new FileInputStream(imageUri.getPath());
            } catch (FileNotFoundException e) {
                e.printStackTrace();
                return null;
            }
        }

        if (stream == null) {
            return null;
        }

        return BitmapFactory.decodeStream(stream);
    }

    private void generateBackgroundColorFromImage(@Nullable Bitmap image, @NonNull final Consumer<Integer> onComplete) {
        final int defaultImageBackground = android.R.color.transparent;
        if (image == null) {
            runOnMainThread(() -> onComplete.accept(defaultImageBackground));
            return;
        }
        Palette.from(image).generate(palette -> {
            if (palette != null) {
                Palette.Swatch primaryPalette = palette.getMutedSwatch();
                if (primaryPalette != null) {
                    final int imageBackground = primaryPalette.getRgb();
                    runOnMainThread(() -> onComplete.accept(imageBackground));
                    return;
                }
            }
            runOnMainThread(() -> onComplete.accept(defaultImageBackground));
        });
    }

    private void navigate(@NonNull EventKey eventKey) {
        Log.d(MainFragment.class.getSimpleName(), "navigate() key=" + eventKey.getKey());
        String initialValue;
        switch (eventKey) {
            case Suffix:
                initialValue = viewModel.suffixText().getValue();
                break;
            case Artist:
                initialValue = viewModel.artistText().getValue();
                break;
            default:
                return;
        }
        String title = getString(eventKey.titleRes);
        InputModalBottomSheet inputModalBottomSheet = InputModalBottomSheet.newInstance(eventKey.getKey(), title, (initialValue != null) ? initialValue : "",
                getChildFragmentManager(), this, (contentKey, value) -> viewModel.updateParameter(contentKey, value));
        inputModalBottomSheet.show(getChildFragmentManager(), null);
    }

    private String getTextOfShareButton(boolean hasText, boolean hasImage) {
        if (!hasText && !hasImage) {
            return getString(R.string.share);
        }
        String target;
        if (hasText && hasImage) {
            target = getString(R.string.text) + getString(R.string.and) + getString(R.string.image);
        } else {
            target = getString((hasText) ? R.string.text : R.string.image);
        }
        return getString(R.string.share_x, target);
    }

    private void runOnMainThread(@NonNull Runnable r) {
        new Handler(Looper.getMainLooper()).post(r);
    }
}