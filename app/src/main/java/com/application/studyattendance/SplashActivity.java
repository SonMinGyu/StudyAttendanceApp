package com.application.studyattendance;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings;

public class SplashActivity extends AppCompatActivity {

    private ConstraintLayout constraintLayout;
    private FirebaseRemoteConfig mFirebaseRemoteConfig;

    @RequiresApi(api = Build.VERSION_CODES.P)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //FirebaseApp.initializeApp(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        constraintLayout = (ConstraintLayout) findViewById(R.id.splashactivity_constrainlayout);

        mFirebaseRemoteConfig = FirebaseRemoteConfig.getInstance();
        /*
        FirebaseRemoteConfigSettings configSettings = new FirebaseRemoteConfigSettings.Builder()
                .setMinimumFetchIntervalInSeconds(3600)
                .build();
        mFirebaseRemoteConfig.setConfigSettingsAsync(configSettings);

         */
        mFirebaseRemoteConfig.setDefaultsAsync(R.xml.default_config);

        mFirebaseRemoteConfig.fetchAndActivate()
                .addOnCompleteListener(this, new OnCompleteListener<Boolean>() {
                    @Override
                    public void onComplete(@NonNull Task<Boolean> task) {
                        if (task.isSuccessful()) {
                            boolean updated = task.getResult();
                            //Log.d(TAG, "Config params updated: " + updated);
                            //Toast.makeText(SplashActivity.this, "Fetch and activate succeeded",
                            //        Toast.LENGTH_SHORT).show();


                        } else {
                            //Toast.makeText(SplashActivity.this, "Fetch failed",
                            //        Toast.LENGTH_SHORT).show();
                        }

                        String splash_background = mFirebaseRemoteConfig.getString("splash_background");
                        constraintLayout.setBackgroundColor(Color.parseColor(splash_background));

                        boolean caps2 = mFirebaseRemoteConfig.getBoolean("splash_message_caps");

                        if(!caps2) // firebase remote config이용을 위한 if문
                        {
                            // 1초동안 splash화면 유지를 위한 핸들러
                            Handler hd = new Handler();
                            hd.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    startActivity(new Intent(getApplication(), MainActivity.class));
                                    SplashActivity.this.finish();
                                }
                            }, 1000);
                        }
                        else
                        {
                            displayMessage();
                        }

                        //displayMessage();
                    }
                });
    }
    void displayMessage()
    {
        String splash_message = mFirebaseRemoteConfig.getString("splash_message");
        boolean caps = mFirebaseRemoteConfig.getBoolean("splash_message_caps");

        if(caps)
        {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage(splash_message).setPositiveButton("확인", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    finish(); // 서버 점검중입니다 메시지를 띄우고 확인 클릭시 종료
                }
            });

            builder.create().show();
        }
    }

}