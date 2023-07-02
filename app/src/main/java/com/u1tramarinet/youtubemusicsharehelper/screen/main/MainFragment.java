package com.u1tramarinet.youtubemusicsharehelper.screen.main;

import android.content.ContentResolver;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.AttrRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavBackStackEntry;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.palette.graphics.Palette;

import android.os.Handler;
import android.os.Looper;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.u1tramarinet.youtubemusicsharehelper.R;
import com.u1tramarinet.youtubemusicsharehelper.databinding.FragmentMainBinding;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.util.function.Consumer;

public class MainFragment extends Fragment {
    private static final String KEY_SUFFIX = "com.u1tramarinet.youtubemusicsharehelper.screen.main.SUFFIX";
    private static final String KEY_ARTIST = "com.u1tramarinet.youtubemusicsharehelper.screen.main.ARTIST";

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
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_main, container, false);
        binding.setLifecycleOwner(getViewLifecycleOwner());
        binding.setViewModel(viewModel);
        binding.suffixInput.setOnClickListener(v -> {
            MainFragmentDirections.ActionMainFragmentToInputFragment action = obtainActionToInputFragment(KEY_SUFFIX, getString(R.string.suffix), viewModel.suffixText().getValue());
            findNavController().navigate(action);
        });
        binding.suffixInputTitle.setOnClickListener(v -> {
            MainFragmentDirections.ActionMainFragmentToInputFragment action = obtainActionToInputFragment(KEY_SUFFIX, getString(R.string.suffix), viewModel.suffixText().getValue());
            findNavController().navigate(action);
        });
        binding.artistInput.setOnClickListener(v -> {
            MainFragmentDirections.ActionMainFragmentToInputFragment action = obtainActionToInputFragment(KEY_ARTIST, getString(R.string.artist), viewModel.musicArtistText().getValue());
            findNavController().navigate(action);
        });
        binding.artistInputTitle.setOnClickListener(v -> {
            MainFragmentDirections.ActionMainFragmentToInputFragment action = obtainActionToInputFragment(KEY_ARTIST, getString(R.string.artist), viewModel.musicArtistText().getValue());
            findNavController().navigate(action);
        });
        NavBackStackEntry navBackStackEntry = findNavController().getCurrentBackStackEntry();
        if (navBackStackEntry != null) {
            navBackStackEntry.getSavedStateHandle().<String>getLiveData(KEY_SUFFIX).observe(getViewLifecycleOwner(), s -> viewModel.updateSuffix(s));
            navBackStackEntry.getSavedStateHandle().<String>getLiveData(KEY_ARTIST).observe(getViewLifecycleOwner(), s -> viewModel.updateArtist(s));
        }
        return binding.getRoot();
    }

    private void updateImage(Uri uri) {
        final Bitmap image = (uri != null) ? createBitmapFromUri(uri) : null;
        createColorPaletteFromImage(image, colorPalette -> updateImageAndColor(image, colorPalette));
    }

    @Nullable
    private Bitmap createBitmapFromUri(@NonNull Uri imageUri) {
        InputStream stream;

        if (imageUri.getScheme().equals(ContentResolver.SCHEME_CONTENT)) {
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

    private void createColorPaletteFromImage(@Nullable Bitmap image, @NonNull final Consumer<ColorPalette> onComplete) {
        ColorPalette colorPalette = new ColorPalette();
        colorPalette.titleTextColor = getDefaultColor(R.attr.colorOnBackground);
        colorPalette.bodyTextColor = getDefaultColor(R.attr.colorOnSurfaceDark);
        colorPalette.bodyBackgroundColor = getDefaultColor(R.attr.colorOnSurfaceDark);
        colorPalette.backgroundColor = getDefaultColor(R.attr.colorPrimaryDark);
        if (image != null) {
            Palette.from(image).generate(palette -> {
                if (palette != null) {
                    Palette.Swatch primaryPalette = palette.getLightVibrantSwatch();
                    Palette.Swatch secondaryPalette = palette.getDarkVibrantSwatch();
                    if (primaryPalette != null) {
                        colorPalette.bodyTextColor = primaryPalette.getBodyTextColor();
                        colorPalette.bodyBackgroundColor = primaryPalette.getRgb();
                    }
                    if (secondaryPalette != null) {
                        colorPalette.titleTextColor = secondaryPalette.getTitleTextColor();
                        colorPalette.backgroundColor = secondaryPalette.getRgb();
                    }
                }
                runOnMainThread(() -> onComplete.accept(colorPalette));
            });
        }
        runOnMainThread(() -> onComplete.accept(colorPalette));
    }

    private void updateImageAndColor(@Nullable Bitmap image, @NonNull ColorPalette colorPalette) {
        binding.background.setBackgroundColor(colorPalette.backgroundColor);
        binding.previewTextTitle.setTextColor(colorPalette.titleTextColor);
        binding.previewText.setTextColor(colorPalette.bodyBackgroundColor);
        binding.previewImageTitle.setTextColor(colorPalette.titleTextColor);
        binding.previewImage.setImageBitmap(image);
        binding.suffixInputTitle.setTextColor(colorPalette.titleTextColor);
        binding.suffixInput.setTextColor(colorPalette.bodyBackgroundColor);
        binding.artistInputTitle.setTextColor(colorPalette.titleTextColor);
        binding.artistInput.setTextColor(colorPalette.bodyBackgroundColor);
        binding.rawTitle.setTextColor(colorPalette.titleTextColor);
    }

    private int getDefaultColor(@AttrRes int attrRes) {
        TypedValue outValue = new TypedValue();
        Resources.Theme theme = contextRef.get().getTheme();
        theme.resolveAttribute(attrRes, outValue, true);
        return getResources().getColor(outValue.resourceId, theme);
    }

    private void runOnMainThread(@NonNull Runnable r) {
        new Handler(Looper.getMainLooper()).post(r);
    }

    private MainFragmentDirections.ActionMainFragmentToInputFragment obtainActionToInputFragment(@NonNull String key, @NonNull String title, @Nullable String initialValue) {
        MainFragmentDirections.ActionMainFragmentToInputFragment action = MainFragmentDirections.actionMainFragmentToInputFragment();
        action.setContentKey(key);
        action.setContentTitle(title);
        if (initialValue != null) {
            action.setInitialValue(initialValue);
        }
        return action;
    }

    private NavController findNavController() {
        return Navigation.findNavController(requireActivity(), R.id.nav_host_fragment);
    }

    private static class ColorPalette {
        int titleTextColor;
        int bodyTextColor;
        int bodyBackgroundColor;
        int backgroundColor;
    }
}