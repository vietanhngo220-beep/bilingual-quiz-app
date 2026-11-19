package com.vietanh.bilingualquiz.ui;

import android.app.Activity;
import android.os.Bundle;
import android.graphics.Color;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.vietanh.bilingualquiz.data.QuizDbHelper;

public class StatisticsActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        QuizDbHelper database = new QuizDbHelper(this);
        LinearLayout screen = new LinearLayout(this);
        screen.setOrientation(LinearLayout.VERTICAL);
        screen.setPadding(40, 48, 40, 40);

        TextView title = new TextView(this);
        title.setText("Thống kê kết quả / Statistics");
        title.setTextSize(25);
        title.setTextColor(Color.rgb(20, 60, 120));
        screen.addView(title);

        TextView summary = new TextView(this);
        summary.setText("\nSố bài đã làm: " + database.getAttemptCount()
                + "\n\nTổng điểm: " + database.getTotalScore()
                + "\n\nĐiểm trung bình: " + format(database.getAverageScore())
                + "\n\nĐiểm cao nhất: " + database.getHighestScore());
        summary.setTextSize(19);
        summary.setTextColor(Color.DKGRAY);
        screen.addView(summary);

        Button wrongQuestions = new Button(this);
        wrongQuestions.setText("Xem câu sai / Review wrong questions");
        wrongQuestions.setOnClickListener(view -> startActivity(
                new android.content.Intent(this, WrongQuestionsActivity.class)));
        screen.addView(wrongQuestions);

        Button refresh = new Button(this);
        refresh.setText("Cập nhật thống kê / Refresh");
        refresh.setOnClickListener(view -> recreate());
        screen.addView(refresh);
        setContentView(screen);
    }

    private String format(double value) {
        return String.format(java.util.Locale.US, "%.1f", value);
    }
}
