package com.u1tramarinet.youtubemusicsharehelper.screen;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;

import com.u1tramarinet.youtubemusicsharehelper.databinding.ActivityMainBinding;
import com.u1tramarinet.youtubemusicsharehelper.R;

import java.util.Arrays;

public class MainActivity extends AppCompatActivity implements DarkModeDialogFragment.DarkModeDialogListener {

    private MainViewModel viewModel;
    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.topAppBar.setOnMenuItemClickListener(item -> {
            int clickedItemId = item.getItemId();
            int[] targetItemIds = {R.id.action_light_mode, R.id.action_dark_mode, R.id.action_battery_auto_mode, R.id.action_system_auto_mode};
            if (Arrays.stream(targetItemIds).anyMatch(itemId -> (itemId == clickedItemId))) {
                showNightModeDialog();
                return true;
            }
            return false;
        });

        viewModel = new ViewModelProvider(this).get(MainViewModel.class);
        viewModel.suffixText().observe(this, this::saveSuffix);
        viewModel.shareEvent().observe(this, this::shareContent);
        viewModel.darkMode().observe(this, this::setAppNightMode);
        viewModel.updateDarkMode(getCurrentAppDarkMode());

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

    private void saveSuffix(String newValue) {
        SharedPreferences preferences = getSharedPreferences();
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(getString(R.string.key_suffix), newValue);
        editor.apply();
    }

    private void restoreSuffix() {
        SharedPreferences preferences = getSharedPreferences();
        viewModel.updateSuffix(preferences.getString(getString(R.string.key_suffix), ""));
    }

    private SharedPreferences getSharedPreferences() {
        return getPreferences(MODE_PRIVATE);
    }

    private void setAppNightMode(@NonNull DarkMode mode) {
        if (getCurrentAppDarkMode() != mode) {
            getDelegate().setLocalNightMode(mode.value);
        }
        Menu menu = binding.topAppBar.getMenu();
        if (menu == null) {
            return;
        }
        for (DarkMode m : DarkMode.values()) {
            menu.findItem(m.actionResId).setVisible(m == mode);
        }
    }

    @NonNull
    private DarkMode getCurrentAppDarkMode() {
        return DarkMode.findByValue(getDelegate().getLocalNightMode());
    }

    private void showNightModeDialog() {
        DarkModeDialogFragment dialog = new DarkModeDialogFragment();
        Bundle arguments = new Bundle();
        arguments.putInt(DarkModeDialogFragment.KEY_INITIAL_CHOICE_VALUE, getCurrentAppDarkMode().value);
        dialog.setArguments(arguments);
        dialog.show(getSupportFragmentManager(), "darkMode");
    }

    @Override
    public void switchDarkMode(@NonNull DarkMode mode) {
        viewModel.updateDarkMode(mode);
    }
}