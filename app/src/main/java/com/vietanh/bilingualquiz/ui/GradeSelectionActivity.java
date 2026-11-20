package com.vietanh.bilingualquiz.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.LinearLayout;

public class GradeSelectionActivity extends Activity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        boolean vi = getSharedPreferences("settings", MODE_PRIVATE)
                .getBoolean("vietnamese", true);
        int first = getIntent().getIntExtra("firstGrade", 1);
        int last = getIntent().getIntExtra("lastGrade", 5);
        LinearLayout screen = AppUi.screen(this, vi ? "Chọn lớp" : "Choose grade", true);
        AppUi.add(screen, AppUi.text(this,
                vi ? "Chọn lớp bạn muốn ôn tập để bắt đầu."
                        : "Choose a grade to start practising.",
                15, AppUi.MUTED, false), 6);

        LinearLayout grades = AppUi.card(this);
        for (int grade = first; grade <= last; grade++) {
            final int selectedGrade = grade;
            Button button = AppUi.button(this, (vi ? "Lớp " : "Grade ") + grade, false);
            button.setOnClickListener(view -> {
                Intent intent = new Intent(this, QuizActivity.class);
                intent.putExtra("grade", selectedGrade);
                startActivity(intent);
            });
            AppUi.add(grades, button, grade == first ? 0 : 12);
        }
        AppUi.add(screen, grades, 24);
    }
}
