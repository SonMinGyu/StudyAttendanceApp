package com.application.studyattendance;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import com.application.studyattendance.model.StudyModel;
import com.application.studyattendance.model.StudyUsers;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

public class StudyPropertisActivity extends Activity {


    private static final String TAG = "mainmainmain";
    private List<StudyModel> studyModels = new ArrayList<>();
    private List<StudyUsers> studyUsers = new ArrayList<>();
    Boolean isMem = false;
    Button joinButton;
    TextView nameText;
    TextView categoryText;
    TextView numberText;
    TextView numberText2;
    CheckBox onCheck;
    CheckBox offCheck;
    CheckBox openCheck;
    CheckBox notOpenCheck;
    CheckBox selfCheck;
    CheckBox fixedCheck;
    CheckBox pushCheck;
    TextView missionText;
    TextView mission;
    TextView area;
    ConstraintLayout areaCons;
    int maximum;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_study_propertis);


        DisplayMetrics dm = getApplicationContext().getResources().getDisplayMetrics();
        int width = (int) (dm.widthPixels * 0.8); // Display 사이즈의 80%
        int height = (int) (dm.heightPixels * 0.8); // Display 사이즈의 80%
        getWindow().getAttributes().width = width;
        getWindow().getAttributes().height = height;

        nameText = (TextView) findViewById(R.id.study_properties_studyName);
        categoryText = (TextView) findViewById(R.id.study_properties_studyCategory);
        numberText = (TextView) findViewById(R.id.study_properties_number);
        numberText2 = (TextView) findViewById(R.id.study_properties_number2);
        offCheck = (CheckBox) findViewById(R.id.study_properties_offStudyCheckBox);
        onCheck = (CheckBox) findViewById(R.id.study_properties_onStudyCheckBox);
        openCheck = (CheckBox) findViewById(R.id.study_properties_openStudyCheckBox);
        notOpenCheck = (CheckBox) findViewById(R.id.study_properties_notOpenStudyCheckBox);
        selfCheck = (CheckBox) findViewById(R.id.study_properties_selfStudyCheckBox);
        fixedCheck = (CheckBox) findViewById(R.id.study_properties_fixedStudyCheckBox);
        pushCheck = (CheckBox) findViewById(R.id.study_properties_pushCheckBox);
        missionText = (TextView) findViewById(R.id.missionText);
        mission = (TextView) findViewById(R.id.study_properties_mission);
        area = (TextView) findViewById(R.id.study_properties_area);
        areaCons = (ConstraintLayout) findViewById(R.id.area_constraintlayout);

        final StudyModel thisStudyModel = new StudyModel();
        // properties 창에 방에대한 정보 보여주기위해 firebase에서 방의 정보를 가져옴
        Intent intent = getIntent();
        FirebaseDatabase.getInstance().getReference().child("allStudy").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                studyModels.clear();
                for(DataSnapshot item :dataSnapshot.getChildren())
                {
                    studyModels.add(item.getValue(StudyModel.class));
                    if(item.getValue(StudyModel.class).getStudyKey().equals(getIntent().getExtras().getString("studykey")))
                    {
                        cat(thisStudyModel, item.getValue(StudyModel.class));
                    }
                }
                nameText.setText(thisStudyModel.getStudyName());
                categoryText.setText(thisStudyModel.getStudyCategory());
                numberText2.setText(Integer.toString(thisStudyModel.getStudyTotalNumber()));
                if(thisStudyModel.getOffOrOn() == 0)
                {
                    areaCons.setVisibility(View.VISIBLE);
                    area.setText(thisStudyModel.getPlace_area());
                    offCheck.setChecked(true);
                    onCheck.setChecked(false);
                }
                else if(thisStudyModel.getOffOrOn() == 1)
                {
                    areaCons.setVisibility(View.GONE);
                    offCheck.setChecked(false);
                    onCheck.setChecked(true);
                }
                else
                {
                    areaCons.setVisibility(View.VISIBLE);
                    area.setText(thisStudyModel.getPlace_area());
                    offCheck.setChecked(true);
                    onCheck.setChecked(true);
                }

                if(thisStudyModel.getIsNotOpenOrOpen())
                {
                    notOpenCheck.setChecked(true);
                    openCheck.setChecked(false);
                }
                else
                {
                    notOpenCheck.setChecked(false);
                    openCheck.setChecked(true);
                }

                if(thisStudyModel.getIsSelfOrFixed())
                {
                    selfCheck.setChecked(true);
                    fixedCheck.setChecked(false);
                }
                else
                {
                    selfCheck.setChecked(false);
                    fixedCheck.setChecked(true);
                }

                if(thisStudyModel.getIsPush())
                {
                    pushCheck.setChecked(true);
                }

                if(thisStudyModel.getIsMission())
                {
                    mission.setText(thisStudyModel.getMissionText());
                }
                else
                {
                    mission.setVisibility(View.GONE);
                    missionText.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        // 중복가입을 막기위한 스터디방의 유저정보 가져오기
        FirebaseDatabase.getInstance().getReference().child("allStudy").child(getIntent().getExtras().getString("studykey"))
                .child("studyUsers").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                studyUsers.clear();
                for(DataSnapshot item :snapshot.getChildren())
                {
                    studyUsers.add(item.getValue(StudyUsers.class));
                }
                numberText.setText(Integer.toString(studyUsers.size()));
                maximum = studyUsers.size();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        joinButton = (Button) findViewById(R.id.study_properties_join);
        joinButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();

                for(int i = 0; i<studyUsers.size(); i++)
                {
                    //Log.d(TAG,studyUsers.get(i).user);
                    if(studyUsers.get(i).user.equals(uid))
                        {
                        isMem = true;
                    }
                }

                if(!(isMem) && !(thisStudyModel.getStudyTotalNumber() <= studyUsers.size()))
                {
                    StudyUsers studyUsers = new StudyUsers();
                    studyUsers.user = uid;
                    //Log.d(TAG, getIntent().getExtras().getString("studykey"));
                    FirebaseDatabase.getInstance().getReference().child("study").child(uid).child(getIntent().getExtras()
                            .getString("studykey")).setValue(thisStudyModel);
                    FirebaseDatabase.getInstance().getReference().child("allStudy").child(getIntent().getExtras()
                            .getString("studykey")).child("studyUsers").push().setValue(studyUsers);
                    Toast.makeText(getApplicationContext(), "스터디에 가입되셨습니다.", Toast.LENGTH_SHORT).show();
                    finish();
                }
                else if(isMem)
                {
                    Toast.makeText(getApplicationContext(), "이미 가입한 스터디 입니다!", Toast.LENGTH_SHORT).show();
                }
                else if(thisStudyModel.getStudyTotalNumber() <= studyUsers.size())
                {
                    Toast.makeText(getApplicationContext(), "아쉽지만 스터디 정원이 다 찼습니다.", Toast.LENGTH_SHORT).show();
                }

            }
        });
    }

    @Override  //바깥레이어 클릭시 안닫히게
    public boolean onTouchEvent(MotionEvent event) {
        if(event.getAction()==MotionEvent.ACTION_OUTSIDE){
            return false;
        }
        return true;
    }

    // 가입한 스터디를 나의 스터디에도 추가
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
    }
}