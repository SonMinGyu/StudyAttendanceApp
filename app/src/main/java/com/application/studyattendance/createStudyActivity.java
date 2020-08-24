package com.application.studyattendance;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.application.studyattendance.MainActivity;
import com.application.studyattendance.R;
import com.application.studyattendance.RegisterActivity;
import com.application.studyattendance.model.StudyModel;
import com.application.studyattendance.model.StudyUsers;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import fragment.studyFragment;

import static com.application.studyattendance.OfflinePlaceActivity.place_now_or_later;
import static com.application.studyattendance.OfflinePlaceActivity.placeaddress;
import static com.application.studyattendance.OfflinePlaceActivity.placearea;
import static com.application.studyattendance.OfflinePlaceActivity.placename;

public class createStudyActivity extends Activity {

    private static final String TAG = "mainmainmain";
    ArrayAdapter<String> arrayAdapter;
    ArrayList<String> arrayList;
    int totalnumber = 0;
    int positionNumber = -1;
    String stTotalnumber;
    String split;
    boolean userWrite = false;
    String getMission;
    String writeNumber;
    String studyHostUid;

    int offOrOn = -1; // offOrOn이 -1이면 둘다 체크 X, 0이면 오프라인, 1이면 온라인, 둘다체크하면 온오프겸용
    boolean isNotOpenStudyChecked = false;
    boolean isOpenStudyChecked = false;
    boolean isSelfStudyChecked = false;
    boolean isFixedStudyChecked = false;
    boolean isMissionChecked = false;
    boolean isPushChecked = false;

    public static CheckBox offStudyCheckBox ;

