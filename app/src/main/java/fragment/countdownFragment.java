package fragment;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.application.studyattendance.R;
import com.application.studyattendance.StudyRoomActivity;
import com.application.studyattendance.model.CountdownModel;
import com.application.studyattendance.model.StudyModel;
import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

public class countdownFragment extends Fragment {

    private static final String TAG = "mainmainmain";

    MyTimer myTimer;
    Button startButton;
    Button stopButton;
    Button resetButton;
    boolean isCountStart = false;
    boolean canReset = false;
    int time;
    TextView hourText;
    TextView minuteText;
    TextView secondText;
    String tempHour;
    String tempMinute;
    String tempSecond;
    long tmepMillisecond;
    boolean firstStart = true;
    Button addButton;
    EditText memoText;
    String uid2 = FirebaseAuth.getInstance().getCurrentUser().getUid();
    List<CountdownModel> countdownModels2 = new ArrayList<>();
    List<String> strings = new ArrayList<>();
    boolean isSameMemo = false;
    int bookmarkSize;

    RecyclerView recyclerView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_countdown, container, false);

        startButton = (Button) view.findViewById(R.id.btnStart);
        stopButton = (Button) view.findViewById(R.id.btnStop);
        resetButton = (Button) view.findViewById(R.id.btnReset);
        hourText = (TextView) view.findViewById(R.id.countdown_hour_text);
        minuteText = (TextView) view.findViewById(R.id.countdown_minute_text);
        secondText = (TextView) view.findViewById(R.id.countdown_second_text);
        memoText = (EditText) view.findViewById(R.id.fragment_countdown_memotext);
        addButton = (Button) view.findViewById(R.id.fragment_countdown_addbutton);

        recyclerView = (RecyclerView) view.findViewById(R.id.fragment_countdown_recyclerview);
        recyclerView.setAdapter(new CountdownRecyclerViewAdapter());
        recyclerView.setLayoutManager(new LinearLayoutManager(inflater.getContext()));

        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if((hourText.getText().length() <= 0) && (minuteText.getText().length() <= 0) && (secondText.getText().length() <= 0))
                {
                    Toast.makeText(getContext(), "카운트할 시간,분,초를 입력해주세요", Toast.LENGTH_SHORT).show();
                    return;
                }
                if(isCountStart)
                {
                    myTimer.cancel();
                    int millisMinute = (int)(tmepMillisecond/1000) % 3600;
                    int millisSecond = (int) millisMinute % 60;
                    hourText.setText(Integer.toString((int) (tmepMillisecond/1000)/3600));
                    minuteText.setText(Integer.toString(millisMinute/60));
                    secondText.setText(Integer.toString(millisSecond));
                    startButton.setText("Start");

                    return;
                }

                if(firstStart) {
                    // stop했을때 값을 가져오기 위해서 저장
                    tempHour = hourText.getText().toString();
                    tempMinute = minuteText.getText().toString();
                    tempSecond = secondText.getText().toString();
                    firstStart = false;
                }

                // 타이머 돌아갈때 클릭못하게
                hourText.setFocusable(false);
                minuteText.setFocusable(false);
                secondText.setFocusable(false);
                hourText.setClickable(false);
                minuteText.setClickable(false);
                secondText.setClickable(false);

                // 타이머 돌아갈때 커서 안보이게
                hourText.setCursorVisible(false);
                minuteText.setCursorVisible(false);
                secondText.setCursorVisible(false);
                time = calculateTime(hourText.getText().toString(), minuteText.getText().toString(), secondText.getText().toString());
                myTimer = new MyTimer(time, 1000);

                myTimer.start();
                isCountStart = true;
                startButton.setText("Pause");

            }
        });

        stopButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!isCountStart)
                {
                    return;
                }

                myTimer.cancel();
                hourText.setCursorVisible(true);
                minuteText.setCursorVisible(true);
                secondText.setCursorVisible(true);

                startButton.setText("Start");
                canReset = true;
                isCountStart = false;
                hourText.setText(tempHour);
                minuteText.setText(tempMinute);
                secondText.setText(tempSecond);

                // stop했을때는 값 수정 불가, reset 해야만 가능
                hourText.setFocusable(false);
                minuteText.setFocusable(false);
                secondText.setFocusable(false);
                hourText.setClickable(false);
                minuteText.setClickable(false);
                secondText.setClickable(false);
            }
        });

        // 리셋클릭시 00시 00분 00초로 돌아감
       resetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(canReset)
                {
                    myTimer.cancel();
                    hourText.setCursorVisible(true);
                    minuteText.setCursorVisible(true);
                    secondText.setCursorVisible(true);
                    isCountStart = false;
                    hourText.setText(null);
                    minuteText.setText(null);
                    secondText.setText(null);
                    canReset = false;
                    startButton.setText("Start");

                    return;
                }

                if(!isCountStart)
                {
                    return;
                }

                myTimer.cancel();
                startButton.setText("Start");
                firstStart = true;
                hourText.setCursorVisible(true);
                minuteText.setCursorVisible(true);
                secondText.setCursorVisible(true);
                isCountStart = false;
                canReset = false;
                hourText.setText(null);
                minuteText.setText(null);
                secondText.setText(null);
            }
        });

       addButton.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View view) {
               isSameMemo = false;

               if((hourText.getText().length() <= 0) && (minuteText.getText().length() <= 0) && (secondText.getText().length() <= 0))
               {
                   Toast.makeText(getContext(), "추가할 시간이 없습니다!", Toast.LENGTH_SHORT).show();
                   return;
               }

               if(memoText.getText().length() <= 0)
               {
                   Toast.makeText(getContext(), "추가할 시간의 메모를 입력해주세요!", Toast.LENGTH_SHORT).show();
                   return;
               }

               for(int i = 0; i < bookmarkSize; i++){
                   if(strings.get(i).equals(memoText.getText().toString()))
                   {
                       isSameMemo = true;
                   }
               }

               if(isSameMemo)
               {
                   Toast.makeText(getContext(), "같은 이름의 메모가 이미 있습니다!", Toast.LENGTH_SHORT).show();
                   return;
               }

               CountdownModel countdownModel = new CountdownModel();
               countdownModel.setMemo(memoText.getText().toString());

               if(hourText.getText().toString().length() <= 0)
               {
                   countdownModel.setHour("0");
               }
               else
               {
                   countdownModel.setHour(hourText.getText().toString());
               }

               if(minuteText.getText().toString().length() <= 0)
               {
                   countdownModel.setMinute("0");
               }
               else
               {
                   countdownModel.setMinute(minuteText.getText().toString());
               }

               if(secondText.getText().toString().length() <= 0)
               {
                   countdownModel.setSecond("0");
               }
               else
               {
                   countdownModel.setSecond(secondText.getText().toString());
               }

               FirebaseDatabase.getInstance().getReference().child("users").child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                       .child("countdownBookmark").push().setValue(countdownModel);
               memoText.setText(null);
           }
       });

        return view;
    }

    class CountdownRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>
    {
        private List<CountdownModel> countdownModels = new ArrayList<>();
        private String uid;

        public CountdownRecyclerViewAdapter() {
            uid = FirebaseAuth.getInstance().getCurrentUser().getUid();

            FirebaseDatabase.getInstance().getReference().child("users").child(uid).child("countdownBookmark").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    countdownModels.clear();
                    strings.clear();
                    for(DataSnapshot item :dataSnapshot.getChildren())
                    {
                        countdownModels.add(item.getValue(CountdownModel.class));
                        strings.add(item.getValue(CountdownModel.class).getMemo());
                    }
                    notifyDataSetChanged();
                    // isSameMemo = false;
                    recyclerView.scrollToPosition(countdownModels.size() - 1);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });


        }

        @NonNull
        @Override // item_studyroom을 리사이클러뷰에 연결
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_countdown_bookmark, parent, false);

            countdownFragment.CountdownRecyclerViewAdapter.CustomViewHolder holder = new countdownFragment.CountdownRecyclerViewAdapter.CustomViewHolder(view);

            return holder;
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
            final countdownFragment.CountdownRecyclerViewAdapter.CustomViewHolder customViewHolder = (countdownFragment.CountdownRecyclerViewAdapter.CustomViewHolder) holder;

            customViewHolder.memoTextView.setText(countdownModels.get(position).getMemo());
            customViewHolder.hourTextView.setText(countdownModels.get(position).getHour());
            customViewHolder.minuteTextView.setText(countdownModels.get(position).getMinute());
            customViewHolder.secondTextView.setText(countdownModels.get(position).getSecond());
        }

        @Override
        public int getItemCount() {
            bookmarkSize = countdownModels.size();
            return (countdownModels != null ? countdownModels.size() : 1);
        }

        private class CustomViewHolder extends RecyclerView.ViewHolder {
            public TextView memoTextView;
            public TextView hourTextView;
            public TextView minuteTextView;
            public TextView secondTextView;
            public Button removeButton;
            public Button birngButton;

            public CustomViewHolder(View view) {
                super(view);

                memoTextView = (TextView) view.findViewById(R.id.item_countdown_memo);
                hourTextView = (TextView) view.findViewById(R.id.item_countdown_hour);
                minuteTextView = (TextView) view.findViewById(R.id.item_countdown_minute);
                secondTextView = (TextView) view.findViewById(R.id.item_countdown_second);
                removeButton = (Button) view.findViewById(R.id.item_countdown_removeButton);
                birngButton = (Button) view.findViewById(R.id.item_countdown_bringButton);

                removeButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
                        Query removeQuery = ref.child("users").child(uid).child("countdownBookmark").orderByChild("memo")
                                .equalTo(countdownModels.get(getAdapterPosition()).getMemo());

                        removeQuery.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                for (DataSnapshot removeSnapshot: dataSnapshot.getChildren()) {
                                    removeSnapshot.getRef().removeValue();
                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {
                            }
                        });
                        Toast.makeText(getContext(), "삭제되었습니다.", Toast.LENGTH_SHORT).show();
                    }
                });

                birngButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        int position = getAdapterPosition();
                        if(position != RecyclerView.NO_POSITION)
                        {
                            resetButton.callOnClick();

                            hourText.setText(countdownModels.get(position).getHour());
                            minuteText.setText(countdownModels.get(position).getMinute());
                            secondText.setText(countdownModels.get(position).getSecond());
                            Log.d(TAG, "터치 실행");
                        }
                    }
                });
            }
        }
    }

    class MyTimer extends CountDownTimer
    {
        public MyTimer(long millisInFuture, long countDownInterval)
        {
            super(millisInFuture, countDownInterval);
        }

        @Override
        public void onTick(long millisUntilFinished) {
            tmepMillisecond = millisUntilFinished;
            int millisMinute = (int)(millisUntilFinished/1000) % 3600;
            int millisSecond = (int) millisMinute % 60;
            hourText.setText(Integer.toString((int) (millisUntilFinished/1000)/3600));
            minuteText.setText(Integer.toString(millisMinute/60));
            secondText.setText(Integer.toString(millisSecond));
        }

        @Override
        public void onFinish() {
            hourText.setCursorVisible(true);
            minuteText.setCursorVisible(true);
            secondText.setCursorVisible(true);
            hourText.setText(null);
            minuteText.setText(null);
            secondText.setText(null);
            isCountStart = false;
            canReset = false;
            firstStart = true;
            startButton.setText("Start");
        }
    }

    public int calculateTime(String hour, String minute, String second)
    {
        int int_hour = 0;
        int int_minute = 0;
        int int_second = 0;
        try{
            int_hour = Integer.parseInt(hour);
            throw new Exception(); //강제 에러 출력
        }catch (Exception e){
            //에러시 수행
            e.printStackTrace(); //오류 출력(방법은 여러가지)
        }

        try{
            int_minute = Integer.parseInt(minute);
            throw new Exception(); //강제 에러 출력
        }catch (Exception e){
            //에러시 수행
            e.printStackTrace(); //오류 출력(방법은 여러가지)
        }

        try{
            int_second = Integer.parseInt(second);
            throw new Exception(); //강제 에러 출력
        }catch (Exception e){
            //에러시 수행
            e.printStackTrace(); //오류 출력(방법은 여러가지)
        }

        int millisTime = ((int_hour * 60 *60) + (int_minute * 60) + int_second) * 1000;

        return millisTime;
    }
}
