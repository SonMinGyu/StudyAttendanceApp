package com.application.studyattendance;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import fragment.countdownFragment;
import fragment.studyFragment;
import fragment.timerFragment;

public class TimerActivity extends AppCompatActivity {

    Button timer_button;
    Button countdown_button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timer);

        timer_button = (Button) findViewById(R.id.timer_button);
        countdown_button = (Button) findViewById(R.id.countdown_button);

        getSupportFragmentManager().beginTransaction().replace(R.id.timer_framelayout, new countdownFragment()).commit();

        timer_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Fragment myStudyFragment = getSupportFragmentManager().findFragmentById(R.id.timer_framelayout);
                if(myStudyFragment instanceof timerFragment) {
                }
                else {
                    getSupportFragmentManager().beginTransaction().replace(R.id.timer_framelayout, new timerFragment()).commit();
                }
            }
        });

        countdown_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Fragment myStudyFragment = getSupportFragmentManager().findFragmentById(R.id.timer_framelayout);
                if(myStudyFragment instanceof countdownFragment) {
                }
                else {
                    getSupportFragmentManager().beginTransaction().replace(R.id.timer_framelayout, new countdownFragment()).commit();
                }
            }
        });

    }
}