    FirebaseAuth firebaseAuth;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_study);
        //requestWindowFeature(Window.FEATURE_NO_TITLE);

        final EditText writeNumber = (EditText)findViewById(R.id.writeNumber);
        Spinner spinner = (Spinner)findViewById(R.id.total_number_spinner);
        //spinner.setAdapter(arrayAdapter);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if(adapterView.getSelectedItemPosition() == 0)
                {
                    positionNumber = 0;
                    userWrite = false;
                }
                else if(adapterView.getSelectedItemPosition() == 12)
                {
                    positionNumber = 12;
                    userWrite = true;
                    /*
                    stTotalnumber = writeNumber.getText().toString();
                    totalnumber = Integer.parseInt(stTotalnumber);
                    /*
                     */
                    //Toast.makeText(getContext(), totalnumber, Toast.LENGTH_SHORT).show();
                }
                else {
                    userWrite = false;
                    positionNumber = adapterView.getSelectedItemPosition();
                    stTotalnumber = (String) adapterView.getSelectedItem();
                    split = stTotalnumber.substring(0, stTotalnumber.length() - 1);
                    totalnumber = Integer.parseInt(split);
                    //Toast.makeText(getContext(), totalnumber, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        offStudyCheckBox = (CheckBox)findViewById(R.id.offStudyCheckBox);
        final CheckBox onStudyCheckBox = (CheckBox)findViewById(R.id.onStudyCheckBox);
        final CheckBox notOpenStudyCheckBox = (CheckBox)findViewById(R.id.notOpenStudyCheckBox);
        final CheckBox openStudyCheckBox = (CheckBox)findViewById(R.id.openStudyCheckBox);
        final CheckBox selfStudyCheckBox = (CheckBox)findViewById(R.id.selfStudyCheckBox);
        final CheckBox fixedStudyCheckBox = (CheckBox)findViewById(R.id.fixedStudyCheckBox);
        final CheckBox missionCheckBox = (CheckBox)findViewById(R.id.missionCheckBox);
        final CheckBox pushCheckBox = (CheckBox)findViewById(R.id.pushCheckBox);

        offStudyCheckBox.setOnCheckedChangeListener( new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(offStudyCheckBox.isChecked() && !onStudyCheckBox.isChecked()) // 오프라인, 온라인 스터디 체크박스(중복가능)
                {
                    offOrOn = 0;
                    //Toast.makeText(getContext(), offOrOn, Toast.LENGTH_SHORT).show();
                    Log.d(TAG, "0");
                }
                else if(!offStudyCheckBox.isChecked() && onStudyCheckBox.isChecked())
                {
                    offOrOn = 1;
                    //Toast.makeText(getContext(), offOrOn, Toast.LENGTH_SHORT).show();
                    Log.d(TAG, "1");
                }
                else if(offStudyCheckBox.isChecked() && onStudyCheckBox.isChecked())
                {
                    offOrOn = 2;
                    //Toast.makeText(getContext(), offOrOn, Toast.LENGTH_SHORT).show();
                    Log.d(TAG, "2");
                }
                else
                {

                }

                if(offStudyCheckBox.isChecked())
                {
                    Intent intent = new Intent(createStudyActivity.this, OfflinePlaceActivity.class);
                    createStudyActivity.this.startActivity(intent);
                }
            }
        });

        onStudyCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(offStudyCheckBox.isChecked() && !onStudyCheckBox.isChecked()) // 오프라인, 온라인 스터디 체크박스(중복가능)
                {
                    offOrOn = 0;
                    Log.d(TAG, "0");
                }
                else if(!offStudyCheckBox.isChecked() && onStudyCheckBox.isChecked())
                {
                    offOrOn = 1;
                    Log.d(TAG, "1");
                }
                else if(offStudyCheckBox.isChecked() && onStudyCheckBox.isChecked())
                {
                    offOrOn = 2;
                    Log.d(TAG, "2");
                }
                else
                {
                    offOrOn = -1;
                }
            }
        });

        notOpenStudyCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(notOpenStudyCheckBox.isChecked()) // 공개, 비공개 스터디 체크박스(중복불가)
                {
                    isNotOpenStudyChecked = true;
                    if(openStudyCheckBox.isChecked())
                    {
                        openStudyCheckBox.setChecked(false);
                        isOpenStudyChecked = false;
                    }
                }
                if(openStudyCheckBox.isChecked())
                {
                    isOpenStudyChecked = true;
                    if(notOpenStudyCheckBox.isChecked())
                    {
                        notOpenStudyCheckBox.setChecked(false);
                        isNotOpenStudyChecked = false;
                    }
                }
            }
        });

        openStudyCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(openStudyCheckBox.isChecked())
                {
                    isOpenStudyChecked = true;
                    if(notOpenStudyCheckBox.isChecked())
                    {
                        notOpenStudyCheckBox.setChecked(false);
                        isNotOpenStudyChecked = false;
                    }
                }
                if(notOpenStudyCheckBox.isChecked()) // 공개, 비공개 스터디 체크박스(중복불가)
                {
                    isNotOpenStudyChecked = true;
                    if(openStudyCheckBox.isChecked())
                    {
                        openStudyCheckBox.setChecked(false);
                        isOpenStudyChecked = false;
                    }
                }
            }
        });

        selfStudyCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(selfStudyCheckBox.isChecked()) // 자율, 고정스터디 체크박스(중복불가)
                {
                    isSelfStudyChecked = true;
                    if(fixedStudyCheckBox.isChecked())
                    {
                        fixedStudyCheckBox.setChecked(false);
                        isFixedStudyChecked = false;
                    }
                }
                if(fixedStudyCheckBox.isChecked())
                {
                    isFixedStudyChecked = true;
                    if(selfStudyCheckBox.isChecked())
                    {
                        selfStudyCheckBox.setChecked(false);
                        isSelfStudyChecked = false;
                    }
                }
            }
        });

        fixedStudyCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(fixedStudyCheckBox.isChecked())
                {
                    isFixedStudyChecked = true;
                    if(selfStudyCheckBox.isChecked())
                    {
                        selfStudyCheckBox.setChecked(false);
                        isSelfStudyChecked = false;
                    }
                }
                if(selfStudyCheckBox.isChecked()) // 자율, 고정스터디 체크박스(중복불가)
                {
                    isSelfStudyChecked = true;
                    if(fixedStudyCheckBox.isChecked())
                    {
                        fixedStudyCheckBox.setChecked(false);
                        isFixedStudyChecked = false;
                    }
                }
            }
        });

        missionCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(missionCheckBox.isChecked())
                {
                    final LinearLayout linear = (LinearLayout) View.inflate(createStudyActivity.this, R.layout.activity_mission, null);
                    new AlertDialog.Builder(createStudyActivity.this)
                            .setView(linear)
                            .setCancelable(false)
                            .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int whichButton) {
                                    EditText mission = (EditText) linear.findViewById(R.id.mission_missionText);
                                    if(mission.getText().toString().getBytes().length <= 0)
                                    {
                                        //Log.d(TAG, "입력안한부분 실행");
                                        Toast.makeText(getApplicationContext(), "미션을 입력하지 않으셨습니다!", Toast.LENGTH_SHORT).show();
                                        missionCheckBox.setChecked(false);
                                        dialog.dismiss();
                                    }
                                    else {
                                        getMission = mission.getText().toString();
                                        //Log.d(TAG, "입력한부분 실행");
                                        missionCheckBox.setChecked(true);
                                        isMissionChecked = true;
                                        dialog.dismiss();
                                    }
                                }
                            })
                            .setNegativeButton("취소", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int whichButton) {
                                    missionCheckBox.setChecked(false);
                                    dialog.dismiss();
                                }
                            })
                            .show();
                }
            }
        });

        pushCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(pushCheckBox.isChecked())
                {
                    isPushChecked = true;
                }
            }
        });

        final EditText studyName = (EditText)findViewById(R.id.studyName);
        final EditText studyCategory = (EditText)findViewById(R.id.studyCategory);
        Button button = (Button) findViewById(R.id.createStudyButton);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(studyName.getText().toString().equals(""))
                {
                    Toast.makeText(getApplicationContext(), "스터디 이름을 입력하세요.", Toast.LENGTH_SHORT).show();
                    return;
                }
                if(studyCategory.getText().toString().equals(""))
                {
                    Toast.makeText(getApplicationContext(), "스터디 카테고리를 입력하세요.", Toast.LENGTH_SHORT).show();
                    return;
                }

                if(positionNumber == 0)
                {
                    Toast.makeText(getApplicationContext(), "스터디 인원수를 선택하세요.", Toast.LENGTH_SHORT).show();
                    return;
                }
                if(positionNumber == 12)
                {
                    if(writeNumber.getText().toString().equals(""))
                    {
                        Toast.makeText(getApplicationContext(), "스터디 인원수를 입력하세요.", Toast.LENGTH_SHORT).show();
                        return;
                    }
                }

                if(offOrOn == -1)
                {
                    Toast.makeText(getApplicationContext(), "온, 오프 체크박스를 체크해주세요.", Toast.LENGTH_SHORT).show();
                    return;
                }
                if(!notOpenStudyCheckBox.isChecked() && !openStudyCheckBox.isChecked())
                {
                    Toast.makeText(getApplicationContext(), "공개, 비공개 체크박스를 체크해주세요.", Toast.LENGTH_SHORT).show();
                    return;
                }
                if(!selfStudyCheckBox.isChecked() && !fixedStudyCheckBox.isChecked())
                {
                    Toast.makeText(getApplicationContext(), "자율 , 고정 체크박스를 체크해주세요.", Toast.LENGTH_SHORT).show();
                    return;
                }
                String studyHostUid = firebaseAuth.getInstance().getCurrentUser().getUid();
                StudyModel studyModel = new StudyModel();
                studyModel.studyHostUid = firebaseAuth.getInstance().getCurrentUser().getUid();
                studyModel.studyName = studyName.getText().toString();
                studyModel.studyCategory = studyCategory.getText().toString();
                if(!userWrite) {
                    studyModel.studyTotalNumber = totalnumber;
                }
                else
                {
                    stTotalnumber = writeNumber.getText().toString();
                    totalnumber = Integer.parseInt(stTotalnumber);
                    studyModel.studyTotalNumber = totalnumber;
                }
                studyModel.offOrOn = offOrOn;
                if(notOpenStudyCheckBox.isChecked()) // 비공개 스터디체크 했으면 studyModel의 noOpenorOpen이 ture, 공개 체크했으면 false;
                {
                    studyModel.notOpenOrOpen = true;
                }
                else
                {
                    studyModel.notOpenOrOpen = false;
                }

                if(selfStudyCheckBox.isChecked())
                {
                    studyModel.selfOrFixed = true;
                }
                else
                {
                    studyModel.selfOrFixed = false;
                }

                studyModel.isMission = missionCheckBox.isChecked();
                if(missionCheckBox.isChecked())
                {
                    studyModel.missionText = getMission;
                }
                studyModel.isPush = pushCheckBox.isChecked();

                /*
                long now = System.currentTimeMillis();
                Date date = new Date(now);
                SimpleDateFormat mFormat = new SimpleDateFormat("yyyy/M/d");
                String time = mFormat.format(date);

                studyModel.createTime = time;
                 */

                studyModel.place_area = placearea;

                studyModel.place_nowOrLater = place_now_or_later;
                if(studyModel.place_nowOrLater) {
                    studyModel.place_name = placename;
                    studyModel.place_address = placeaddress;
                }

                String stStudyName = studyName.getText().toString();
                String studyName_createTime = stStudyName + " " + getToDay();

                studyModel.studyKey = studyName_createTime;

                FirebaseDatabase.getInstance().getReference().child("study").child(studyHostUid).child(studyName_createTime).setValue(studyModel);
                FirebaseDatabase.getInstance().getReference().child("allStudy").child(studyName_createTime).setValue(studyModel);
                //Map<String, Object> taskMap = new HashMap<String, Object>();
                //taskMap.put("host", studyHostUid);
                StudyUsers studyUsers = new StudyUsers();
                studyUsers.user = studyHostUid;
                FirebaseDatabase.getInstance().getReference().child("allStudy").child(studyName_createTime).child("studyUsers").push().setValue(studyUsers);


                placearea = "";
                finish();
                /*
                Intent intent = new Intent(createStudyActivity.this, MyStudyActivity.class);
                createStudyActivity.this.startActivity(intent);
                finish();

                 */
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

    public static String getToDay()
    {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy+MM+dd+HH+mm+ss");
        return sdf.format(new Date());
    }


}
