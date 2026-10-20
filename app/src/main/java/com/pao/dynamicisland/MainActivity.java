package com.pao.dynamicisland;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.Gravity;
import android.widget.LinearLayout;
import android.widget.TextView;

public final class MainActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setGravity(Gravity.CENTER);
        root.setPadding(dp(24), dp(24), dp(24), dp(24));
        root.setBackgroundColor(Color.rgb(12, 14, 19));

        TextView title = new TextView(this);
        title.setText("Dynamic Island");
        title.setTextColor(Color.WHITE);
        title.setTextSize(28);
        title.setTypeface(Typeface.DEFAULT, Typeface.BOLD);
        title.setGravity(Gravity.CENTER);

        TextView status = new TextView(this);
        status.setText("Clean Android baseline is running.");
        status.setTextColor(Color.rgb(185, 192, 205));
        status.setTextSize(16);
        status.setGravity(Gravity.CENTER);
        status.setPadding(0, dp(12), 0, 0);

        TextView note = new TextView(this);
        note.setText("Next: implement the island overlay after startup is verified.");
        note.setTextColor(Color.rgb(130, 141, 160));
        note.setTextSize(14);
        note.setGravity(Gravity.CENTER);
        note.setPadding(0, dp(20), 0, 0);

        root.addView(title, new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        root.addView(status, new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        root.addView(note, new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));

        setContentView(root);
    }

    private int dp(int value) {
        return Math.round(value * getResources().getDisplayMetrics().density);
    }
}
