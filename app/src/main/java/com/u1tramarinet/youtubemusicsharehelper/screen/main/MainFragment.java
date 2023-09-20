package com.u1tramarinet.youtubemusicsharehelper.screen.main;

import android.content.ContentResolver;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.palette.graphics.Palette;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.u1tramarinet.youtubemusicsharehelper.BuildConfig;
import com.u1tramarinet.youtubemusicsharehelper.R;
import com.u1tramarinet.youtubemusicsharehelper.databinding.FragmentMainBinding;
import com.u1tramarinet.youtubemusicsharehelper.model.history.History;
import com.u1tramarinet.youtubemusicsharehelper.screen.MainViewModel;
import com.u1tramarinet.youtubemusicsharehelper.screen.input.InputModalBottomSheet;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
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
        binding.setContext(requireContext());
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        MobileAds.initialize(requireActivity(), initializationStatus -> {
        });
        AdView adView = new AdView(requireContext());
        adView.setAdSize(AdSize.BANNER);
        adView.setAdUnitId(BuildConfig.ADMOB_APP_ID);
        binding.adViewContainer.addView(adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        adView.loadAd(adRequest);
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
        String initialValue;
        List<History> histories;
        switch (eventKey) {
            case Suffix:
                initialValue = Optional.ofNullable(viewModel.suffixText().getValue()).orElse("");
                histories = Optional.ofNullable(viewModel.suffixHistory().getValue()).orElse(new ArrayList<>());
                break;
            case Artist:
                initialValue = Optional.ofNullable(viewModel.artistText().getValue()).orElse("");
                histories = Optional.ofNullable(viewModel.artistHistory().getValue()).orElse(new ArrayList<>());
                break;
            case ImagePicker:
                // NOP(handle in Activity)
                return;
            default:
                return;
        }
        String title = getString(eventKey.titleRes);
        InputModalBottomSheet inputModalBottomSheet = InputModalBottomSheet.newInstance(eventKey.getKey(), title, initialValue, histories,
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