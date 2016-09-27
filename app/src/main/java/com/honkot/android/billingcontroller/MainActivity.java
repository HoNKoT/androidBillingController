package com.honkot.android.billingcontroller;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import java.util.Calendar;

public class MainActivity extends AppCompatActivity {

    private Textlog mLog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mLog = new Textlog((TextView)findViewById(R.id.log));
    }

    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.button1:
                break;
            case R.id.button2:
                break;
            case R.id.button3:
                break;
        }
    }

    private class Textlog {
        TextView mLogView;
        Calendar mCal;

        Textlog(TextView tv) {
            mCal = Calendar.getInstance();
            mLogView = tv;
        }

        public void print(String log) {
            mCal.setTimeInMillis(System.currentTimeMillis());
            StringBuffer buf = new StringBuffer();
            buf.append(log);
            buf.append(System.getProperty("line.separator"));
            buf.append(mLogView.getText());
            mLogView.setText(buf.toString());
        }
    }
}
