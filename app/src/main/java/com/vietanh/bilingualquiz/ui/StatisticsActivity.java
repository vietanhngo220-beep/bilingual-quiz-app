package com.vietanh.bilingualquiz.ui;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.database.Cursor;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.ProgressBar;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.vietanh.bilingualquiz.LanguageSettings;
import com.vietanh.bilingualquiz.data.QuizDbHelper;

/** Shared main layout combined with the statistics module and its saved filters. */
public class StatisticsActivity extends Activity {
    private static final String SELECTED_GRADE = "selectedGrade";
    private boolean vi;
    private int selectedGrade;
    private TextView questionsValue;
    private TextView correctValue;
    private TextView accuracyValue;
    private ProgressBar accuracyProgress;
    private StatisticsChartView gradeChart;
    private StatisticsChartView answerChart;
    private TextView attemptsValue;
    private TextView totalValue;
    private TextView averageValue;
    private TextView highestValue;
    private TextView empty;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        vi = LanguageSettings.isVietnamese(this);
        selectedGrade = savedInstanceState == null ? 0 : savedInstanceState.getInt(SELECTED_GRADE, 0);
        LinearLayout screen = AppUi.screen(this,
                vi ? "Thống kê kết quả" : "Statistics", true);
        TextView subtitle = AppUi.text(this,
                vi ? "Tổng quan các lượt làm bài của bạn" : "Your quiz results at a glance",
                15, AppUi.MUTED, false);
        add(screen, subtitle, 6);
        LinearLayout filterCard = AppUi.card(this);
        add(filterCard, AppUi.text(this, vi ? "Lọc theo lớp" : "Filter by grade",
                14, AppUi.MUTED, false), 0);
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
        gradeFilter.setMinimumHeight(dp(48));
        add(filterCard, gradeFilter, 4);
        add(screen, filterCard, 20);

        attemptsValue = value();
        totalValue = value();
        averageValue = value();
        highestValue = value();
        LinearLayout overview = metric(vi ? "Số bài đã làm" : "Attempts", attemptsValue,
                vi ? "Tổng số lượt làm bài đã lưu" : "Total saved quiz attempts", true);
        add(screen, overview, 24);
        LinearLayout scores = AppUi.card(this);
        scoreRow(scores, vi ? "Tổng điểm" : "Total score", totalValue, false);
        scoreRow(scores, vi ? "Điểm trung bình" : "Average score", averageValue, true);
        scoreRow(scores, vi ? "Điểm cao nhất" : "Highest score", highestValue, true);
        add(screen, scores, 12);
        LinearLayout accuracyCard = AppUi.card(this);
        questionsValue = value();
        correctValue = value();
        accuracyValue = value();
        scoreRow(accuracyCard, vi ? "Số câu đã trả lời" : "Questions answered", questionsValue, false);
        scoreRow(accuracyCard, vi ? "Số câu đúng" : "Correct answers", correctValue, true);
        scoreRow(accuracyCard, vi ? "Tỷ lệ trả lời đúng" : "Accuracy", accuracyValue, true);
        accuracyProgress = new ProgressBar(this, null, android.R.attr.progressBarStyleHorizontal);
        accuracyProgress.setMax(1000);
        accuracyProgress.setProgressTintList(android.content.res.ColorStateList.valueOf(AppUi.GREEN));
        accuracyProgress.setProgressBackgroundTintList(android.content.res.ColorStateList.valueOf(AppUi.BORDER));
        add(accuracyCard, accuracyProgress, 12);
        add(screen, accuracyCard, 12);

        empty = AppUi.text(this, vi
                ? "Chưa có bài làm nào. Kết quả sẽ xuất hiện sau khi bạn hoàn thành bài luyện tập."
                : "No attempts yet. Your results will appear after you complete a practice quiz.",
                14, AppUi.MUTED, false);
        add(screen, empty, 16);

        LinearLayout barCard = AppUi.card(this);
        add(barCard, AppUi.text(this, vi ? "Kết quả theo lớp" : "Results by grade",
                18, AppUi.TEXT, true), 0);
        add(barCard, AppUi.text(this, vi ? "Tổng số câu đã trả lời ở từng lớp"
                : "Total questions answered by grade", 13, AppUi.MUTED, false), 4);
        gradeChart = new StatisticsChartView(this, false, vi);
        add(barCard, gradeChart, 16);
        add(screen, barCard, 20);

        LinearLayout donutCard = AppUi.card(this);
        add(donutCard, AppUi.text(this, vi ? "Tỷ lệ trả lời" : "Answer breakdown",
                18, AppUi.TEXT, true), 0);
        answerChart = new StatisticsChartView(this, true, vi);
        add(donutCard, answerChart, 8);
        LinearLayout legend = new LinearLayout(this);
        legend.setGravity(Gravity.CENTER);
        boolean largeText = getResources().getConfiguration().fontScale > 1.2f;
        legend.setOrientation(largeText ? LinearLayout.VERTICAL : LinearLayout.HORIZONTAL);
        TextView correctLegend = AppUi.text(this, vi ? "●  Đúng" : "●  Correct",
                14, AppUi.GREEN, false);
        TextView wrongLegend = AppUi.text(this, vi ? "●  Sai" : "●  Incorrect",
                14, Color.rgb(164, 78, 37), false);
        legend.addView(correctLegend);
        wrongLegend.setPadding(largeText ? 0 : dp(24), largeText ? dp(8) : 0, 0, 0);
        legend.addView(wrongLegend);
        add(donutCard, legend, 4);
        add(screen, donutCard, 16);

