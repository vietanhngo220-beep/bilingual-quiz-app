package com.vietanh.bilingualquiz.ui;
import android.app.Activity; import android.os.Bundle; import android.graphics.Color; import android.view.Gravity; import android.widget.*;
public class StatisticsActivity extends Activity { public void onCreate(Bundle b){super.onCreate(b); TextView t=new TextView(this); t.setText("Thống kê / Statistics\n\nChưa có dữ liệu.\nCác thành viên sẽ nối lịch sử điểm và biểu đồ tại đây."); t.setTextSize(20); t.setTextColor(Color.DKGRAY); t.setGravity(Gravity.CENTER); t.setPadding(32,32,32,32); setContentView(t);} }
