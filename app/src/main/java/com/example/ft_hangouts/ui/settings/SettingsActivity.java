package com.example.ft_hangouts.ui.settings;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.ft_hangouts.R;
import com.example.ft_hangouts.Utils;
import com.example.ft_hangouts.databinding.ActivitySettingsBinding;

import java.util.Objects;

public class SettingsActivity extends AppCompatActivity {

    private ActivitySettingsBinding binding;
    int theme;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        theme = Utils.setTheme(this);

        binding = ActivitySettingsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Setup toolbar
        binding.settingToolbar.myToolbar.setTitle(R.string.title_settings);
        setSupportActionBar(binding.settingToolbar.myToolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.settings_layout), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        if (theme == R.style.Overlay_orange) {
            binding.radioOrangeColor.setChecked(true);
        } else if (theme == R.style.Overlay_green) {
            binding.radioGreenColor.setChecked(true);
        } else if (theme == R.style.Overlay_pink) {
            binding.radioPinkColor.setChecked(true);
        } else if (theme == R.style.Overlay_purple) {
            binding.radioPurpleColor.setChecked(true);
        } else if (theme == R.style.Overlay_turquoise) {
            binding.radioTurquoiseColor.setChecked(true);
        }

        binding.radioGroup.setOnCheckedChangeListener((group, checkedId) -> {
            int theme = 0;
            if (checkedId == R.id.radio_orange_color) {
                theme = R.style.Overlay_orange;
                binding.radioOrangeColor.setChecked(true);
            } else if (checkedId == R.id.radio_green_color) {
                theme = R.style.Overlay_green;
                binding.radioGreenColor.setChecked(true);
            } else if (checkedId == R.id.radio_pink_color) {
                theme = R.style.Overlay_pink;
                binding.radioPinkColor.setChecked(true);
            } else if (checkedId == R.id.radio_purple_color) {
                theme = R.style.Overlay_purple;
                binding.radioPurpleColor.setChecked(true);
            } else if (checkedId == R.id.radio_turquoise_color) {
                theme = R.style.Overlay_turquoise;
                binding.radioTurquoiseColor.setChecked(true);
            }

            SharedPreferences.Editor editor = getSharedPreferences().edit();
            editor.putInt("theme", theme);
            editor.apply();

            Intent intent = getIntent();
            intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
            finish();
            overridePendingTransition(0, 0);
            startActivity(intent);
        });

        binding.saveSettingsButton.setOnClickListener(v -> {
            SharedPreferences.Editor editor = getSharedPreferences().edit();
            editor.putBoolean("theme_changed", true);
            editor.apply();
            finish();
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            SharedPreferences.Editor editor = getSharedPreferences().edit();
            editor.putInt("theme", theme);
            editor.apply();
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private SharedPreferences getSharedPreferences() {
        return getSharedPreferences(getPackageName() + "_preferences", Context.MODE_PRIVATE);
    }
}