        TextView reviewHeading = AppUi.text(this, vi ? "Ôn tập" : "Review", 19,
                AppUi.TEXT, true);
        add(screen, reviewHeading, 28);
        LinearLayout review = AppUi.card(this);
        add(review, AppUi.text(this, vi ? "Câu hỏi cần xem lại" : "Questions to revisit",
                18, AppUi.TEXT, true), 0);
        add(review, AppUi.text(this, vi ? "Xem lại các câu đã trả lời sai để ôn tập."
                : "Revisit your incorrect answers and keep practising.",
                14, AppUi.MUTED, false), 8);
        Button wrongQuestions = AppUi.button(this,
                vi ? "Xem câu sai" : "Review wrong questions", true);
        wrongQuestions.setOnClickListener(view -> startActivity(
                new Intent(this, WrongQuestionsActivity.class)));
        add(review, wrongQuestions, 18);
        Button history = AppUi.button(this, vi ? "Lịch sử làm bài" : "Quiz history", false);
        history.setOnClickListener(view -> startActivity(new Intent(this, HistoryActivity.class)));
        add(review, history, 12);
        add(screen, review, 12);

        gradeFilter.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedGrade = position;
                updateStatistics();
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) { }
        });

    }

    private TextView value() {
        return AppUi.text(this, "—", 32, AppUi.TEXT, true);
    }

    private LinearLayout metric(String label, TextView value, String caption, boolean featured) {
        LinearLayout card = AppUi.card(this);
        if (featured) card.setBackground(AppUi.background(this, AppUi.BLUE, 20));
        int primary = featured ? Color.WHITE : AppUi.TEXT;
        int secondary = featured ? Color.rgb(219, 233, 249) : AppUi.MUTED;
        add(card, AppUi.text(this, label, 14, featured ? Color.WHITE : AppUi.MUTED, false), 0);
        value.setTextColor(primary);
        add(card, value, 14);
        add(card, AppUi.text(this, caption, 12, secondary, false), 8);
        return card;
    }

    private void scoreRow(LinearLayout parent, String label, TextView value, boolean divider) {
        if (divider) {
            View line = new View(this);
            line.setBackgroundColor(AppUi.BORDER);
            LinearLayout.LayoutParams lineParams = new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT, dp(1));
            lineParams.setMargins(0, dp(12), 0, dp(12));
            parent.addView(line, lineParams);
        }
        LinearLayout row = new LinearLayout(this);
        boolean stacked = getResources().getConfiguration().fontScale > 1.2f;
        row.setOrientation(stacked ? LinearLayout.VERTICAL : LinearLayout.HORIZONTAL);
        row.setGravity(Gravity.CENTER_VERTICAL);
        TextView name = AppUi.text(this, label, 15, AppUi.MUTED, false);
        value.setTextSize(22);
        if (stacked) {
            row.addView(name);
            row.addView(value);
        } else {
            row.addView(name, new LinearLayout.LayoutParams(0,
                    ViewGroup.LayoutParams.WRAP_CONTENT, 1));
            row.addView(value);
        }
        add(parent, row, 0);
    }

    private void add(LinearLayout parent, View child, int top) {
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.topMargin = dp(top);
        parent.addView(child, params);
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
        try (QuizDbHelper database = new QuizDbHelper(this);
             Cursor result = database.getStatistics(selectedGrade)) {
            if (!result.moveToFirst()) return;
            int count = result.getInt(0);
            long correct = result.getLong(1);
            long total = result.getLong(4);
            double accuracy = total > 0 ? 100.0 * correct / total : 0;
            String percent = String.format(java.util.Locale.US, "%.1f%%", accuracy);
            attemptsValue.setText(String.valueOf(count));
            totalValue.setText(String.valueOf(correct));
            averageValue.setText(String.format(java.util.Locale.US, "%.1f", result.getDouble(2)));
            highestValue.setText(String.valueOf(result.getLong(3)));
            questionsValue.setText(String.valueOf(total));
            correctValue.setText(String.valueOf(correct));
            accuracyValue.setText(percent);
            accuracyProgress.setProgress((int) Math.round(accuracy * 10));
            accuracyProgress.setContentDescription((vi ? "Tỷ lệ trả lời đúng: " : "Accuracy: ") + percent);
            empty.setVisibility(count == 0 ? View.VISIBLE : View.GONE);
            empty.setText(selectedGrade == 0
                    ? (vi ? "Chưa có bài làm nào. Kết quả sẽ xuất hiện sau khi bạn hoàn thành bài luyện tập."
                            : "No attempts yet. Your results will appear after you complete a practice quiz.")
                    : (vi ? "Chưa có bài làm cho lớp " + selectedGrade + "."
                            : "No attempts for grade " + selectedGrade + "."));
            answerChart.setData(new String[]{vi ? "Đúng" : "Correct", vi ? "Sai" : "Incorrect"},
                    new float[]{correct, Math.max(0, total - correct)},
                    new int[]{AppUi.GREEN, Color.rgb(164, 78, 37)});
            int first = selectedGrade == 0 ? 1 : selectedGrade;
            int last = selectedGrade == 0 ? 12 : selectedGrade;
            String[] labels = new String[last - first + 1];
            float[] answered = new float[labels.length];
            int[] colors = new int[labels.length];
            for (int grade = first; grade <= last; grade++) {
                int index = grade - first;
                labels[index] = String.valueOf(grade);
                colors[index] = Color.rgb(16, 155, 144);
                try (Cursor gradeResult = database.getStatistics(grade)) {
                    if (gradeResult.moveToFirst()) answered[index] = gradeResult.getLong(4);
                }
            }
            gradeChart.setData(labels, answered, colors);
        }
    }

    private int dp(int value) {
        return AppUi.dp(this, value);
    }
}
