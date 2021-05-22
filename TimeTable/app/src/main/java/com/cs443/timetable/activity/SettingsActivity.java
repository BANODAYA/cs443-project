package com.cs443.timetable.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Switch;

import androidx.appcompat.app.AppCompatActivity;

import com.cs443.timetable.R;

public class SettingsActivity extends AppCompatActivity implements View.OnClickListener {
    private ImageView iv_back;
    private RadioGroup radioGroup;
    private RadioButton radioButton;
    private RadioButton radioButton2;
    private RadioButton radioButton3;
    private RadioButton radioButton4;
    private Switch subject;
    private Button btn_save;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

    }

    @Override
    public void onClick(View v) {
        if (v == radioButton) {
            // Handle clicks for radioButton
        } else if (v == radioButton2) {
            // Handle clicks for radioButton2
        } else if (v == radioButton3) {
            // Handle clicks for radioButton3
        } else if (v == radioButton4) {
            // Handle clicks for radioButton4
        } else if (v == btn_save || v == iv_back) {
            onBackPressed();
        }
    }


    private void findViews() {
        iv_back = (ImageView) findViewById(R.id.iv_back);
        radioGroup = (RadioGroup) findViewById(R.id.radioGroup);
        radioButton = (RadioButton) findViewById(R.id.radioButton);
        radioButton2 = (RadioButton) findViewById(R.id.radioButton2);
        radioButton3 = (RadioButton) findViewById(R.id.radioButton3);
        radioButton4 = (RadioButton) findViewById(R.id.radioButton4);
        subject = (Switch) findViewById(R.id.subject);
        btn_save = (Button) findViewById(R.id.btn_save);

        radioButton.setOnClickListener(this);
        radioButton2.setOnClickListener(this);
        radioButton3.setOnClickListener(this);
        radioButton4.setOnClickListener(this);
        btn_save.setOnClickListener(this);
        iv_back.setOnClickListener(this);
    }
}