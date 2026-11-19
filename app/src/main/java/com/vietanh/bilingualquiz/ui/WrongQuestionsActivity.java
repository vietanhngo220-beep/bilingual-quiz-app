package com.vietanh.bilingualquiz.ui;

import android.app.Activity;
import android.database.Cursor;
import android.os.Bundle;
import android.graphics.Color;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.vietanh.bilingualquiz.data.QuizDbHelper;
import com.vietanh.bilingualquiz.LanguageSettings;

public class WrongQuestionsActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LinearLayout screen = new LinearLayout(this);
        boolean vi = LanguageSettings.isVietnamese(this);
        screen.setOrientation(LinearLayout.VERTICAL);
        screen.setPadding(32, 40, 32, 32);

        TextView title = new TextView(this);
        title.setText(vi ? "Câu hỏi sai" : "Wrong questions");
        title.setTextSize(24);
        title.setTextColor(Color.rgb(20, 60, 120));
        screen.addView(title);

        QuizDbHelper database = new QuizDbHelper(this);
        Cursor cursor = database.getWrongQuestions();
        int number = 1;
        while (cursor.moveToNext()) {
            TextView item = new TextView(this);
            item.setText("\n" + number + ". " + (vi ? "Lớp " : "Grade ") + cursor.getInt(1) + "\n" +
                    (vi ? cursor.getString(2) : cursor.getString(3)) +
                    "\n" + (vi ? "Đáp án đúng: " : "Correct answer: ") + cursor.getString(4));
            item.setTextSize(17);
            item.setTextColor(Color.DKGRAY);
            screen.addView(item);
            number++;
        }
        cursor.close();

        if (number == 1) {
            TextView empty = new TextView(this);
            empty.setText(vi ? "\nChưa có câu sai được lưu.\nHãy hoàn thành một bài luyện tập trước."
                    : "\nNo wrong questions saved.\nComplete a practice quiz first.");
            empty.setTextSize(18);
            screen.addView(empty);
        }
        setContentView(screen);
    }
}
