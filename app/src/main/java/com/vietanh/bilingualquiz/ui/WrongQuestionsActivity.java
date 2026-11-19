package com.vietanh.bilingualquiz.ui;

import android.app.Activity;
import android.database.Cursor;
import android.os.Bundle;
import android.graphics.Color;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.vietanh.bilingualquiz.data.QuizDbHelper;

public class WrongQuestionsActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LinearLayout screen = new LinearLayout(this);
        screen.setOrientation(LinearLayout.VERTICAL);
        screen.setPadding(32, 40, 32, 32);

        TextView title = new TextView(this);
        title.setText("Câu hỏi sai / Wrong questions");
        title.setTextSize(24);
        title.setTextColor(Color.rgb(20, 60, 120));
        screen.addView(title);

        QuizDbHelper database = new QuizDbHelper(this);
        Cursor cursor = database.getWrongQuestions();
        int number = 1;
        while (cursor.moveToNext()) {
            TextView item = new TextView(this);
            item.setText("\n" + number + ". Lớp " + cursor.getInt(1) + "\n" +
                    cursor.getString(2) + "\n" + cursor.getString(3) +
                    "\nĐáp án đúng / Correct answer: " + cursor.getString(4));
            item.setTextSize(17);
            item.setTextColor(Color.DKGRAY);
            screen.addView(item);
            number++;
        }
        cursor.close();

        if (number == 1) {
            TextView empty = new TextView(this);
            empty.setText("\nChưa có câu sai được lưu.\nHãy hoàn thành một bài luyện tập trước.");
            empty.setTextSize(18);
            screen.addView(empty);
        }
        setContentView(screen);
    }
}
