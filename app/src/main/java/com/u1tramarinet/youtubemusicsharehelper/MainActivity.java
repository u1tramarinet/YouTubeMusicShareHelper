package com.u1tramarinet.youtubemusicsharehelper;

import androidx.annotation.NonNull;
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
        viewModel.suffixInputEnabled().observe(this, enabled -> {
            if (!enabled) saveSuffix();
        });
        viewModel.handleIntent(getIntent());
        restoreSuffix();
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
    }

    private void saveSuffix() {
        String suffix = viewModel.previewSuffix().getValue();
        SharedPreferences preferences = getSharedPreferences();
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(getString(R.string.key_suffix), suffix);
        editor.apply();
    }

    private void restoreSuffix() {
        SharedPreferences preferences = getSharedPreferences();
        viewModel.updateSuffix(preferences.getString(getString(R.string.key_suffix), ""));
    }

    private SharedPreferences getSharedPreferences() {
        return this.getPreferences(MODE_PRIVATE);
    }
}