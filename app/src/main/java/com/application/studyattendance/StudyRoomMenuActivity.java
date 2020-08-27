package com.application.studyattendance;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import com.application.studyattendance.model.StudyModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class StudyRoomMenuActivity extends Activity {

    private StudyModel menuStudyModel = new StudyModel();
    TextView addresText;
    Button modifyButton;
    ConstraintLayout addressConstraintLayout;
    Boolean isHost;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_study_room_menu);

        addresText = (TextView) findViewById(R.id.study_room_menu_modifyText);
        modifyButton = (Button) findViewById(R.id.study_room_menu_modifyButton);
        addressConstraintLayout = (ConstraintLayout) findViewById(R.id.constraintLayout20);

        this.getWindow().getAttributes().windowAnimations = R.style.MyDialog;

        WindowManager.LayoutParams wmlp = this.getWindow().getAttributes();
        wmlp.gravity = Gravity.TOP | Gravity.RIGHT;
        wmlp.x = 0;   //x position
        wmlp.y = 0;   //y position
        this.getWindow().setAttributes(wmlp);

        DisplayMetrics dm = getApplicationContext().getResources().getDisplayMetrics();
        int width = (int) (dm.widthPixels * 0.75); // Display 사이즈의 80%
        int height = (int) (dm.heightPixels * 0.99); // Display 사이즈의 99%
        getWindow().getAttributes().width = width;
        getWindow().getAttributes().height = height;

        Intent intent = getIntent();

        // 방정보를 가져오기위한 db사용
        FirebaseDatabase.getInstance().getReference().child("allStudy").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot item :dataSnapshot.getChildren())
                {
                    if(item.getValue(StudyModel.class).getStudyKey().equals(getIntent().getExtras().getString("study")))
                    {
                        cat(menuStudyModel, item.getValue(StudyModel.class));
                    }
                }

                if(menuStudyModel.getOffOrOn() == 0 || menuStudyModel.getOffOrOn() == 2)
                {
                    addressConstraintLayout.setVisibility(View.VISIBLE);
                    if(menuStudyModel.place_nowOrLater) {
                        addresText.setText(menuStudyModel.getPlace_name() + "\n" + menuStudyModel.getPlace_address());
                    }
                    else
                    {
                        addresText.setText("팀원들과 상의하여\n스터디 장소를 정해보세요!");
                    }
                }
                else
                {
                   addressConstraintLayout.setVisibility(View.GONE);
                }

                if(menuStudyModel.getStudyHostUid().equals(FirebaseAuth.getInstance().getCurrentUser().getUid()))
                {
                    isHost = true;
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        modifyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(isHost)
                {
                    Intent intent3 = new Intent(StudyRoomMenuActivity.this, OfflinePlace2Activity.class);
                    intent3.putExtra("studyroomkey", getIntent().getExtras().getString("study"));
                    StudyRoomMenuActivity.this.startActivity(intent3);
                }
            }
        });

    }

    public void cat(StudyModel studyModel1, StudyModel studyMode2)
    {
        studyModel1.setStudyHostUid(studyMode2.getStudyHostUid());
        studyModel1.setStudyName(studyMode2.getStudyName());
        studyModel1.setStudyCategory(studyMode2.getStudyCategory());
        studyModel1.setStudyTotalNumber(studyMode2.getStudyTotalNumber());
        studyModel1.setOffOrOn(studyMode2.getOffOrOn());
        studyModel1.setNotOpenOrOpen(studyMode2.getIsNotOpenOrOpen());
        studyModel1.setSelfOrFixed(studyMode2.getIsSelfOrFixed());
        studyModel1.setMission(studyMode2.getIsMission());
        studyModel1.setPush(studyMode2.getIsPush());
        studyModel1.setProfile(studyMode2.getProfile());
        studyModel1.setStudyKey(studyMode2.getStudyKey());
        studyModel1.setMissionText(studyMode2.getMissionText());
        studyModel1.setPlace_nowOrLater(studyMode2.isPlace_nowOrLater());
        studyModel1.setPlace_name(studyMode2.getPlace_name());
        studyModel1.setPlace_address(studyMode2.getPlace_address());
        studyModel1.setPlace_area(studyMode2.getPlace_area());
        studyModel1.setStudyroom_password(studyMode2.getStudyroom_password());
    }

    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_OUTSIDE) {
            return false;
        }
        return true;
    }
}