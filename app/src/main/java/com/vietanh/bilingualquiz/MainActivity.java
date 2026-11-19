package com.vietanh.bilingualquiz;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

public class MainActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        TextView title = new TextView(this);
        title.setText("Bilingual Quiz\n\nÔn tập trắc nghiệm song ngữ");
        title.setTextSize(24);
        title.setPadding(48, 96, 48, 48);
        setContentView(title);
    }
}
