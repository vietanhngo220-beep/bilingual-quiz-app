package com.vietanh.bilingualquiz.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.graphics.Color;
import android.view.Gravity;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.vietanh.bilingualquiz.R;

public class GradeSelectionActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        String level = getIntent().getStringExtra("level");
        int first = getIntent().getIntExtra("firstGrade", 1);
        int last = getIntent().getIntExtra("lastGrade", 5);
        LinearLayout screen = new LinearLayout(this);
        screen.setOrientation(LinearLayout.VERTICAL);
        screen.setGravity(Gravity.CENTER_HORIZONTAL);
        screen.setPadding(40, 48, 40, 40);
        TextView title = new TextView(this);
        title.setText("Chọn lớp / Choose grade\n" + level);
        title.setTextSize(24);
        title.setTextColor(Color.rgb(20, 60, 120));
        screen.addView(title);
        for (int grade = first; grade <= last; grade++) {
            final int selectedGrade = grade;
            Button button = new Button(this);
            button.setText("Lớp " + grade + " / Grade " + grade);
            button.setOnClickListener(view -> {
                Intent intent = new Intent(this, QuizActivity.class);
                intent.putExtra("grade", selectedGrade);
                startActivity(intent);
            });
            screen.addView(button);
        }
        setContentView(screen);
    }
}
