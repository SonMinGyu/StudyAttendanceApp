package com.application.studyattendance;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import fragment.searchStudyFragment;
import fragment.setupFragment;
import fragment.studyFragment;

public class MyStudyActivity extends AppCompatActivity {

    TextView menuText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_study);

        menuText = (TextView) findViewById(R.id.menuText);

        menuText.setText("내 스터디");
        getSupportFragmentManager().beginTransaction().replace(R.id.mystudyFramLayout, new studyFragment()).commit();

        FloatingActionButton timerBUtton = (FloatingActionButton) findViewById(R.id.my_study_timerButton);
        timerBUtton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MyStudyActivity.this, TimerActivity.class);
                MyStudyActivity.this.startActivity(intent);

            }
        });

        FloatingActionButton createStudyButton = (FloatingActionButton) findViewById(R.id.createStudyButton);
        createStudyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MyStudyActivity.this, createStudyActivity.class);
                MyStudyActivity.this.startActivity(intent);
            }
        });

        BottomNavigationView bottomNavigationView = (BottomNavigationView) findViewById(R.id.mystudyNavigationView);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch (menuItem.getItemId())
                {
                    case R.id.action_mystudy:
                        Fragment myStudyFragment = getSupportFragmentManager().findFragmentById(R.id.mystudyFramLayout);
                        if(myStudyFragment instanceof studyFragment) {
                            return true;
                        }
                        else {
                            getSupportFragmentManager().beginTransaction().replace(R.id.mystudyFramLayout, new studyFragment()).commit();
                            menuText.setText("내 스터디");
                            return true;
                        }

                    case R.id.action_searchstudy:
                        Fragment searchStudyFragment = getSupportFragmentManager().findFragmentById(R.id.mystudyFramLayout);
                        if(searchStudyFragment instanceof searchStudyFragment) {
                            return true;
                        }
                        else {
                            getSupportFragmentManager().beginTransaction().replace(R.id.mystudyFramLayout, new searchStudyFragment()).commit();
                            menuText.setText("스터디 검색");
                            return true;
                        }

                    case R.id.action_setup:
                        Fragment setupFragment = getSupportFragmentManager().findFragmentById(R.id.mystudyFramLayout);
                        if(setupFragment instanceof setupFragment) {
                            return true;
                        }
                        else {
                            getSupportFragmentManager().beginTransaction().replace(R.id.mystudyFramLayout, new setupFragment()).commit();
                            menuText.setText("설정");
                            return true;
                        }
                }

                return false;
            }
        });
    }
}