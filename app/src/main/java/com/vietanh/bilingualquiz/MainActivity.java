package com.vietanh.bilingualquiz;

import android.app.Activity;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowInsets;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.vietanh.bilingualquiz.ui.GradeSelectionActivity;
import com.vietanh.bilingualquiz.ui.StatisticsActivity;

public class MainActivity extends Activity {
    private static final String SETTINGS = "settings";
    private static final String LANGUAGE_KEY = "vietnamese";
    private static final int BLUE = Color.rgb(24, 74, 128);
    private boolean vietnamese;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setStatusBarColor(Color.rgb(246, 249, 253));
        getWindow().setNavigationBarColor(Color.rgb(246, 249, 253));
        vietnamese = getSharedPreferences(SETTINGS, MODE_PRIVATE)
                .getBoolean(LANGUAGE_KEY, true);

        ScrollView scroll = new ScrollView(this);
        scroll.setFillViewport(true);
        scroll.setBackgroundColor(Color.rgb(246, 249, 253));

        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        // Keep the first row below the system status bar even on edge-to-edge devices.
        root.setPadding(dp(24), dp(32), dp(24), dp(24));
        scroll.addView(root, new ScrollView.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));

        LinearLayout languageRow = new LinearLayout(this);
        languageRow.setGravity(Gravity.END | Gravity.CENTER_VERTICAL);
        root.addView(languageRow, new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, dp(56)));

        Button languageButton = new Button(this);
        languageButton.setText(vietnamese ? "EN" : "VI");
        languageButton.setTextSize(14);
        languageButton.setTypeface(Typeface.DEFAULT, Typeface.BOLD);
        languageButton.setTextColor(Color.WHITE);
        languageButton.setAllCaps(false);
        languageButton.setGravity(Gravity.CENTER);
        languageButton.setMinWidth(0);
        languageButton.setMinimumWidth(dp(64));
        languageButton.setMinimumHeight(dp(48));
        languageButton.setPadding(dp(12), 0, dp(12), 0);
        languageButton.setBackground(makeBackground(BLUE, dp(12)));
        languageButton.setContentDescription(vietnamese
                ? "Chuyển sang tiếng Anh"
                : "Switch to Vietnamese");
        languageButton.setClickable(true);
        languageButton.setFocusable(true);
        languageButton.setElevation(dp(3));
        languageButton.setOnClickListener(view -> {
            vietnamese = !vietnamese;
            getSharedPreferences(SETTINGS, MODE_PRIVATE).edit()
                    .putBoolean(LANGUAGE_KEY, vietnamese).apply();
            recreate();
        });
        languageRow.addView(languageButton, new LinearLayout.LayoutParams(dp(64), dp(48)));

        TextView title = new TextView(this);
        title.setText(vietnamese
                ? "Bilingual Quiz\nÔn tập trắc nghiệm"
                : "Bilingual Quiz\nMultiple-choice practice");
        title.setTextSize(26);
        title.setTypeface(Typeface.DEFAULT, Typeface.BOLD);
        title.setGravity(Gravity.CENTER);
        title.setTextColor(BLUE);
        title.setPadding(0, dp(28), 0, dp(28));
        root.addView(title, new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));

        addLevel(root, vietnamese ? "Cấp tiểu học" : "Primary school", 1, 5);
        addLevel(root, vietnamese ? "Cấp trung học cơ sở" : "Lower secondary school", 6, 9);
        addLevel(root, vietnamese ? "Cấp trung học phổ thông" : "Upper secondary school", 10, 12);

        Button statistics = makeMenuButton(vietnamese ? "Thống kê" : "Statistics");
        statistics.setOnClickListener(view ->
                startActivity(new Intent(this, StatisticsActivity.class)));
        root.addView(statistics);

        root.setOnApplyWindowInsetsListener((View view, WindowInsets insets) -> {
            view.setPadding(
                    dp(24) + insets.getSystemWindowInsetLeft(),
                    dp(32) + insets.getSystemWindowInsetTop(),
                    dp(24) + insets.getSystemWindowInsetRight(),
                    dp(24) + insets.getSystemWindowInsetBottom());
            return insets;
        });
        setContentView(scroll);
        root.requestApplyInsets();
    }

    private void addLevel(LinearLayout root, String label, int first, int last) {
        Button button = makeMenuButton(label);
        button.setOnClickListener(view -> {
            Intent intent = new Intent(this, GradeSelectionActivity.class);
            intent.putExtra("level", label);
            intent.putExtra("firstGrade", first);
            intent.putExtra("lastGrade", last);
            startActivity(intent);
        });
        root.addView(button);
    }

    private Button makeMenuButton(String label) {
        Button button = new Button(this);
        button.setText(label);
        button.setTextSize(17);
        button.setAllCaps(false);
        button.setTextColor(BLUE);
        button.setMinHeight(0);
        button.setMinimumHeight(dp(56));
        button.setPadding(dp(12), dp(8), dp(12), dp(8));
        button.setBackground(makeBackground(Color.WHITE, dp(14)));
        button.setElevation(dp(2));
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, dp(60));
        params.setMargins(0, dp(7), 0, dp(7));
        button.setLayoutParams(params);
        return button;
    }

    private GradientDrawable makeBackground(int color, int radius) {
        GradientDrawable drawable = new GradientDrawable();
        drawable.setColor(color);
        drawable.setCornerRadius(radius);
        return drawable;
    }

    private int dp(int value) {
        return Math.round(value * getResources().getDisplayMetrics().density);
    }
}
