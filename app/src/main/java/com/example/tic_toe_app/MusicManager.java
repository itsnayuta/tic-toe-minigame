package com.example.tic_toe_app;

import android.content.Context;
import android.content.SharedPreferences;
import android.media.MediaPlayer;

public class MusicManager {
    private static MusicManager instance;
    private MediaPlayer mediaPlayer;
    private Context context;
    private SharedPreferences preferences;
    private boolean isMusicEnabled = true;
    private float currentVolume = 0.5f;
    private boolean isAppInBackground = false;
    private boolean isInitializing = false;
    
    private static final String PREFS_NAME = "MusicSettings";
    private static final String KEY_MUSIC_ENABLED = "music_enabled";
    private static final String KEY_VOLUME = "volume";
    
    private MusicManager(Context context) {
        this.context = context.getApplicationContext();
        this.preferences = this.context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        loadSettings();
        initializeMediaPlayer();
    }
    
    public static synchronized MusicManager getInstance(Context context) {
        if (instance == null) {
            instance = new MusicManager(context);
        }
        return instance;
    }
    
    private void initializeMediaPlayer() {
        if (isInitializing) return;
        
        isInitializing = true;
        try {
            if (mediaPlayer != null) {
                if (mediaPlayer.isPlaying()) {
                    mediaPlayer.stop();
                }
                mediaPlayer.release();
                mediaPlayer = null;
            }
            
            mediaPlayer = MediaPlayer.create(context, R.raw.music_bg);
            if (mediaPlayer != null) {
                mediaPlayer.setLooping(true);
                mediaPlayer.setVolume(currentVolume, currentVolume);
            }
        } catch (Exception e) {
            mediaPlayer = null;
        } finally {
            isInitializing = false;
        }
    }
    
    private void loadSettings() {
        isMusicEnabled = preferences.getBoolean(KEY_MUSIC_ENABLED, true);
        currentVolume = preferences.getFloat(KEY_VOLUME, 0.5f);
    }
    
    private void saveSettings() {
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean(KEY_MUSIC_ENABLED, isMusicEnabled);
        editor.putFloat(KEY_VOLUME, currentVolume);
        editor.apply();
    }
    
    public synchronized void startMusic() {
        if (mediaPlayer != null && isMusicEnabled && !isAppInBackground) {
            try {
                if (!mediaPlayer.isPlaying()) {
                    mediaPlayer.start();
                }
            } catch (Exception ignored) {}
        }
    }
    
    public synchronized void pauseMusic() {
        if (mediaPlayer != null) {
            try {
                if (mediaPlayer.isPlaying()) {
                    mediaPlayer.pause();
                }
            } catch (Exception ignored) {}
        }
    }
    
    public synchronized void setVolume(float volume) {
        currentVolume = Math.max(0f, Math.min(1f, volume));
        if (mediaPlayer != null) {
            try {
                mediaPlayer.setVolume(currentVolume, currentVolume);
            } catch (Exception ignored) {}
        }
        saveSettings();
    }
    
    public float getVolume() {
        return currentVolume;
    }
    
    public synchronized void setMusicEnabled(boolean enabled) {
        isMusicEnabled = enabled;
        if (!enabled) {
            pauseMusic();
        } else if (!isAppInBackground) {
            startMusic();
        }
        saveSettings();
    }
    
    public boolean isMusicEnabled() {
        return isMusicEnabled;
    }
    
    public boolean isPlaying() {
        try {
            return mediaPlayer != null && mediaPlayer.isPlaying();
        } catch (Exception e) {
            return false;
        }
    }
    
    public synchronized void release() {
        if (mediaPlayer != null) {
            try {
                if (mediaPlayer.isPlaying()) {
                    mediaPlayer.stop();
                }
                mediaPlayer.release();
                mediaPlayer = null;
            } catch (Exception e) {
                mediaPlayer = null;
            }
        }
    }
    
    public synchronized void onActivityResume() {
        isAppInBackground = false;
        if (isMusicEnabled) {
            if (mediaPlayer == null) {
                initializeMediaPlayer();
            }
            startMusic();
        }
    }
    
    public synchronized void onActivityPause() {
        pauseMusic();
    }
    
    public void onAppDestroy() {
        release();
        instance = null;
    }
}
