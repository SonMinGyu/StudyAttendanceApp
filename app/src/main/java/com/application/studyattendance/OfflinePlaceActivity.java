package com.application.studyattendance;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

import static com.application.studyattendance.createStudyActivity.offStudyCheckBox;

public class OfflinePlaceActivity extends Activity {

    private static final String TAG = "mainmainmain";

    Button kakaoButton;
    Button naverButton;
    Button googleButton;
    CheckBox now;
    CheckBox later;
    ConstraintLayout constraintLayout;
    Button cancel_button;
    Button ok_button;
    EditText placeName;
    EditText placeAddress;
    EditText placeArea;

    public static String placearea;
    public static String placename;
    public static String placeaddress;
    public static boolean place_now_or_later;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_offline_place);

        kakaoButton = (Button) findViewById(R.id.kakao_map_button);
        naverButton = (Button) findViewById(R.id.naver_map_button);
        googleButton = (Button) findViewById(R.id.google_map_button);
        now = (CheckBox) findViewById(R.id.now_checkBox);
        later = (CheckBox) findViewById(R.id.later_checkBox);
        constraintLayout = (ConstraintLayout) findViewById(R.id.set_place_layout);
        cancel_button = (Button) findViewById(R.id.cancel_button);
        ok_button = (Button) findViewById(R.id.ok_button);
        placeAddress = (EditText) findViewById(R.id.offline_place_place_address);
        placeName = (EditText) findViewById(R.id.offline_place_place_name);
        placeArea = (EditText) findViewById(R.id.offline_place_place_area);

        now.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(now.isChecked())
                {
                    if(later.isChecked()) {
                        later.setChecked(false);
                    }
                    constraintLayout.setVisibility(View.VISIBLE);
                }
                if(!now.isChecked())
                {
                    constraintLayout.setVisibility(View.GONE);
                }
            }
        });

        later.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(later.isChecked())
                {
                    if(now.isChecked()) {
                        now.setChecked(false);
                        constraintLayout.setVisibility(View.GONE);
                    }
                }
            }
        });

        kakaoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String url = "kakaomap://open?page=placeSearch";

                try{
                    //에러가 발생할 수 있는 코드
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                    startActivity(intent);
                    throw new Exception(); //강제 에러 출력
                }catch (Exception e) {
                    //에러시 수행
                    Toast.makeText(getApplicationContext(),"Kakao map이 다운되어 있지 않습니다.", Toast.LENGTH_SHORT).show();
                    e.printStackTrace(); //오류 출력(방법은 여러가지)
                }

            }
        });

        naverButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String url = "nmap://map?&appname=com.example.myapp";

                try{
                    //에러가 발생할 수 있는 코드
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                    startActivity(intent);
                    throw new Exception(); //강제 에러 출력
                }catch (Exception e) {
                    //에러시 수행
                    Toast.makeText(getApplicationContext(),"Naver map이 다운되어 있지 않습니다.", Toast.LENGTH_SHORT).show();
                    e.printStackTrace(); //오류 출력(방법은 여러가지)
                }
            }
        });

        googleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String url = "https://www.google.com/maps/search/?api=1&parameters";

                try{
                    //에러가 발생할 수 있는 코드
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                    startActivity(intent);
                    throw new Exception(); //강제 에러 출력
                }catch (Exception e) {
                    //에러시 수행
                    Toast.makeText(getApplicationContext(),"Google map이 다운되어 있지 않습니다.", Toast.LENGTH_SHORT).show();
                    e.printStackTrace(); //오류 출력(방법은 여러가지)
                }
            }
        });

        cancel_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                offStudyCheckBox.setChecked(false);
                finish();
            }
        });

        ok_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(placeArea.getText().toString().length() <= 0)
                {
                    Toast.makeText(getApplicationContext(),"스터디 지역을 입력하세요!",Toast.LENGTH_SHORT).show();
                    return;
                }

                if(!(now.isChecked()) && !(later.isChecked()))
                {
                    Toast.makeText(getApplicationContext(),"체크박스를 체크하세요!",Toast.LENGTH_SHORT).show();
                }
                else if(now.isChecked())
                {
                    if(placeName.getText().toString().length() <= 0)
                    {
                        Toast.makeText(getApplicationContext(),"스터디 장소 이름을 입력해주세요!",Toast.LENGTH_SHORT).show();
                        return;
                    }

                    if(placeAddress.getText().toString().length() <= 0)
                    {
                        Toast.makeText(getApplicationContext(),"지도 어플을 사용하여 스터디 장소 주소를 입력해주세요!",Toast.LENGTH_SHORT).show();
                        return;
                    }

                    placearea = placeArea.getText().toString();
                    placename = placeName.getText().toString();
                    placeaddress = placeAddress.getText().toString();
                    place_now_or_later = true;

                    finish();
                }
                else
                {
                    place_now_or_later = false;
                    finish();
                }
            }
        });
    }

    public boolean onTouchEvent(MotionEvent event) {
        if(event.getAction()==MotionEvent.ACTION_OUTSIDE){
            return false;
        }
        return true;
    }

    public void onBackPressed() {
        //super.onBackPressed();
    }
}