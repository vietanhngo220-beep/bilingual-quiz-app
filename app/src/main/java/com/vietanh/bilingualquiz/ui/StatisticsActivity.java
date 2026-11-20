package com.vietanh.bilingualquiz.ui;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.vietanh.bilingualquiz.LanguageSettings;
import com.vietanh.bilingualquiz.data.QuizDbHelper;

/** Statistics layout. Data and navigation keep the existing main-branch behavior. */
public class StatisticsActivity extends Activity {
    private boolean vi;
    private TextView attemptsValue;
    private TextView totalValue;
    private TextView averageValue;
    private TextView highestValue;
    private TextView empty;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        vi = LanguageSettings.isVietnamese(this);
        LinearLayout screen = AppUi.screen(this,
                vi ? "Thống kê kết quả" : "Statistics", true);
        TextView subtitle = AppUi.text(this,
                vi ? "Tổng quan các lượt làm bài của bạn" : "Your quiz results at a glance",
                15, AppUi.MUTED, false);
        add(screen, subtitle, 6);

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

        empty = AppUi.text(this, vi
                ? "Chưa có bài làm nào. Kết quả sẽ xuất hiện sau khi bạn hoàn thành bài luyện tập."
                : "No attempts yet. Your results will appear after you complete a practice quiz.",
                14, AppUi.MUTED, false);
        add(screen, empty, 16);

        LinearLayout barCard = AppUi.card(this);
        add(barCard, AppUi.text(this, vi ? "Kết quả theo lớp" : "Results by grade",
                18, AppUi.TEXT, true), 0);
        add(barCard, AppUi.text(this, vi ? "Số câu đúng và sai ở từng lớp"
                : "Correct and incorrect answers by grade", 13, AppUi.MUTED, false), 4);
        StatisticsChartView gradeChart = new StatisticsChartView(this, false, vi);
        add(barCard, gradeChart, 16);
        add(screen, barCard, 20);

        LinearLayout donutCard = AppUi.card(this);
        add(donutCard, AppUi.text(this, vi ? "Tỷ lệ trả lời" : "Answer breakdown",
                18, AppUi.TEXT, true), 0);
        StatisticsChartView answerChart = new StatisticsChartView(this, true, vi);
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
        add(screen, review, 12);

        Button refresh = AppUi.button(this,
                vi ? "Cập nhật thống kê" : "Refresh statistics", false);
        refresh.setOnClickListener(view -> updateStatistics());
        add(screen, refresh, 16);

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

    private void updateStatistics() {
        try (QuizDbHelper database = new QuizDbHelper(this)) {
            int count = database.getAttemptCount();
            attemptsValue.setText(String.valueOf(count));
            totalValue.setText(String.valueOf(database.getTotalScore()));
            averageValue.setText(String.format(java.util.Locale.US, "%.1f",
                    database.getAverageScore()));
            highestValue.setText(String.valueOf(database.getHighestScore()));
            empty.setVisibility(count == 0 ? View.VISIBLE : View.GONE);
        }
    }

    private int dp(int value) {
        return AppUi.dp(this, value);
    }
}
