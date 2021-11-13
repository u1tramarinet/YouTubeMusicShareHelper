package com.u1tramarinet.youtubemusicsharehelper;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;

public class MainActivity extends AppCompatActivity {

    private MainViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        viewModel = new ViewModelProvider(this).get(MainViewModel.class);
        viewModel.shareEvent().observe(this, this::shareText);
        viewModel.handleIntent(getIntent());
        viewModel.updateSuffix(obtainSavedSuffix());
    }

    @Override
    protected void onPause() {
        super.onPause();
        saveSuffix();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        viewModel.handleIntent(intent);
    }

    private void shareText(@NonNull Bundle extras) {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtras(extras);
        startActivity(intent);
        saveSuffix();
    }

    private void saveSuffix() {
        String suffix = viewModel.previewSuffix().getValue();
        String current = obtainSavedSuffix();
        if (!TextUtils.equals(current, suffix)) {
            SharedPreferences preferences = getSharedPreferences();
            SharedPreferences.Editor editor = preferences.edit();
            editor.putString(getString(R.string.key_suffix), suffix);
            editor.apply();
        }
    }

    private String obtainSavedSuffix() {
        SharedPreferences preferences = getSharedPreferences();
        return preferences.getString(getString(R.string.key_suffix), "");
    }

    private SharedPreferences getSharedPreferences() {
        return this.getPreferences(MODE_PRIVATE);
    }
}