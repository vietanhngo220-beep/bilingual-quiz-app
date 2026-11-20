package com.vietanh.bilingualquiz.ui;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.RippleDrawable;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

/** Shared colors, spacing and controls for programmatic app screens. */
public final class AppUi {
    public static final int BACKGROUND = Color.rgb(246, 249, 253);
    public static final int SURFACE = Color.WHITE;
    public static final int BLUE = Color.rgb(24, 74, 128);
    public static final int TEXT = Color.rgb(23, 43, 70);
    public static final int MUTED = Color.rgb(82, 100, 122);
    public static final int BORDER = Color.rgb(223, 231, 241);
    public static final int GREEN = Color.rgb(22, 114, 75);
    public static final int LIGHT_BLUE = Color.rgb(234, 242, 252);

    private AppUi() { }

    /** Installs the common page shell and returns the content area for each member's screen. */
    public static LinearLayout screen(android.app.Activity activity, String title, boolean withBack) {
        activity.getWindow().setStatusBarColor(BACKGROUND);
        activity.getWindow().setNavigationBarColor(BACKGROUND);
        int systemBars = android.view.View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR;
        if (android.os.Build.VERSION.SDK_INT >= 26) {
            systemBars |= android.view.View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR;
        }
        activity.getWindow().getDecorView().setSystemUiVisibility(systemBars);
        LinearLayout page = new LinearLayout(activity);
        page.setOrientation(LinearLayout.VERTICAL);
        page.setBackgroundColor(BACKGROUND);
        page.setOnApplyWindowInsetsListener((view, insets) -> {
            view.setPadding(insets.getSystemWindowInsetLeft(), insets.getSystemWindowInsetTop(),
                    insets.getSystemWindowInsetRight(), insets.getSystemWindowInsetBottom());
            return insets;
        });
        if (withBack) {
            LinearLayout toolbar = new LinearLayout(activity);
            toolbar.setGravity(Gravity.CENTER_VERTICAL);
            toolbar.setPadding(dp(activity, 20), dp(activity, 4), dp(activity, 20), 0);
            boolean vi = com.vietanh.bilingualquiz.LanguageSettings.isVietnamese(activity);
            Button back = button(activity, vi ? "Quay lại" : "Back", false);
            back.setTextSize(14);
            back.setIncludeFontPadding(false);
            back.setMinHeight(dp(activity, 48));
            back.setMinimumHeight(dp(activity, 48));
            back.setPadding(dp(activity, 12), dp(activity, 8), dp(activity, 14), dp(activity, 8));
            back.setCompoundDrawablesRelative(backIcon(activity), null, null, null);
            back.setCompoundDrawablePadding(dp(activity, 6));
            back.setContentDescription(vi ? "Quay lại màn hình trước" : "Go back to the previous screen");
            back.setBackground(new RippleDrawable(ColorStateList.valueOf(Color.argb(35, 24, 74, 128)),
                    background(activity, LIGHT_BLUE, 12),
                    background(activity, Color.WHITE, 12)));
            back.setOnClickListener(view -> activity.finish());
            toolbar.addView(back, new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            page.addView(toolbar, new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        }
        android.widget.ScrollView scroll = new android.widget.ScrollView(activity);
        scroll.setFillViewport(true);
        scroll.setClipToPadding(true);
        LinearLayout center = new LinearLayout(activity);
        center.setGravity(Gravity.CENTER_HORIZONTAL);
        center.setPadding(dp(activity, 20), dp(activity, 12), dp(activity, 20), dp(activity, 24));
        scroll.addView(center);
        LinearLayout content = new LinearLayout(activity);
        content.setOrientation(LinearLayout.VERTICAL);
        int width = activity.getResources().getConfiguration().screenWidthDp > 680
                ? dp(activity, 640) : ViewGroup.LayoutParams.MATCH_PARENT;
        center.addView(content, new LinearLayout.LayoutParams(width, ViewGroup.LayoutParams.WRAP_CONTENT));
        add(content, text(activity, title, 28, TEXT, true), 8);
        page.addView(scroll, new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 0, 1));
        activity.setContentView(page);
        page.requestApplyInsets();
        return content;
    }

    private static android.graphics.drawable.Drawable backIcon(Context context) {
        android.graphics.Path path = new android.graphics.Path();
        path.moveTo(20, 11);
        path.lineTo(7.83f, 11);
        path.lineTo(13.42f, 5.41f);
        path.lineTo(12, 4);
        path.lineTo(4, 12);
        path.lineTo(12, 20);
        path.lineTo(13.41f, 18.59f);
        path.lineTo(7.83f, 13);
        path.lineTo(20, 13);
        path.close();
        android.graphics.drawable.ShapeDrawable icon = new android.graphics.drawable.ShapeDrawable(
                new android.graphics.drawable.shapes.PathShape(path, 24, 24));
        icon.getPaint().setColor(BLUE);
        icon.getPaint().setAntiAlias(true);
        icon.setBounds(0, 0, dp(context, 20), dp(context, 20));
        return icon;
    }

    public static void add(LinearLayout parent, android.view.View child, int topDp) {
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.topMargin = dp(parent.getContext(), topDp);
        parent.addView(child, params);
    }

    public static int dp(Context context, int value) {
        return Math.round(value * context.getResources().getDisplayMetrics().density);
    }

    public static TextView text(Context context, String label, int size, int color,
                                boolean bold) {
        TextView text = new TextView(context);
        text.setText(label);
        text.setTextSize(size);
        text.setTextColor(color);
        text.setTypeface(Typeface.DEFAULT, bold ? Typeface.BOLD : Typeface.NORMAL);
        text.setLineSpacing(dp(context, 2), 1.0f);
        return text;
    }

    public static LinearLayout card(Context context) {
        LinearLayout card = new LinearLayout(context);
        card.setOrientation(LinearLayout.VERTICAL);
        int padding = dp(context, 16);
        card.setPadding(padding, padding, padding, padding);
        GradientDrawable shape = background(context, SURFACE, 20);
        shape.setStroke(dp(context, 1), BORDER);
        card.setBackground(shape);
        return card;
    }

    /** Radius is expressed in dp, like the spacing helpers in this class. */
    public static GradientDrawable background(Context context, int color, int radius) {
        GradientDrawable drawable = new GradientDrawable();
        drawable.setColor(color);
        drawable.setCornerRadius(dp(context, radius));
        return drawable;
    }

    public static Button button(Context context, String label, boolean primary) {
        Button button = new Button(context);
        button.setText(label);
        button.setTextSize(16);
        button.setTypeface(Typeface.DEFAULT, Typeface.BOLD);
        button.setTextColor(primary ? SURFACE : BLUE);
        button.setAllCaps(false);
        button.setSingleLine(false);
        button.setGravity(Gravity.CENTER);
        button.setMinWidth(0);
        button.setMinimumWidth(0);
        button.setMinHeight(dp(context, 52));
        button.setMinimumHeight(dp(context, 52));
        button.setPadding(dp(context, 16), dp(context, 12),
                dp(context, 16), dp(context, 12));
        GradientDrawable shape = background(context, primary ? BLUE : LIGHT_BLUE, 14);
        int ripple = primary ? Color.argb(55, 255, 255, 255) : Color.argb(35, 24, 74, 128);
        button.setStateListAnimator(null);
        button.setElevation(0);
        button.setBackgroundTintList(null);
        button.setBackground(new RippleDrawable(ColorStateList.valueOf(ripple), shape,
                background(context, Color.WHITE, 14)));
        button.setLayoutParams(new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        return button;
    }
}
