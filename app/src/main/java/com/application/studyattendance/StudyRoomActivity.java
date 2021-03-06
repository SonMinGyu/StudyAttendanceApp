package com.application.studyattendance;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.application.studyattendance.model.AttendanceModel;
import com.application.studyattendance.model.ChatModel;
import com.application.studyattendance.model.StudyModel;
import com.application.studyattendance.model.TodayGoalModel;
import com.application.studyattendance.model.UserModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.security.MessageDigest;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class StudyRoomActivity extends AppCompatActivity {

    private List<StudyModel> studyModels = new ArrayList<>();
    private List<AttendanceModel> attendanceModels = new ArrayList<>();
    private List<UserModel> userModels = new ArrayList<>();
    private StudyModel studyModel = new StudyModel();
    private List<ChatModel> chatModels2 = new ArrayList<>();
    private static final String TAG = "mainmainmain";

    Boolean isAlreadyAttendance = false;
    String getUid;

    Button attendanceButton;
    TextView noneAttendanceText;
    TextView studyName;
    TextView studyPlace;
    TextView studyPlaceText;
    EditText chatEditText;
    ImageButton chatSendButton;
    ImageButton timerButton;
    ImageButton menuButton;
    boolean dateFlag = false;
    FrameLayout menuFrameLayout;
    ConstraintLayout placeConstraintLayout;

    final String attendanceUid = FirebaseAuth.getInstance().getCurrentUser().getUid();

    private RecyclerView rankRecyclerView;
    private RecyclerView chatRecyclerView;
    private RecyclerView todayGoalRecyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_study_room);

        attendanceButton = (Button) findViewById(R.id.attendance_button);
        noneAttendanceText = (TextView) findViewById(R.id.none_attendance_text);
        studyName = (TextView)findViewById(R.id.study_room_study_name);
        studyPlace = (TextView) findViewById(R.id.study_room_study_meet_place);
        studyPlaceText = (TextView) findViewById(R.id.study_room_place_text);
        chatEditText = (EditText) findViewById(R.id.study_room_chat_edittext);
        chatSendButton = (ImageButton) findViewById(R.id.study_room_send_button);
        timerButton = (ImageButton) findViewById(R.id.study_room_timer_button);
        menuButton = (ImageButton) findViewById(R.id.study_room_menuButton);
        menuFrameLayout = (FrameLayout) findViewById(R.id.study_room_framelayout);
        placeConstraintLayout = (ConstraintLayout) findViewById(R.id.constraintLayout19);

        Intent intent = getIntent();

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
                    if(item.getValue(StudyModel.class).getStudyKey().equals(getIntent().getExtras().getString("studykey")))
                    {
                        cat(studyModel, item.getValue(StudyModel.class));
                    }
                }
                studyName.setText(studyModel.getStudyName());
                if(studyModel.getOffOrOn() == 0 || studyModel.getOffOrOn() == 2)
                {
                    studyPlaceText.setVisibility(View.VISIBLE);
                    placeConstraintLayout.setVisibility(View.VISIBLE);
                    studyPlace.setVisibility(View.VISIBLE);
                    if(studyModel.place_nowOrLater) {
                        studyPlace.setText(studyModel.place_name + "\n" + studyModel.place_address);
                    }
                    else
                    {
                        studyPlace.setText("아직 스터디 장소를 정하지 않았습니다.\n스터디원들과 함께 장소를 정해보세요!");
                    }
                }
                else
                {
                    studyPlaceText.setVisibility(View.GONE);
                    placeConstraintLayout.setVisibility(View.GONE);
                    studyPlace.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        // 이미 출석했나안했나 검사하기 위해 db받아오기 + 출석한 사람 있나없나 검사
        FirebaseDatabase.getInstance().getReference().child("allStudy").child(getIntent().getExtras().getString("studykey"))
                .child("attendance").child(getToDay()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                attendanceModels.clear();
                for(DataSnapshot item :snapshot.getChildren())
                {
                    attendanceModels.add(item.getValue(AttendanceModel.class));
                }

                if(attendanceModels.size() <= 0)
                {
                    noneAttendanceText.setVisibility(View.VISIBLE);
                }
                else
                {
                    noneAttendanceText.setVisibility(View.GONE);
                }

                for(int i = 0; i < attendanceModels.size(); i++)
                {
                    if(attendanceModels.get(i).attendanceUid.equals(attendanceUid))
                    {
                        isAlreadyAttendance = true;
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        attendanceButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!isAlreadyAttendance) {
                    AttendanceModel attendanceModel = new AttendanceModel();
                    attendanceModel.attendanceUid = attendanceUid;
                    attendanceModel.attendanceTime = getTime();
                    FirebaseDatabase.getInstance().getReference().child("allStudy").child(getIntent().getExtras().getString("studykey"))
                            .child("attendance").child(getToDay()).push().setValue(attendanceModel);
                    Toast.makeText(getApplicationContext(), "출석완료!",Toast.LENGTH_SHORT).show();

                    // 출석하면 출석한 사람에 대한 오늘의 목표 생성
                    TodayGoalModel todayGoalModel = new TodayGoalModel();
                    todayGoalModel.setUserUid(attendanceUid);
                    todayGoalModel.setTodayGoal("");
                    FirebaseDatabase.getInstance().getReference().child("allStudy").child(getIntent().getExtras().getString("studykey"))
                            .child("today_goal").child(getToDay()).child(attendanceUid).setValue(todayGoalModel);

                }
                else
                {
                    Toast.makeText(getApplicationContext(), "이미 출석하셨습니다!",Toast.LENGTH_SHORT).show();
                    getAppKeyHash();
                }
            }
        });

        // 날짜추가를 위한 채팅db 가져오기
        FirebaseDatabase.getInstance().getReference().child("allStudy").child(getIntent().getExtras().getString("studykey"))
                .child("chatting").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                chatModels2.clear();
                for(DataSnapshot item :snapshot.getChildren())
                {
                    chatModels2.add(item.getValue(ChatModel.class));
                }

                if(chatModels2.size() == 0)
                {
                    dateFlag = true;
                }
                else
                {
                    if(!chatModels2.get(chatModels2.size()-1).getChatDate().equals(getToDay()))
                    {
                        dateFlag = true;
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        chatSendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(chatEditText.getText().toString().length() <= 0)
                {
                    return;
                }

                ChatModel chatModel = new ChatModel();
                if(dateFlag)
                {
                    ChatModel chatModel1 = new ChatModel();
                    chatModel1.setChattingText("first Message of " + getToDay());
                    chatModel1.setDayOfFirstMessage(true);
                    chatModel1.setChattingUid(FirebaseAuth.getInstance().getCurrentUser().getUid());
                    chatModel1.setChatTime(getTime());
                    chatModel1.setChatDate(getToDay());
                    FirebaseDatabase.getInstance().getReference().child("allStudy").child(getIntent().getExtras().getString("studykey"))
                            .child("chatting").push().setValue(chatModel1);
                    dateFlag = false;
                }

                chatModel.setChattingUid(FirebaseAuth.getInstance().getCurrentUser().getUid());
                chatModel.setChattingText(chatEditText.getText().toString());
                chatModel.setChatTime(getTime());
                chatModel.setChatDate(getToDay());

                FirebaseDatabase.getInstance().getReference().child("allStudy").child(getIntent().getExtras().getString("studykey"))
                        .child("chatting").push().setValue(chatModel);

                chatEditText.setText(null);
            }
        });

        timerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(StudyRoomActivity.this, TimerActivity.class);
                StudyRoomActivity.this.startActivity(intent);
            }
        });


        menuButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent menuIntent = new Intent(StudyRoomActivity.this, StudyRoomMenuActivity.class);
                menuIntent.putExtra("study", getIntent().getExtras().getString("studykey"));
                StudyRoomActivity.this.startActivity(menuIntent);
                //overridePendingTransition(R.anim.enter_from_right, 0);


                /* // 다이얼로그 이용
                final ConstraintLayout linear2 = (ConstraintLayout) View.inflate(StudyRoomActivity.this, R.layout.activity_study_room_menu, null);
                final AlertDialog.Builder customDialog2 = new AlertDialog.Builder(StudyRoomActivity.this);
                customDialog2.setView(linear2);

                final AlertDialog dialog2 = customDialog2.create();

                WindowManager.LayoutParams wmlp = dialog2.getWindow().getAttributes();
                wmlp.gravity = Gravity.TOP | Gravity.RIGHT;
                wmlp.x = 1100;   //x position
                wmlp.y = 500;   //y position
                dialog2.getWindow().setAttributes(wmlp);

                dialog2.show();

                Display display = getWindowManager().getDefaultDisplay();
                Point size = new Point();
                display.getSize(size);

                Window window = dialog2.getWindow();
                int x = (int) (size.x * 0.75);
                int y = (int) (size.y * 1);

                window.setLayout(x,y);
                 */


            }
        });

        rankRecyclerView = (RecyclerView) findViewById(R.id.study_room_rank_recyclerview);
        rankRecyclerView.setAdapter(new RankRecyclerViewAdapter());
        rankRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        chatRecyclerView = (RecyclerView) findViewById(R.id.study_room_chat_recyclerview);
        chatRecyclerView.setAdapter(new ChatRecyclerViewAdapter());
        chatRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        todayGoalRecyclerView = (RecyclerView) findViewById(R.id.study_room_today_goal_recyclerView);
        todayGoalRecyclerView.setAdapter(new TodayGoalRecyclerViewAdapter());
        todayGoalRecyclerView.setLayoutManager(new LinearLayoutManager(this));

    }

    // recyclerview adapter
    class RankRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>
    {
        private List<AttendanceModel> attendanceModels2 = new ArrayList<>();
        private String uid;

        public RankRecyclerViewAdapter() {

            FirebaseDatabase.getInstance().getReference().child("allStudy").child(getIntent().getExtras().getString("studykey"))
                    .child("attendance").child(getToDay()).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    attendanceModels2.clear();
                    for(DataSnapshot item :snapshot.getChildren())
                    {
                        attendanceModels2.add(item.getValue(AttendanceModel.class));
                    }
                    notifyDataSetChanged();
                    rankRecyclerView.scrollToPosition(attendanceModels2.size() - 1);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });

        }

        @NonNull
        @Override // item_studyroom을 리사이클러뷰에 연결
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {


            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_attendance_rank,parent,false);
            StudyRoomActivity.RankRecyclerViewAdapter.CustomViewHolder holder = new StudyRoomActivity.RankRecyclerViewAdapter.CustomViewHolder(view);


            return holder;
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
            final StudyRoomActivity.RankRecyclerViewAdapter.CustomViewHolder customViewHolder = (StudyRoomActivity.RankRecyclerViewAdapter.CustomViewHolder) holder;
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
                if(attendanceModels2.get(position).attendanceUid.equals(userModels.get(i).userUid))
                {
                    customViewHolder.userName.setText(userModels.get(i).userName);
                }
            }

            customViewHolder.imageView.setImageResource(R.drawable.rank_icon);
            customViewHolder.rank.setText(Integer.toString(position+1)+"등");
        }

        @Override
        public int getItemCount() {
            return (attendanceModels2 != null ? attendanceModels2.size() : 0);
        }

        private class CustomViewHolder extends RecyclerView.ViewHolder {
            public ImageView imageView;
            public TextView rank;
            public TextView userName;

            public CustomViewHolder(View view) {
                super(view);
                imageView =(ImageView) view.findViewById(R.id.attendance_rank_imageview);
                rank = (TextView) view.findViewById(R.id.attendance_rank_rank);
                userName = (TextView) view.findViewById(R.id.attendance_rank_name);

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

    // 채팅하기위한 리사이클러뷰
    class ChatRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>
    {
        private List<ChatModel> chatModels = new ArrayList<>();
        private String uid;
        //private String recentDate;

        public ChatRecyclerViewAdapter() {

            FirebaseDatabase.getInstance().getReference().child("allStudy").child(getIntent().getExtras().getString("studykey"))
                    .child("chatting").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    chatModels.clear();
                    for(DataSnapshot item :snapshot.getChildren())
                    {
                        chatModels.add(item.getValue(ChatModel.class));
                        //Log.d(TAG, item.getValue(ChatModel.class).getChattingText());
                    }
                    // 리사이클러뷰 갱신
                    notifyDataSetChanged();
                    // 마지막 메시지로
                    chatRecyclerView.scrollToPosition(chatModels.size() - 1);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });

        }

        @Override
        public int getItemViewType(int position) {
            if(chatModels.get(position).isDayOfFirstMessage())
            {
                return 0;
            }
            else
            {
                return 1;
            }
        }

        @NonNull
        @Override // item_studyroom을 리사이클러뷰에 연결
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view;
            switch (viewType)
            {
                case 0:
                    view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_message_date,parent,false);
                    StudyRoomActivity.ChatRecyclerViewAdapter.CustomViewHolder1 holder1 = new StudyRoomActivity.ChatRecyclerViewAdapter.CustomViewHolder1(view);
                    return holder1;

                case 1:
                    view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_message,parent,false);
                    StudyRoomActivity.ChatRecyclerViewAdapter.CustomViewHolder holder = new StudyRoomActivity.ChatRecyclerViewAdapter.CustomViewHolder(view);

                    return holder;
            }

            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_message,parent,false);
            StudyRoomActivity.ChatRecyclerViewAdapter.CustomViewHolder holder = new StudyRoomActivity.ChatRecyclerViewAdapter.CustomViewHolder(view);

            return holder;
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
            if(!chatModels.get(position).isDayOfFirstMessage()) {

                final StudyRoomActivity.ChatRecyclerViewAdapter.CustomViewHolder customViewHolder = (StudyRoomActivity.ChatRecyclerViewAdapter.CustomViewHolder) holder;
                //Picasso.get().load(studyModels.get(position).getProfile()).into(customViewHolder.imageView);

            /* // 이미지 삽입
            Glide.with(customViewHolder.itemView.getContext())
                    .load(studyModels.get(position).getProfile())
                    .error(R.drawable.rank_icon)
                    .into(((StudyRoomActivity.RankRecyclerViewAdapter.CustomViewHolder)holder).imageView);

             */
                //Log.d(TAG,studyModels.get(position).getProfile());


                // 내가보낸 메시지
                if (chatModels.get(position).getChattingUid().equals(attendanceUid)) {
                    customViewHolder.message_imageview.setImageResource(R.drawable.message_icon);
                    for (int i = 0; i < userModels.size(); i++) {
                        if (chatModels.get(position).getChattingUid().equals(userModels.get(i).userUid)) {
                            customViewHolder.message_user_name.setText(userModels.get(i).getUserName());
                        }
                    }
                    customViewHolder.message_message.setText(chatModels.get(position).getChattingText());
                    customViewHolder.message_message.setBackgroundResource(R.drawable.message_icon_right);
                    customViewHolder.message_user_time.setText(getTimeOnString(chatModels.get(position).getChatTime()));
                    //customViewHolder.message_linear_time.setGravity(Gravity.RIGHT);
                    customViewHolder.message_profile_linear.setVisibility(View.INVISIBLE);
                    customViewHolder.message_message_linear.setVisibility(View.VISIBLE);
                    customViewHolder.message_message_linear.setGravity(Gravity.RIGHT);
                    customViewHolder.message_linear.setGravity(Gravity.RIGHT);
                }
                // 다른사람이 보낸 메시지
                else {
                    customViewHolder.message_imageview.setImageResource(R.drawable.message_icon);
                    for (int i = 0; i < userModels.size(); i++) {
                        if (chatModels.get(position).getChattingUid().equals(userModels.get(i).userUid)) {
                            customViewHolder.message_user_name.setText(userModels.get(i).getUserName());
                        }
                    }
                    customViewHolder.message_message.setText(chatModels.get(position).getChattingText());
                    customViewHolder.message_message.setBackgroundResource(R.drawable.message_icon_left);
                    customViewHolder.message_user_time.setText(getTimeOnString(chatModels.get(position).getChatTime()));
                    //customViewHolder.message_linear_time.setGravity(Gravity.LEFT);
                    customViewHolder.message_profile_linear.setVisibility(View.VISIBLE);
                    customViewHolder.message_message_linear.setVisibility(View.VISIBLE);
                    customViewHolder.message_message_linear.setGravity(Gravity.LEFT);
                    customViewHolder.message_linear.setGravity(Gravity.LEFT);
                }
            }
            else
            {
                final StudyRoomActivity.ChatRecyclerViewAdapter.CustomViewHolder1 customViewHolder1 = (StudyRoomActivity.ChatRecyclerViewAdapter.CustomViewHolder1) holder;
                customViewHolder1.message_date.setText(getDateOnString(chatModels.get(position).getChatDate()));
                customViewHolder1.message_date_linearLayout.setGravity(Gravity.CENTER);
            }
        }

        @Override
        public int getItemCount() {
            return (chatModels != null ? chatModels.size() : 0);
        }

        private class CustomViewHolder extends RecyclerView.ViewHolder {
            public ImageView message_imageview;
            public TextView message_user_name;
            public TextView message_user_time;
            public TextView message_message;
            public LinearLayout message_profile_linear;
            public LinearLayout message_message_linear;
            public LinearLayout message_linear;
            public LinearLayout message_linear_time;

            public CustomViewHolder(View view) {
                super(view);
                message_imageview =(ImageView) view.findViewById(R.id.message_imageView);
                message_user_name = (TextView) view.findViewById(R.id.message_textview_name);
                message_user_time = (TextView) view.findViewById(R.id.message_textview_time);
                message_message = (TextView) view.findViewById(R.id.message_textview_message);
                message_profile_linear = (LinearLayout) view.findViewById(R.id.message_linearlayout_profile);
                message_message_linear = (LinearLayout) view.findViewById(R.id.message_linearlayout_message);
                message_linear = (LinearLayout) view.findViewById(R.id.message_linear);
                message_linear_time = (LinearLayout) view.findViewById(R.id.message_linearlayout_time);
            }
        }

        private class CustomViewHolder1 extends RecyclerView.ViewHolder {

            public TextView message_date;
            public LinearLayout message_date_linearLayout;

            public CustomViewHolder1(@NonNull View view) {
                super(view);

                message_date = (TextView) view.findViewById(R.id.message_date);
                message_date_linearLayout = (LinearLayout) view.findViewById(R.id.message_date_linear);
            }
        }
    }

    // recyclerview adapter
    class TodayGoalRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>
    {
        private List<TodayGoalModel> todayGoalModels = new ArrayList<>();
        private String uid;

        public TodayGoalRecyclerViewAdapter() {

            FirebaseDatabase.getInstance().getReference().child("allStudy").child(getIntent().getExtras().getString("studykey"))
                    .child("today_goal").child(getToDay()).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    todayGoalModels.clear();
                    for(DataSnapshot item :snapshot.getChildren())
                    {
                        todayGoalModels.add(item.getValue(TodayGoalModel.class));
                    }
                    notifyDataSetChanged();
                    todayGoalRecyclerView.scrollToPosition(todayGoalModels.size() - 1);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });

        }

        @NonNull
        @Override // item_studyroom을 리사이클러뷰에 연결
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_today_goal,parent,false);
            StudyRoomActivity.TodayGoalRecyclerViewAdapter.CustomViewHolder holder = new StudyRoomActivity.TodayGoalRecyclerViewAdapter.CustomViewHolder(view);

            return holder;
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
            final StudyRoomActivity.TodayGoalRecyclerViewAdapter.CustomViewHolder customViewHolder = (StudyRoomActivity.TodayGoalRecyclerViewAdapter.CustomViewHolder) holder;

            // 유저 이름 가져오기
            for(int i = 0; i < userModels.size(); i++)
            {
                if(todayGoalModels.get(position).getUserUid().equals(userModels.get(i).userUid))
                {
                    customViewHolder.userName.setText(userModels.get(i).userName + "님:");
                }
            }

            customViewHolder.userGoal.setText(todayGoalModels.get(position).getTodayGoal());
        }

        @Override
        public int getItemCount() {
            return (todayGoalModels != null ? todayGoalModels.size() : 0);
        }

        private class CustomViewHolder extends RecyclerView.ViewHolder {
            public TextView userName;
            public TextView userGoal;
            public Button modifyButton;
            public LinearLayout linearLayout;

            public CustomViewHolder(View view) {
                super(view);
                userName = (TextView) view.findViewById(R.id.item_today_goal_nameText);
                userGoal = (TextView) view.findViewById(R.id.item_today_goal_editText);
                modifyButton = (Button) view.findViewById(R.id.item_today_goal_modifyButton);
                linearLayout = (LinearLayout) view.findViewById(R.id.item_today_goal_linear);

                modifyButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        int position = getAdapterPosition();
                        if(position != RecyclerView.NO_POSITION)
                        {
                            if(!todayGoalModels.get(position).getUserUid().equals(attendanceUid))
                            {
                                Toast.makeText(getApplicationContext(), "다른 스터디원의 목표는 수정할 수 없습니다!", Toast.LENGTH_SHORT).show();
                            }
                            else
                            {
                                final LinearLayout linear2 = (LinearLayout) View.inflate(StudyRoomActivity.this, R.layout.activity_modify_today_goal, null);
                                final AlertDialog.Builder customDialog2 = new AlertDialog.Builder(StudyRoomActivity.this);
                                customDialog2.setView(linear2)
                                        .setCancelable(false)
                                        .setPositiveButton("수정", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialogInterface, int i) {

                                            }
                                        })
                                        .setNegativeButton("취소", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialogInterface, int i) {

                                            }
                                        });
                                final AlertDialog dialog2 = customDialog2.create();
                                dialog2.show();
                                dialog2.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        EditText today_goal = (EditText) dialog2.findViewById(R.id.today_goal_editText);
                                        if(today_goal.getText().toString().length() <= 0)
                                        {
                                            Toast.makeText(getApplicationContext(), "수정할 내용이 없습니다!", Toast.LENGTH_SHORT).show();
                                        }
                                        else
                                        {
                                            Log.d(TAG, "수정실행");
                                            Map<String, Object> taskMap = new HashMap<String, Object>();
                                            taskMap.put("todayGoal", today_goal.getText().toString());
                                            FirebaseDatabase.getInstance().getReference().child("allStudy").child(getIntent().getExtras().getString("studykey"))
                                                    .child("today_goal").child(getToDay()).child(attendanceUid).updateChildren(taskMap);
                                            dialog2.dismiss();
                                        }
                                    }
                                });
                                dialog2.getButton(AlertDialog.BUTTON_NEGATIVE).setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        dialog2.dismiss();
                                    }
                                });
                            }
                        }

                    }
                });

            }
        }
    }

    public static String getToDay()
    {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy+MM+dd");
        return sdf.format(new Date());
    }

    public static String getTime()
    {
        SimpleDateFormat sdf = new SimpleDateFormat("HH+mm+ss");
        return sdf.format(new Date());
    }

    public static String getTimeOnString(String message_time)
    {
        String time = message_time.substring(0,5);
        String stringTime = time.replace("+",":");
        return stringTime;
    }

    public static String getDateOnString(String message_Date)
    {
        char[] charDate1 = message_Date.toCharArray();
        char[] charDate = new char[13];
        charDate[0] = charDate1[0];
        charDate[1] = charDate1[1];
        charDate[2] = charDate1[2];
        charDate[3] = charDate1[3];
        charDate[4] = '년';
        charDate[5] = ' ';
        charDate[6] = charDate1[5];
        charDate[7] = charDate1[6];
        charDate[8] = '년';
        charDate[9] = ' ';
        charDate[10] = charDate1[8];
        charDate[11] = charDate1[9];
        charDate[12] = '일';

        return String.valueOf(charDate);
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
        studyModel1.setStudyroom_password(studyMode2.getStudyroom_password());
        studyModel1.setMissionReword(studyMode2.getMissionReword());
        studyModel1.setFine_title(studyMode2.getFine_title());
        studyModel1.setFine(studyMode2.getFine());
        studyModel1.setUseFine(studyMode2.isUseFine());
        studyModel1.setFirstFineCheck(studyMode2.isFirstFineCheck());
    }

    private void getAppKeyHash() {
        try {
            PackageInfo info = getPackageManager().getPackageInfo(getPackageName(), PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md;
                md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                String something = new String(Base64.encode(md.digest(), 0));
                Log.e("Hash key", something);
            }
        } catch (Exception e) {
            // TODO Auto-generated catch block
            Log.e("name not found", e.toString());
        }
    }

}