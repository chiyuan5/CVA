package com.imuxuan.floatingview;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;

import java.lang.ref.WeakReference;

/**
 * Minimal in-app floating view manager compatible with the APIs used by this project.
 */
public final class FloatingView {
    private static final FloatingView INSTANCE = new FloatingView();

    private WeakReference<Activity> attachedActivity;
    private FloatingMagnetView view;

    private FloatingView() {
    }

    public static FloatingView get() {
        return INSTANCE;
    }

    public void customView(FloatingMagnetView customView) {
        removeFromParent(customView);
        this.view = customView;
        Activity activity = attachedActivity != null ? attachedActivity.get() : null;
        if (activity != null) {
            attach(activity);
        }
    }

    public FloatingMagnetView getView() {
        return view;
    }

    public void attach(Activity activity) {
        attachedActivity = new WeakReference<>(activity);
        if (activity == null || view == null) {
            return;
        }
        ViewGroup root = activity.findViewById(android.R.id.content);
        if (root == null) {
            return;
        }
        removeFromParent(view);
        root.addView(view);
        view.bringToFront();
    }

    public void detach(Activity activity) {
        if (view == null) {
            return;
        }
        removeFromParent(view);
        if (attachedActivity != null && attachedActivity.get() == activity) {
            attachedActivity.clear();
        }
    }

    public void add() {
        Activity activity = attachedActivity != null ? attachedActivity.get() : null;
        if (activity != null) {
            attach(activity);
        }
    }

    public void remove() {
        if (view != null) {
            removeFromParent(view);
        }
    }

    private void removeFromParent(View child) {
        if (child == null) {
            return;
        }
        ViewGroup parent = child.getParent() instanceof ViewGroup ? (ViewGroup) child.getParent() : null;
        if (parent != null) {
            parent.removeView(child);
        }
    }
}
