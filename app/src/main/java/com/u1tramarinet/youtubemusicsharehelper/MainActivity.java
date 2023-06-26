package com.u1tramarinet.youtubemusicsharehelper;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;

public class MainActivity extends AppCompatActivity {

    private MainViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        viewModel = new ViewModelProvider(this).get(MainViewModel.class);
        viewModel.shareEvent().observe(this, this::shareContent);
        // TODO: 編集完了ボタンを復活させるか、別の契機で保存させることを検討せよ
        viewModel.suffixInputEnabled().observe(this, enabled -> {
            if (!enabled) saveSuffix();
        });
        // TODO: アーティスト入力欄についても同様の検討をせよ
        handleIntent(getIntent());
        restoreSuffix();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        handleIntent(intent);
    }

    private void handleIntent(Intent intent) {
        String action = intent.getAction();
        String type = intent.getType();
        Log.d(MainActivity.class.getSimpleName(), "handleIntent() action=" + action + ", type=" + type);

        if (!Intent.ACTION_SEND.equals(action) || type == null) {
            return;
        }

        if ("text/plain".equals(type)) {
            viewModel.handlePlainText(intent.getExtras());
        } else if (type.startsWith("image/")) {
            viewModel.handleImage(intent.getParcelableExtra(Intent.EXTRA_STREAM));
        }
    }

    private void shareContent(@NonNull Bundle extras) {
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