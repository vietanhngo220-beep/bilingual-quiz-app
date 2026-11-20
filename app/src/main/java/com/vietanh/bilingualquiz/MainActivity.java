package com.vietanh.bilingualquiz;

import android.app.Activity;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.RippleDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;

import com.vietanh.bilingualquiz.ui.AppUi;
import com.vietanh.bilingualquiz.ui.GradeSelectionActivity;
import com.vietanh.bilingualquiz.ui.StatisticsActivity;

public class MainActivity extends Activity {
    private static final String SETTINGS = "settings";
    private static final String LANGUAGE_KEY = "vietnamese";
    private boolean vietnamese;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        vietnamese = getSharedPreferences(SETTINGS, MODE_PRIVATE)
                .getBoolean(LANGUAGE_KEY, true);
        LinearLayout screen = AppUi.screen(this, "Bilingual Quiz", false);

        LinearLayout languageRow = new LinearLayout(this);
        languageRow.setGravity(Gravity.END | Gravity.CENTER_VERTICAL);
        Button language = AppUi.button(this, vietnamese ? "EN" : "VI", false);
        language.setTextSize(14);
        language.setMinHeight(AppUi.dp(this, 48));
        language.setMinimumHeight(AppUi.dp(this, 48));
        language.setPadding(AppUi.dp(this, 16), AppUi.dp(this, 8),
                AppUi.dp(this, 16), AppUi.dp(this, 8));
        language.setContentDescription(vietnamese ? "Chuyển sang tiếng Anh" : "Switch to Vietnamese");
        language.setOnClickListener(view -> {
            vietnamese = !vietnamese;
            getSharedPreferences(SETTINGS, MODE_PRIVATE).edit()
                    .putBoolean(LANGUAGE_KEY, vietnamese).apply();
            recreate();
        });
        languageRow.addView(language, new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        AppUi.add(screen, languageRow, 0);
        AppUi.add(screen, AppUi.text(this, vietnamese ? "Ôn tập trắc nghiệm"
                : "Multiple-choice practice", 20, AppUi.TEXT, true), 8);
        AppUi.add(screen, AppUi.text(this, vietnamese ? "Chọn cấp học để bắt đầu luyện tập."
                : "Choose a school level to start practising.", 15, AppUi.MUTED, false), 6);

        addLevel(screen, vietnamese ? "Cấp tiểu học" : "Primary school", 1, 5);
        addLevel(screen, vietnamese ? "Cấp trung học cơ sở" : "Lower secondary school", 6, 9);
        addLevel(screen, vietnamese ? "Cấp trung học phổ thông" : "Upper secondary school", 10, 12);

        AppUi.add(screen, AppUi.text(this, vietnamese ? "Kết quả học tập" : "Your results",
                19, AppUi.TEXT, true), 28);
        Button statistics = AppUi.button(this, vietnamese ? "Xem thống kê" : "View statistics", true);
        statistics.setOnClickListener(view -> startActivity(new Intent(this, StatisticsActivity.class)));
        AppUi.add(screen, statistics, 12);
    }

    private void addLevel(LinearLayout root, String label, int first, int last) {
        LinearLayout card = AppUi.card(this);
        GradientDrawable shape = AppUi.background(this, AppUi.SURFACE, 20);
        shape.setStroke(AppUi.dp(this, 1), AppUi.BORDER);
        card.setBackground(new RippleDrawable(ColorStateList.valueOf(AppUi.LIGHT_BLUE),
                shape, AppUi.background(this, AppUi.SURFACE, 20)));
        card.setMinimumHeight(AppUi.dp(this, 84));
        card.setGravity(Gravity.CENTER_VERTICAL);
        card.setFocusable(true);
        card.setClickable(true);
        AppUi.add(card, AppUi.text(this, label, 17, AppUi.TEXT, true), 0);
        String range = (vietnamese ? "Lớp " : "Grades ") + first + "–" + last;
        AppUi.add(card, AppUi.text(this, range, 14, AppUi.MUTED, false), 6);
        card.setContentDescription(label + ", " + range);
        card.setOnClickListener(view -> {
            Intent intent = new Intent(this, GradeSelectionActivity.class);
            intent.putExtra("level", label);
            intent.putExtra("firstGrade", first);
            intent.putExtra("lastGrade", last);
            startActivity(intent);
        });
        AppUi.add(root, card, 14);
    }
}
