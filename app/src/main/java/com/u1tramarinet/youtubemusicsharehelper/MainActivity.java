package com.u1tramarinet.youtubemusicsharehelper;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentProvider;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

public class MainActivity extends AppCompatActivity {

    private TextView previewText;
    private TextView previewSuffix;
    private ImageView previewImage;
    private Button shareButton;
    private Button clearTextButton;
    private Button clearImageButton;
    private EditText suffixInput;
    private String text;
    private Uri imageUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(MainActivity.class.getSimpleName(), "onCreate()");
        setContentView(R.layout.activity_main);

        String suffix = getSuffix();
        previewText = findViewById(R.id.preview_text);
        previewSuffix = findViewById(R.id.preview_suffix);
        previewSuffix.setText(suffix);
        previewImage = findViewById(R.id.preview_image);
        clearTextButton = findViewById(R.id.clear_text);
        clearTextButton.setOnClickListener((v) -> updateText(null));
        clearImageButton = findViewById(R.id.clear_image);
        clearImageButton.setOnClickListener((v) -> updateImage(null));
        shareButton = findViewById(R.id.share);
        shareButton.setOnClickListener((v) -> share());
        ToggleButton registerToggle = findViewById(R.id.register_toggle);
        registerToggle.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean checked) {
                suffixInput.setEnabled(checked);
                if (!checked) {
                    updateSuffix(suffixInput.getText().toString());
                } else {
                    int length = suffixInput.getText().toString().length();
                    suffixInput.setSelection(length);
                    suffixInput.requestFocus();
                }
            }
        });
        suffixInput = findViewById(R.id.suffix_input);
        suffixInput.setEnabled(registerToggle.isChecked());
        suffixInput.setText(suffix);

        updateText(null);
        updateImage(null);

        handleIntent(getIntent());
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        Log.d(MainActivity.class.getSimpleName(), "onNewIntent()");
        handleIntent(intent);
    }

    private void handleIntent(Intent intent) {
        String action = intent.getAction();
        String type = intent.getType();
        Log.d(MainActivity.class.getSimpleName(), "handleIntent() action=" + action + ", type=" + type);

        if (!Intent.ACTION_SEND.equals(action)) {
            return;
        }
        if (type == null) {
            return;
        }

        if ("text/plain".equals(type)) {
            handleText(intent);
        } else if (type.startsWith("image/")) {
            handleImage(intent);
        }
    }

    private void handleText(Intent intent) {
        String text = parseText(intent.getExtras());
        Log.d(MainActivity.class.getSimpleName(), "handleText() text=" + text);
        updateText(text);
    }

    @Nullable
    private String parseText(Bundle bundle) {
        String subject = bundle.getString(Intent.EXTRA_SUBJECT, "");
        String text = bundle.getString(Intent.EXTRA_TEXT, "");

        String key = "\"";
        int beginIndex = 1;
        int endIndex = subject.lastIndexOf(key);
        Log.d(MainActivity.class.getSimpleName(), "parseText() key=" + key + ", beginIndex=" + beginIndex + ", endIndex=" + endIndex);
        subject = subject.substring(beginIndex, endIndex);

        if (subject.isEmpty() && text.isEmpty()) return null;
        if (!subject.isEmpty() && !text.isEmpty()) return subject + "\n" + text;
        return subject + text;
    }

    private void handleImage(Intent intent) {
        Uri imageUri = intent.getParcelableExtra(Intent.EXTRA_STREAM);
        Log.d(MainActivity.class.getSimpleName(), "handleImage() uri=" + imageUri.getPath());
        updateImage(imageUri);
    }

    @Nullable
    private Bitmap createBitmapFromUri(Uri imageUri) {
        if (imageUri == null) return null;
        InputStream stream;

        if (imageUri.getScheme().equals(ContentResolver.SCHEME_CONTENT)) {
            try {
                stream = getContentResolver().openInputStream(imageUri);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
                Toast.makeText(this, "Failed to get image...", Toast.LENGTH_SHORT).show();
                return null;
            }
        } else {
            try {
                stream = new FileInputStream(imageUri.getPath());
            } catch (FileNotFoundException e) {
                e.printStackTrace();
                Toast.makeText(this, "Failed to get image...", Toast.LENGTH_SHORT).show();
                return null;
            }
        }

        if (stream == null) {
            Toast.makeText(this, "Failed to get image...", Toast.LENGTH_SHORT).show();
            return null;
        }

        return BitmapFactory.decodeStream(stream);
    }

    private void share() {
        if (text == null && imageUri == null) {
            Toast.makeText(this, "Nothing to share.", Toast.LENGTH_SHORT).show();
            return;
        }

        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_SEND);

        if (text != null) {
            String suffix = getSuffix();
            if (!suffix.isEmpty()) {
                suffix = "\n" + suffix;
            }
            intent.putExtra(Intent.EXTRA_TEXT, text + suffix);
        }
        if (imageUri != null) {
            intent.putExtra(Intent.EXTRA_STREAM, imageUri);
        }
        intent.setType("text/plain");
        startActivity(intent);
    }

    private void updateText(String text) {
        this.text = text;
        previewText.setText(text);
        clearTextButton.setEnabled((text != null));
        shareButton.setEnabled(clearTextButton.isEnabled() || clearImageButton.isEnabled());
    }

    private void updateImage(Uri imageUri) {
        this.imageUri = imageUri;
        previewImage.setImageBitmap(createBitmapFromUri(imageUri));
        clearImageButton.setEnabled((imageUri != null));
        shareButton.setEnabled(clearTextButton.isEnabled() || clearImageButton.isEnabled());
    }

    private void updateSuffix(String suffix) {
        SharedPreferences preferences = getSharedPreferences();
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(getString(R.string.key_suffix), suffix);
        editor.apply();
        previewSuffix.setText(suffix);
    }

    private String getSuffix() {
        SharedPreferences preferences = getSharedPreferences();
        return preferences.getString(getString(R.string.key_suffix), "");
    }

    private SharedPreferences getSharedPreferences() {
        return this.getPreferences(MODE_PRIVATE);
    }
}