package com.vietanh.bilingualquiz.ui;

import android.app.Activity;
import android.os.Bundle;
import android.graphics.Color;
import android.database.Cursor;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.ScrollView;
import android.widget.ProgressBar;
import com.vietanh.bilingualquiz.data.QuizDbHelper;
import com.vietanh.bilingualquiz.LanguageSettings;

public class StatisticsActivity extends Activity {
    private static final String SELECTED_GRADE = "selectedGrade";
    private boolean vi;
    private int selectedGrade;
    private TextView summary;
    private TextView empty;
    private TextView accuracySummary;
    private ProgressBar accuracyProgress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        vi = LanguageSettings.isVietnamese(this);
        selectedGrade = savedInstanceState == null ? 0
                : savedInstanceState.getInt(SELECTED_GRADE, 0);
        ScrollView scroll = new ScrollView(this);
        LinearLayout screen = new LinearLayout(this);
        screen.setOrientation(LinearLayout.VERTICAL);
        screen.setPadding(dp(24), dp(24), dp(24), dp(24));
        scroll.addView(screen);
        screen.setOnApplyWindowInsetsListener((view, insets) -> {
            view.setPadding(dp(24) + insets.getSystemWindowInsetLeft(),
                    dp(24) + insets.getSystemWindowInsetTop(),
                    dp(24) + insets.getSystemWindowInsetRight(),
                    dp(24) + insets.getSystemWindowInsetBottom());
            return insets;
        });

        TextView title = new TextView(this);
        title.setText(vi ? "Thống kê kết quả" : "Statistics");
        title.setTextSize(25);
        title.setTextColor(Color.rgb(20, 60, 120));
        screen.addView(title);

        TextView filterLabel = new TextView(this);
        filterLabel.setText(vi ? "Lọc theo lớp" : "Filter by grade");
        filterLabel.setPadding(0, dp(16), 0, dp(4));
        screen.addView(filterLabel);

        String[] grades = new String[13];
        grades[0] = vi ? "Tất cả lớp" : "All grades";
        for (int grade = 1; grade <= 12; grade++) {
            grades[grade] = (vi ? "Lớp " : "Grade ") + grade;
        }
        Spinner gradeFilter = new Spinner(this);
        gradeFilter.setContentDescription(vi ? "Lọc theo lớp" : "Filter by grade");
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, grades);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        gradeFilter.setAdapter(adapter);
        gradeFilter.setSelection(selectedGrade);
        screen.addView(gradeFilter, new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, dp(48)));

        summary = new TextView(this);
        summary.setTextSize(19);
        summary.setTextColor(Color.DKGRAY);
        screen.addView(summary);

        accuracySummary = new TextView(this);
        accuracySummary.setTextSize(19);
        accuracySummary.setTextColor(Color.DKGRAY);
        accuracySummary.setPadding(0, dp(16), 0, dp(8));
        screen.addView(accuracySummary);

        accuracyProgress = new ProgressBar(this, null, android.R.attr.progressBarStyleHorizontal);
        accuracyProgress.setMax(1000);
        screen.addView(accuracyProgress, new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, dp(12)));

        empty = new TextView(this);
        empty.setTextSize(16);
        empty.setPadding(0, dp(12), 0, dp(12));
        screen.addView(empty);

        gradeFilter.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedGrade = position;
                updateStatistics();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) { }
        });

        Button historyButton = new Button(this);
        historyButton.setText(vi ? "Lịch sử làm bài" : "Quiz history");
        historyButton.setOnClickListener(view -> startActivity(
                new android.content.Intent(this, HistoryActivity.class)));
        screen.addView(historyButton);
        Button wrongQuestions = new Button(this);
        wrongQuestions.setText(vi ? "Xem câu sai" : "Review wrong questions");
        wrongQuestions.setOnClickListener(view -> startActivity(
                new android.content.Intent(this, WrongQuestionsActivity.class)));
        screen.addView(wrongQuestions);

        Button refresh = new Button(this);
        refresh.setText(vi ? "Cập nhật thống kê" : "Refresh");
        refresh.setOnClickListener(view -> updateStatistics());
        screen.addView(refresh);
        setContentView(scroll);
        screen.requestApplyInsets();
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateStatistics();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putInt(SELECTED_GRADE, selectedGrade);
        super.onSaveInstanceState(outState);
    }

    private void updateStatistics() {
        QuizDbHelper database = new QuizDbHelper(this);
        try (Cursor result = database.getStatistics(selectedGrade)) {
            if (result.moveToFirst()) {
                long correct = result.getLong(1);
                long total = result.getLong(4);
                double accuracy = total > 0 ? 100.0 * correct / total : 0.0;
                String accuracyText = format(accuracy) + "%";
                accuracySummary.setText(vi
                        ? "Tổng số câu đã trả lời: " + total
                                + "\n\nSố câu đúng: " + correct
                                + "\n\nTỉ lệ trả lời đúng: " + accuracyText
                        : "Total questions answered: " + total
                                + "\n\nCorrect answers: " + correct
                                + "\n\nAccuracy: " + accuracyText);
                accuracyProgress.setProgress((int) Math.round(accuracy * 10));
                accuracyProgress.setContentDescription((vi ? "Tỉ lệ trả lời đúng: " : "Accuracy: ")
                        + accuracyText);
                summary.setText(vi ? "\nSố bài đã làm: " + result.getInt(0)
                        + "\n\nTổng điểm: " + result.getInt(1)
                        + "\n\nĐiểm trung bình: " + format(result.getDouble(2))
                        + "\n\nĐiểm cao nhất: " + result.getInt(3)
                        : "\nAttempts: " + result.getInt(0)
                        + "\n\nTotal score: " + result.getInt(1)
                        + "\n\nAverage score: " + format(result.getDouble(2))
                        + "\n\nHighest score: " + result.getInt(3));
                empty.setVisibility(result.getInt(0) == 0 ? View.VISIBLE : View.GONE);
                empty.setText(selectedGrade == 0
                        ? (vi ? "Chưa có bài làm nào." : "No attempts yet.")
                        : (vi ? "Chưa có bài làm cho lớp " + selectedGrade + "."
                                : "No attempts for grade " + selectedGrade + "."));
            }
        } finally {
            database.close();
        }
    }

    private int dp(int value) {
        return Math.round(value * getResources().getDisplayMetrics().density);
    }

    private String format(double value) {
        return String.format(java.util.Locale.US, "%.1f", value);
    }
}
