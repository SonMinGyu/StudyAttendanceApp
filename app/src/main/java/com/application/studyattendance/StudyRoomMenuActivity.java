package com.application.studyattendance;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.application.studyattendance.model.AttendanceModel;
import com.application.studyattendance.model.FineModel;
import com.application.studyattendance.model.StudyModel;
import com.application.studyattendance.model.StudyUsers;
import com.application.studyattendance.model.UserModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import org.w3c.dom.Text;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class StudyRoomMenuActivity extends Activity {

    private StudyModel menuStudyModel = new StudyModel();
    TextView addresText;
    Button modifyButton;
    ConstraintLayout addressConstraintLayout;
    Boolean isHost;
    ConstraintLayout missionConst;
    TextView missionText;
    TextView rewordText;
    CheckBox fineCheckBox;
    TextView menuFineTitle;
    TextView menuFine;
    ConstraintLayout fineConst;
    Boolean checkUseFine;
    List<UserModel> userModels = new ArrayList<>();
    List<StudyUsers> studyUsers2 = new ArrayList<>();
    int bringFineUnit;
    String giveUid;

    private RecyclerView usersRecyclerView;
    private RecyclerView fineRecyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_study_room_menu);

        addresText = (TextView) findViewById(R.id.study_room_menu_modifyText);
        modifyButton = (Button) findViewById(R.id.study_room_menu_modifyButton);
        addressConstraintLayout = (ConstraintLayout) findViewById(R.id.constraintLayout20);
        missionConst = (ConstraintLayout) findViewById(R.id.study_room_menu_mission_cons);
        missionText = (TextView) findViewById(R.id.study_room_menu_missionText2);
        rewordText = (TextView) findViewById(R.id.study_room_menu_missionReword2);
        fineCheckBox = (CheckBox) findViewById(R.id.study_room_menu_fineCheckBox);
        menuFineTitle = (TextView) findViewById(R.id.study_room_menu_fineTitle);
        menuFine = (TextView) findViewById(R.id.study_room_menu_fine);
        fineConst = (ConstraintLayout) findViewById(R.id.study_room_menu_fine_const);

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

        usersRecyclerView = (RecyclerView) findViewById(R.id.study_room_menu_member_recyclerView);
        usersRecyclerView.setAdapter(new UsersRecyclerViewAdapter());
        usersRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        fineRecyclerView = (RecyclerView) findViewById(R.id.study_room_menu_fine_recyclerView);
        fineRecyclerView.setAdapter(new FineRecyclerViewAdapter());
        fineRecyclerView.setLayoutManager(new LinearLayoutManager(this));


        FirebaseDatabase.getInstance().getReference().child("allStudy").child(getIntent().getExtras().getString("study"))
                .child("studyUsers").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                studyUsers2.clear();
                for(DataSnapshot item :snapshot.getChildren())
                {
                    studyUsers2.add(item.getValue(StudyUsers.class));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        // 출석한 user의 이름을 가져오기 위한 db사용
        FirebaseDatabase.getInstance().getReference().child("users").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                userModels.clear();
                for (DataSnapshot item : snapshot.getChildren()) {
                    userModels.add(item.getValue(UserModel.class));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

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
                else
                {
                    isHost = false;
                }

                if(menuStudyModel.isMission)
                {
                    missionConst.setVisibility(View.VISIBLE);
                    missionText.setText(menuStudyModel.getMissionText());
                    rewordText.setText(menuStudyModel.getMissionReword());
                }
                else
                {
                    missionConst.setVisibility(View.GONE);
                }

                if(menuStudyModel.isUseFine())
                {
                    checkUseFine = true;
                    fineCheckBox.setChecked(true);
                    fineConst.setVisibility(View.VISIBLE);
                    try {
                        menuFineTitle.setText(menuStudyModel.getFine_title());
                        menuFine.setText(menuStudyModel.getFine());
                    } catch (Exception e) {

                    }
                }
                else
                {
                    checkUseFine = false;
                    fineCheckBox.setChecked(false);
                    fineConst.setVisibility(View.GONE);
                }

                if(menuStudyModel.getStudyHostUid().equals(FirebaseAuth.getInstance().getCurrentUser().getUid()))
                {
                    fineCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                        @Override
                        public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                            if(fineCheckBox.isChecked())
                            {
                                if(menuStudyModel.firstFineCheck) {
                                    final LinearLayout fineLinear = (LinearLayout) View.inflate(StudyRoomMenuActivity.this, R.layout.activity_fine, null);
                                    final AlertDialog.Builder customDialog = new AlertDialog.Builder(StudyRoomMenuActivity.this);
                                    customDialog.setView(fineLinear)
                                            .setCancelable(false)
                                            .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialogInterface, int i) {

                                                }
                                            })
                                            .setNegativeButton("취소", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialogInterface, int i) {

                                                }
                                            });
                                    final AlertDialog dialog = customDialog.create();
                                    dialog.show();
                                    dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {
                                            EditText fineTitle = (EditText) dialog.findViewById(R.id.fine_fineTitleText);
                                            EditText fine = (EditText) dialog.findViewById(R.id.fine_fineText);

                                            if (fineTitle.getText().toString().length() <= 0) {
                                                Toast.makeText(getApplicationContext(), "벌금 부과항목을 입력하지 않으셨습니다!", Toast.LENGTH_SHORT).show();
                                                return;
                                            }

                                            if (fine.getText().toString().length() <= 0) {
                                                Toast.makeText(getApplicationContext(), "벌금 단위를 입력하지 않으셨습니다!", Toast.LENGTH_SHORT).show();
                                                return;
                                            }

                                            Map<String, Object> taskMap = new HashMap<String, Object>();
                                            taskMap.put("useFine", true);
                                            taskMap.put("firstFineCheck", false);
                                            taskMap.put("fine_title", fineTitle.getText().toString());
                                            taskMap.put("fine", fine.getText().toString());
                                            FirebaseDatabase.getInstance().getReference().child("allStudy").child(getIntent().getExtras().getString("study"))
                                                    .updateChildren(taskMap);

                                            menuFineTitle.setText(fineTitle.getText().toString());
                                            menuFine.setText(fine.getText().toString());

                                            fineCheckBox.setChecked(true);
                                            fineConst.setVisibility(View.VISIBLE);
                                            dialog.dismiss();
                                        }
                                    });
                                    dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {
                                            fineCheckBox.setChecked(false);
                                            dialog.dismiss();
                                        }
                                    });
                                }
                                else
                                {
                                    fineConst.setVisibility(View.VISIBLE);
                                    Map<String, Object> taskMap = new HashMap<String, Object>();
                                    taskMap.put("useFine", true);
                                    FirebaseDatabase.getInstance().getReference().child("allStudy").child(getIntent().getExtras().getString("study"))
                                            .updateChildren(taskMap);
                                    menuFineTitle.setText(menuStudyModel.getFine_title());
                                    menuFine.setText(menuStudyModel.getFine());
                                }
                            }
                            else
                            {
                                Map<String, Object> taskMap = new HashMap<String, Object>();
                                taskMap.put("useFine", false);
                                FirebaseDatabase.getInstance().getReference().child("allStudy").child(getIntent().getExtras().getString("study"))
                                        .updateChildren(taskMap);
                                fineConst.setVisibility(View.GONE);
                            }
                        }
                    });
                }
                else
                {
                    fineCheckBox.setOnTouchListener(new View.OnTouchListener() {
                        @Override
                        public boolean onTouch(View view, MotionEvent motionEvent) {
                            Toast.makeText(getApplicationContext(), "설정 및 수정은 스터디 호스트만 가능합니다!", Toast.LENGTH_SHORT).show();
                            return true;
                        }
                    });
                }

                if(checkUseFine)
                {
                    fineRecyclerView.setVisibility(View.VISIBLE);
                    bringFineUnit = Integer.parseInt(menuStudyModel.getFine());
                }
                else
                {
                    fineRecyclerView.setVisibility(View.INVISIBLE);
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
                else
                {
                    Toast.makeText(getApplicationContext(), "설정 및 수정은 스터디 호스트만 가능합니다!", Toast.LENGTH_SHORT).show();
                    return;
                }
            }
        });

    }

    class FineRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>
    {
        private List<StudyUsers> studyMenuStudyUsers2 = new ArrayList<>();
        private String uid;

        public FineRecyclerViewAdapter() {

            FirebaseDatabase.getInstance().getReference().child("allStudy").child(getIntent().getExtras().getString("study"))
                    .child("studyUsers").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    studyMenuStudyUsers2.clear();
                    for(DataSnapshot item :snapshot.getChildren())
                    {
                        studyMenuStudyUsers2.add(item.getValue(StudyUsers.class));
                    }
                    notifyDataSetChanged();
                    // rankRecyclerView.scrollToPosition(attendanceModels2.size() - 1);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });

        }

        @NonNull
        @Override // item_studyroom을 리사이클러뷰에 연결
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_study_users_fine,parent,false);
            StudyRoomMenuActivity.FineRecyclerViewAdapter.CustomViewHolder holder = new StudyRoomMenuActivity.FineRecyclerViewAdapter.CustomViewHolder(view);

            return holder;
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
            final StudyRoomMenuActivity.FineRecyclerViewAdapter.CustomViewHolder customViewHolder = (StudyRoomMenuActivity.FineRecyclerViewAdapter.CustomViewHolder) holder;
            //Picasso.get().load(studyModels.get(position).getProfile()).into(customViewHolder.imageView);

            /* // 이미지 삽입
            Glide.with(customViewHolder.itemView.getContext())
                    .load(studyModels.get(position).getProfile())
                    .error(R.drawable.rank_icon)
                    .into(((StudyRoomActivity.RankRecyclerViewAdapter.CustomViewHolder)holder).imageView);

             */
            //Log.d(TAG,studyModels.get(position).getProfile());

            customViewHolder.recyclerfine2.setText(studyMenuStudyUsers2.get(position).totalFine);
            //customViewHolder.rank.setText(Integer.toString(position+1)+"등");
        }

        @Override
        public int getItemCount() {
            return (studyMenuStudyUsers2 != null ? studyMenuStudyUsers2.size() : 0);
        }

        private class CustomViewHolder extends RecyclerView.ViewHolder {
            public TextView recyclerfine2;
            public Button plusButton2;
            public Button minusButton2;

            public CustomViewHolder(View view) {
                super(view);
                recyclerfine2 = (TextView) view.findViewById(R.id.item_study_users_fine_fineText);
                plusButton2 = (Button) view.findViewById(R.id.item_study_users_fine_plusButton);
                minusButton2 = (Button) view.findViewById(R.id.item_study_users_fine_minusButton);

                plusButton2.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        final LinearLayout plusFine = (LinearLayout) View.inflate(StudyRoomMenuActivity.this, R.layout.activity_plus_fine, null);
                        final AlertDialog.Builder customDialog = new AlertDialog.Builder(StudyRoomMenuActivity.this);
                        customDialog.setView(plusFine)
                                .setCancelable(false)
                                .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {

                                    }
                                })
                                .setNegativeButton("취소", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {

                                    }
                                });
                        final AlertDialog dialog = customDialog.create();
                        dialog.show();
                        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                final EditText reasonText = (EditText) dialog.findViewById(R.id.plus_fine_reasonText);

                                if(reasonText.getText().toString().length() <= 0)
                                {
                                    Toast.makeText(getApplicationContext(), "벌금 사유를 입력하지 않으셨습니다!", Toast.LENGTH_SHORT).show();
                                    return;
                                }

                                final FineModel fineModel = new FineModel();
                                fineModel.fine_reason = reasonText.getText().toString();
                                fineModel.fine_day = dayToString(getToDay()) + " " + timeToString(getTime());

                                int currentFine = Integer.parseInt(studyMenuStudyUsers2.get(getAdapterPosition()).totalFine);
                                currentFine += bringFineUnit;
                                final Map<String, Object> taskMap = new HashMap<String, Object>();
                                taskMap.put("totalFine", Integer.toString(currentFine));

                                FirebaseDatabase.getInstance().getReference().child("allStudy").child(getIntent().getExtras().getString("study"))
                                        .child("studyUsers").child(studyMenuStudyUsers2.get(getAdapterPosition()).user)
                                        .child("fine_history").push().setValue(fineModel);

                                FirebaseDatabase.getInstance().getReference().child("allStudy").child(getIntent().getExtras().getString("study"))
                                        .child("studyUsers").child(studyMenuStudyUsers2.get(getAdapterPosition()).user)
                                        .updateChildren(taskMap);

                                dialog.dismiss();
                            }
                        });
                        dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                dialog.dismiss();
                            }
                        });
                    }
                });

                /* //  리사이클러뷰 클릭하면 유저정보 보여줄까???
                view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        int position = getAdapterPosition();
                        if(position != RecyclerView.NO_POSITION)
                        {
                            String studyKey = keyList.get(position);
                            Log.d(TAG, keyList.get(position));
                            Intent intent = new Intent(getActivity(), StudyRoomActivity.class);
                            intent.putExtra("studykey", studyKey);
                            getActivity().startActivity(intent);
                            //Toast.makeText(getContext(),"클릭",Toast.LENGTH_SHORT).show();
                        }

                    }
                });

                 */

            }
        }
    }

    class UsersRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>
    {
        private List<StudyUsers> studyMenuStudyUsers = new ArrayList<>();
        private String uid;

        public UsersRecyclerViewAdapter() {

            FirebaseDatabase.getInstance().getReference().child("allStudy").child(getIntent().getExtras().getString("study"))
                    .child("studyUsers").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    studyMenuStudyUsers.clear();
                    for(DataSnapshot item :snapshot.getChildren())
                    {
                        studyMenuStudyUsers.add(item.getValue(StudyUsers.class));
                    }
                    notifyDataSetChanged();
                    // rankRecyclerView.scrollToPosition(attendanceModels2.size() - 1);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });

        }

        @NonNull
        @Override // item_studyroom을 리사이클러뷰에 연결
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_study_users,parent,false);
            StudyRoomMenuActivity.UsersRecyclerViewAdapter.CustomViewHolder holder = new StudyRoomMenuActivity.UsersRecyclerViewAdapter.CustomViewHolder(view);

            return holder;
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
            final StudyRoomMenuActivity.UsersRecyclerViewAdapter.CustomViewHolder customViewHolder = (StudyRoomMenuActivity.UsersRecyclerViewAdapter.CustomViewHolder) holder;
            //Picasso.get().load(studyModels.get(position).getProfile()).into(customViewHolder.imageView);

            /* // 이미지 삽입
            Glide.with(customViewHolder.itemView.getContext())
                    .load(studyModels.get(position).getProfile())
                    .error(R.drawable.rank_icon)
                    .into(((StudyRoomActivity.RankRecyclerViewAdapter.CustomViewHolder)holder).imageView);

             */
            //Log.d(TAG,studyModels.get(position).getProfile());


            // 유저 이름 가져오기
            for(int i = 0; i < userModels.size(); i++)
            {
                if(studyMenuStudyUsers.get(position).user.equals(userModels.get(i).userUid))
                {
                    customViewHolder.userName.setText(userModels.get(i).userName);
                }
            }

            customViewHolder.imageView.setImageResource(R.drawable.user_icon);

        }

        @Override
        public int getItemCount() {
            return (studyMenuStudyUsers != null ? studyMenuStudyUsers.size() : 0);
        }

        private class CustomViewHolder extends RecyclerView.ViewHolder {
            public ImageView imageView;
            public TextView userName;

            public CustomViewHolder(View view) {
                super(view);
                imageView =(ImageView) view.findViewById(R.id.item_study_users_imageview);
                userName = (TextView) view.findViewById(R.id.item_study_users_name);

                view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        giveUid = studyMenuStudyUsers.get(getAdapterPosition()).user;

                        Intent intent3 = new Intent(StudyRoomMenuActivity.this, FineReasonActivity.class);
                        intent3.putExtra("studyK", getIntent().getExtras().getString("study"));
                        intent3.putExtra("selectedUid", giveUid);
                        StudyRoomMenuActivity.this.startActivity(intent3);
                    }
                });

            }
        }
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
        studyModel1.setMissionReword(studyMode2.getMissionReword());
        studyModel1.setFine_title(studyMode2.getFine_title());
        studyModel1.setFine(studyMode2.getFine());
        studyModel1.setUseFine(studyMode2.isUseFine());
        studyModel1.setFirstFineCheck(studyMode2.isFirstFineCheck());
    }

    public static String getToDay()
    {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy+MM+dd");
        return sdf.format(new Date());
    }

    public static String dayToString(String day)
    {
        String stDay = day.replace('+','/');
        return stDay;
    }

    public static String getTime()
    {
        SimpleDateFormat sdf = new SimpleDateFormat("HH+mm+ss");
        return sdf.format(new Date());
    }

    public static String timeToString(String time)
    {
        String stTime = time.replace('+', ':');
        return stTime;
    }

    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_OUTSIDE) {
            return false;
        }
        return true;
    }
}