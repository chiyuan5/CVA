package com.github.nukc.stateview;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.Gravity;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

/**
 * Minimal compatibility StateView with loading/empty/content state switching.
 */
public class StateView extends FrameLayout {
    private final ProgressBar progressBar;
    private final TextView emptyView;

    public StateView(Context context) {
        this(context, null);
    }

    public StateView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public StateView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setClickable(true);
        setBackgroundColor(Color.TRANSPARENT);

        progressBar = new ProgressBar(context);
        LayoutParams progressParams = new LayoutParams(
                LayoutParams.WRAP_CONTENT,
                LayoutParams.WRAP_CONTENT
        );
        progressParams.gravity = Gravity.CENTER;
        addView(progressBar, progressParams);

        emptyView = new TextView(context);
        emptyView.setText("No data");
        emptyView.setGravity(Gravity.CENTER);
        LayoutParams emptyParams = new LayoutParams(
                LayoutParams.WRAP_CONTENT,
                LayoutParams.WRAP_CONTENT
        );
        emptyParams.gravity = Gravity.CENTER;
        addView(emptyView, emptyParams);

        showContent();
    }

    public void showLoading() {
        setVisibility(VISIBLE);
        progressBar.setVisibility(VISIBLE);
        emptyView.setVisibility(GONE);
    }

    public void showEmpty() {
        setVisibility(VISIBLE);
        progressBar.setVisibility(GONE);
        emptyView.setVisibility(VISIBLE);
    }

    public void showContent() {
        progressBar.setVisibility(GONE);
        emptyView.setVisibility(GONE);
        setVisibility(GONE);
    }
}
