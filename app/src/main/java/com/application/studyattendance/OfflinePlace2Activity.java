package com.application.studyattendance;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

import static com.application.studyattendance.createStudyActivity.offStudyCheckBox;

public class OfflinePlace2Activity extends Activity {

    private static final String TAG = "mainmainmain";

    Button kakaoButton;
    Button naverButton;
    Button googleButton;
    ConstraintLayout constraintLayout;
    Button cancel_button;
    Button ok_button;
    EditText placeName;
    EditText placeAddress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_offline_place2);

        kakaoButton = (Button) findViewById(R.id.kakao_map_button2);
        naverButton = (Button) findViewById(R.id.naver_map_button2);
        googleButton = (Button) findViewById(R.id.google_map_button2);
        constraintLayout = (ConstraintLayout) findViewById(R.id.offline_place2_set_place_layout);
        cancel_button = (Button) findViewById(R.id.cancel_button2);
        ok_button = (Button) findViewById(R.id.ok_button2);
        placeAddress = (EditText) findViewById(R.id.offline_place2_place_address);
        placeName = (EditText) findViewById(R.id.offline_place2_place_name);

        Intent intent = getIntent();

        kakaoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String url = "kakaomap://open?page=placeSearch";

                try {
                    //에러가 발생할 수 있는 코드
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                    startActivity(intent);
                    //throw new Exception(); //강제 에러 출력
                } catch (Exception e) {
                    //에러시 수행
                    Toast.makeText(getApplicationContext(), "Kakao map이 다운되어 있지 않습니다.", Toast.LENGTH_SHORT).show();
                    e.printStackTrace(); //오류 출력(방법은 여러가지)
                }

            }
        });

        naverButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String url = "nmap://map?&appname=com.example.myapp";

                try {
                    //에러가 발생할 수 있는 코드
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                    startActivity(intent);
                    //throw new Exception(); //강제 에러 출력
                } catch (Exception e) {
                    //에러시 수행
                    Toast.makeText(getApplicationContext(), "Naver map이 다운되어 있지 않습니다.", Toast.LENGTH_SHORT).show();
                    e.printStackTrace(); //오류 출력(방법은 여러가지)
                }
            }
        });

        googleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String url = "https://www.google.com/maps/search/?api=1&parameters";

                try {
                    //에러가 발생할 수 있는 코드
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                    startActivity(intent);
                    //throw new Exception(); //강제 에러 출력
                } catch (Exception e) {
                    //에러시 수행
                    Toast.makeText(getApplicationContext(), "Google map이 다운되어 있지 않습니다.", Toast.LENGTH_SHORT).show();
                    e.printStackTrace(); //오류 출력(방법은 여러가지)
                }
            }
        });

        cancel_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        ok_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (placeName.getText().toString().length() <= 0) {
                    Toast.makeText(getApplicationContext(), "스터디 장소 이름을 입력해주세요!", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (placeAddress.getText().toString().length() <= 0) {
                    Toast.makeText(getApplicationContext(), "지도 어플을 사용하여 스터디 장소 주소를 입력해주세요!", Toast.LENGTH_SHORT).show();
                    return;
                }

                // firebase에 정보업데이트
                Map<String, Object> taskMap = new HashMap<String, Object>();
                taskMap.put("place_name", placeName.getText().toString());
                taskMap.put("place_address", placeAddress.getText().toString());
                taskMap.put("place_nowOrLater", true);
                FirebaseDatabase.getInstance().getReference().child("allStudy").child(getIntent().getExtras().getString("studyroomkey"))
                        .updateChildren(taskMap);


                finish();
            }
        });
    }

    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_OUTSIDE) {
            return false;
        }
        return true;
    }
}