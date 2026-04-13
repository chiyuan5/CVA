package com.roger.catloadinglibrary;

import android.app.Dialog;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

/**
 * Lightweight compatibility implementation for the original CatLoadingView API.
 */
public class CatLoadingView extends DialogFragment {
    private boolean clickCancelable = true;
    private int backgroundColorResId = 0;

    public void setBackgroundColor(int colorResId) {
        this.backgroundColorResId = colorResId;
    }

    public void setClickCancelAble(boolean clickCancelAble) {
        this.clickCancelable = clickCancelAble;
        Dialog dialog = getDialog();
        if (dialog != null) {
            dialog.setCanceledOnTouchOutside(clickCancelAble);
            dialog.setCancelable(clickCancelAble);
        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        Dialog dialog = new Dialog(requireContext());
        dialog.setCanceledOnTouchOutside(clickCancelable);
        dialog.setCancelable(clickCancelable);

        FrameLayout container = new FrameLayout(requireContext());
        int padding = (int) (24 * requireContext().getResources().getDisplayMetrics().density);
        container.setPadding(padding, padding, padding, padding);
        if (backgroundColorResId != 0) {
            try {
                container.setBackgroundColor(requireContext().getResources().getColor(backgroundColorResId));
            } catch (Throwable ignored) {
                // Keep default background when resource lookup fails.
            }
        }

        ProgressBar progressBar = new ProgressBar(requireContext());
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        );
        params.gravity = Gravity.CENTER;
        container.addView(progressBar, params);
        dialog.setContentView(container);
        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        }
        return dialog;
    }
}
