package com.example.tic_toe_app;

import android.os.Bundle;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.ToggleButton;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class SettingsActivity extends AppCompatActivity {

    private SeekBar volumeSeekBar;
    private TextView volumeValueText;
    private ToggleButton muteToggleButton;
    private Button btnBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_settings);
        
        initializeViews();
        setupVolumeControls();
        
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    private void initializeViews() {
        volumeSeekBar = findViewById(R.id.volumeSeekBar);
        volumeValueText = findViewById(R.id.volumeValueText);
        muteToggleButton = findViewById(R.id.muteToggleButton);
        btnBack = findViewById(R.id.btnBack);
    }

    private void setupVolumeControls() {
        // Set initial volume value
        int currentVolume = 70; // Default volume
        volumeSeekBar.setProgress(currentVolume);
        updateVolumeText(currentVolume);

        // Volume SeekBar listener
        volumeSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    updateVolumeText(progress);
                    // Here you would update the actual game volume
                    // MusicManager.setVolume(progress / 100.0f);
                    
                    // If volume is moved, automatically unmute
                    if (progress > 0 && muteToggleButton.isChecked()) {
                        muteToggleButton.setChecked(false);
                    }
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });

        // Mute/Unmute toggle button listener
        muteToggleButton.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                // Mute: Save current volume and set to 0
                volumeSeekBar.setEnabled(false);
                updateVolumeText(0);
                // MusicManager.setVolume(0.0f);
            } else {
                // Unmute: Restore volume
                volumeSeekBar.setEnabled(true);
                updateVolumeText(volumeSeekBar.getProgress());
                // MusicManager.setVolume(volumeSeekBar.getProgress() / 100.0f);
            }
        });

        // Back button listener
        btnBack.setOnClickListener(v -> {
            finish(); // Close settings and return to previous activity
        });
    }

    private void updateVolumeText(int volume) {
        if (muteToggleButton.isChecked()) {
            volumeValueText.setText("Volume: Muted");
        } else {
            volumeValueText.setText("Volume: " + volume + "%");
        }
    }
}