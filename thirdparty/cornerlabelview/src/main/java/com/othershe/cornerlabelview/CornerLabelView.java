package com.othershe.cornerlabelview;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;

import com.othershe.cornerlabelview.R;

/**
 * Lightweight compatibility implementation used only as a layout widget in this project.
 */
public class CornerLabelView extends androidx.appcompat.widget.AppCompatTextView {
    public CornerLabelView(Context context) {
        this(context, null);
    }

    public CornerLabelView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CornerLabelView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        setGravity(Gravity.CENTER);
        int bgColor = 0xFF6200EE;
        int textColor = Color.WHITE;
        float textSizePx = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 10,
                getResources().getDisplayMetrics());
        CharSequence label = getText();

        if (attrs != null) {
            TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.CornerLabelView);
            bgColor = ta.getColor(R.styleable.CornerLabelView_bg_color, bgColor);
            textColor = ta.getColor(R.styleable.CornerLabelView_text_color, textColor);
            textSizePx = ta.getDimension(R.styleable.CornerLabelView_text_size, textSizePx);
            CharSequence attrText = ta.getText(R.styleable.CornerLabelView_text);
            if (attrText != null) {
                label = attrText;
            }
            ta.recycle();
        }

        setText(label);
        setTextColor(textColor);
        setTextSize(TypedValue.COMPLEX_UNIT_PX, textSizePx);

        GradientDrawable drawable = new GradientDrawable();
        drawable.setColor(bgColor);
        drawable.setCornerRadius(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 4,
                getResources().getDisplayMetrics()));
        setBackground(drawable);
    }
}
