package cz.vitlabuda.icanteenjidelnicek;

import android.view.View;

import androidx.annotation.StringRes;

import com.google.android.material.snackbar.Snackbar;

public final class SnackbarMaker {
    private final View view;

    public SnackbarMaker(View view) {
        this.view = view;
    }

    public void make(String message, int duration) {
        Snackbar.make(view, message, duration).show();
    }
}
