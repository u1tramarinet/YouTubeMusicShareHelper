package com.u1tramarinet.youtubemusicsharehelper;

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
import androidx.palette.graphics.Palette;

import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.u1tramarinet.youtubemusicsharehelper.databinding.FragmentMainBinding;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.lang.ref.WeakReference;

public class MainFragment extends Fragment {

    private MainViewModel viewModel;
    private WeakReference<Context> contextRef;
    private FragmentMainBinding binding;

    public MainFragment() {
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_main, container, false);
        binding.setLifecycleOwner(getViewLifecycleOwner());
        linkBindingAndViewModel();
        return binding.getRoot();
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        contextRef = new WeakReference<>(context);
        viewModel = new ViewModelProvider(requireActivity()).get(MainViewModel.class);
        viewModel.previewImageUri().observe(this, this::updateImage);
        viewModel.suffixInputEnabled().observe(this, enabled -> {
            if (enabled) {
                prepareToInput();
            }
        });
        linkBindingAndViewModel();
    }

    private void linkBindingAndViewModel() {
        if (binding == null || viewModel == null) return;
        binding.setViewModel(viewModel);
    }

    private void updateImage(Uri uri) {
        Bitmap image = null;
        if (uri != null) {
            image = createBitmapFromUri(uri);
        }
        binding.previewImage.setImageBitmap(image);
        updateColorFromImage(image);
    }

    private void prepareToInput() {
        // moveCursorToTai
        binding.suffixInput.setSelection(binding.suffixInput.length());
        // Set focus
        binding.suffixInput.requestFocus();
    }

    @Nullable
    private Bitmap createBitmapFromUri(Uri imageUri) {
        if (imageUri == null) return null;
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

    private void updateColorFromImage(@Nullable Bitmap bitmap) {
        int bodyTextColor = getDefaultColor(R.attr.colorOnSurfaceDark);
        int backgroundColor = getDefaultColor(R.attr.colorSurfaceDark);
        if (bitmap != null) {
            Palette palette = createPaletteSync(bitmap);
            Palette.Swatch swatch = palette.getVibrantSwatch();
            if (swatch != null) {
                bodyTextColor = swatch.getBodyTextColor();
                backgroundColor = swatch.getRgb();
            }
        }
        binding.previewText.setTextColor(bodyTextColor);
        binding.previewText.setBackgroundColor(backgroundColor);
        binding.previewImageBackground.setBackgroundColor(backgroundColor);
    }

    private Palette createPaletteSync(@NonNull Bitmap bitmap) {
        return Palette.from(bitmap).generate();
    }

    private int getDefaultColor(@AttrRes int attrRes) {
        TypedValue outValue = new TypedValue();
        Resources.Theme theme = contextRef.get().getTheme();
        theme.resolveAttribute(attrRes, outValue, true);
        return getResources().getColor(outValue.resourceId, theme);
    }
}