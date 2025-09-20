package com.example.tic_toe_app.ui;

import android.content.DialogInterface;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
public class ResultDialog {
    public interface Callback {
        void onPlayAgain();
    }
    public static void show(AppCompatActivity activity, String title, String message, final Callback cb) {
        AlertDialog.Builder b = new AlertDialog.Builder(activity);
        b.setTitle(title)
                .setMessage(message)
                .setPositiveButton("Play Again", (dialog, which) -> {
                    if (cb != null) cb.onPlayAgain();
                })
                .setNegativeButton("Back", (dialog, which) -> activity.finish())
                .setCancelable(false)
                .show();
    }
}