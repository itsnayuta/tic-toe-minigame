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
    private MusicManager musicManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_settings);
        
        musicManager = MusicManager.getInstance(this);
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
        float currentVolume = musicManager.getVolume();
        boolean isMusicEnabled = musicManager.isMusicEnabled();
        
        int volumePercent = (int) (currentVolume * 100);
        volumeSeekBar.setProgress(volumePercent);
        muteToggleButton.setChecked(!isMusicEnabled);
        volumeSeekBar.setEnabled(isMusicEnabled);
        updateVolumeText(isMusicEnabled ? volumePercent : 0);

        volumeSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    float volume = progress / 100.0f;
                    musicManager.setVolume(volume);
                    updateVolumeText(progress);
                    
                    if (progress > 0 && muteToggleButton.isChecked()) {
                        muteToggleButton.setChecked(false);
                        musicManager.setMusicEnabled(true);
                        volumeSeekBar.setEnabled(true);
                    }
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });

        muteToggleButton.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                musicManager.setMusicEnabled(false);
                volumeSeekBar.setEnabled(false);
                updateVolumeText(0);
            } else {
                musicManager.setMusicEnabled(true);
                volumeSeekBar.setEnabled(true);
                updateVolumeText(volumeSeekBar.getProgress());
            }
        });

        btnBack.setOnClickListener(v -> finish());
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (musicManager != null) {
            musicManager.onActivityResume();
        }
    }
    
    @Override
    protected void onPause() {
        super.onPause();
        if (musicManager != null) {
            musicManager.onActivityPause();
        }
    }

    private void updateVolumeText(int volume) {
        if (muteToggleButton.isChecked()) {
            volumeValueText.setText("Volume: Muted");
        } else {
            volumeValueText.setText("Volume: " + volume + "%");
        }
    }
}