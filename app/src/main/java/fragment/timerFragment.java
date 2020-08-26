package fragment;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.application.studyattendance.R;
import com.application.studyattendance.StudyRoomActivity;
import com.application.studyattendance.model.StudyModel;
import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class timerFragment extends Fragment {
    private static final String TAG = "mainmainmain";

    // 스톱워치 구간기록 기능 구현
    TextView stopwatchTextview;

    Button startAndRestartButton, resetAndTimeLapButton;
    public static final int INIT = 0;//처음
    public static final int RUNNING = 1;//실행중
    public static final int PAUSING = 2;//정지
    int count = 0; // 리사이클러뷰 스크롤 하기위한 변수

    //상태값을 저장하는 변수
    // INIT은 초기값, status 안에 INIT(0)넣어서 초기상태로 셋팅
    public static int status = INIT;

    private long baseTime,pauseTime;

    TimeLapRecyclerViewAdapter timeLapRecyclerViewAdapter;
    RecyclerView timerlapRecyclerView;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_timer, container, false);

        stopwatchTextview = (TextView) view.findViewById(R.id.fragment_timer_timer_text);
        startAndRestartButton = (Button)view.findViewById(R.id.fragment_timer_startAndRestartButton);
        resetAndTimeLapButton = (Button)view.findViewById(R.id.fragment_timer_resetAndTimeLapButton);

        timerlapRecyclerView = (RecyclerView) view.findViewById(R.id.fragment_timer_recyclerView);
        timeLapRecyclerViewAdapter = new TimeLapRecyclerViewAdapter();
        timerlapRecyclerView.setAdapter(timeLapRecyclerViewAdapter);
        timerlapRecyclerView.setLayoutManager(new LinearLayoutManager(inflater.getContext()));

        resetAndTimeLapButton.setEnabled(false);

        startAndRestartButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pressStartAndRestartButton();
            }
        });

        resetAndTimeLapButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pressResetAndTimeLapButton();
            }
        });

        return view;
    }

    Handler handler = new Handler()
    {
        @Override
        public void handleMessage(@NonNull Message msg) {

            stopwatchTextview.setText(getTime());

            handler.sendEmptyMessage(0);
        }
    };

    private void pressStartAndRestartButton() {
        switch (status){
            case INIT:
                //어플리케이션이 실행되고 나서 실제로 경과된 시간...
                baseTime = SystemClock.elapsedRealtime();

                //핸들러 실행
                handler.sendEmptyMessage(0);
                startAndRestartButton.setText("PAUSE");
                resetAndTimeLapButton.setText("TIME LAP");
                resetAndTimeLapButton.setEnabled(true);

                //상태 변환
                status = RUNNING;
                break;
            case RUNNING: // 돌아가는 상태에서 왼쪽버튼 눌렀을때 작동
                //핸들러 정지
                handler.removeMessages(0);

                //정지 시간 체크
                pauseTime = SystemClock.elapsedRealtime();

                startAndRestartButton.setText("RESTART");
                resetAndTimeLapButton.setText("RESET");
                resetAndTimeLapButton.setEnabled(true);

                //상태변환
                status = PAUSING;
                break;

            case PAUSING: // 중지된 상태에서 왼쪽버튼 눌렀을때 작동
                long reStart = SystemClock.elapsedRealtime();
                baseTime += (reStart - pauseTime);

                handler.sendEmptyMessage(0);

                startAndRestartButton.setText("PAUSE");
                resetAndTimeLapButton.setText("TIME LAP");
                resetAndTimeLapButton.setEnabled(true);

                status = RUNNING;
        }

    }


    private void pressResetAndTimeLapButton(){
        switch (status){
            case RUNNING:
                if(count <= 0)
                {
                    timeLapRecyclerViewAdapter.addItem("first");
                }
                timeLapRecyclerViewAdapter.addItem(stopwatchTextview.getText().toString());
                timeLapRecyclerViewAdapter.notifyDataSetChanged();
                timerlapRecyclerView.scrollToPosition(count);

                break;

            case PAUSING:
                startAndRestartButton.setText("START");
                resetAndTimeLapButton.setText("RESET");
                resetAndTimeLapButton.setEnabled(false);

                stopwatchTextview.setText("00:00:00");
                timeLapRecyclerViewAdapter.clear();
                count = 0;

                baseTime = 0;
                pauseTime = 0;

                status = INIT;
        }
    }

    private String getTime(){
        //경과된 시간 체크

        long nowTime = SystemClock.elapsedRealtime();
        //시스템이 부팅된 이후의 시간?
        long overTime = nowTime - baseTime;

        long hour = (overTime/1000)/3600;
        long m = ((overTime/1000)%3600)/60;
        long s = (overTime/1000%3600)%60;

        String recTime = String.format("%02d:%02d:%02d",hour,m,s);

        return recTime;
    }

    class TimeLapRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>
    {
        List<String> bringTimeLaps = new ArrayList<>();

        // 아이템 추가함수
        void addItem(String timelap) {
            bringTimeLaps.add(timelap);
            count++;
        }

        void clear()
        {
            bringTimeLaps.clear();
        }

        @NonNull
        @Override // item_studyroom을 리사이클러뷰에 연결
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view;
            switch (viewType)
            {
                case 0:
                    view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_timer_lap_first, parent, false);
                    timerFragment.TimeLapRecyclerViewAdapter.CustomViewHolder1 holder1 = new timerFragment.TimeLapRecyclerViewAdapter.CustomViewHolder1(view);
                    return holder1;

                case 1:
                    view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_timer_lap, parent, false);
                    timerFragment.TimeLapRecyclerViewAdapter.CustomViewHolder holder = new timerFragment.TimeLapRecyclerViewAdapter.CustomViewHolder(view);

                    return holder;
            }

            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_timer_lap, parent, false);
            timerFragment.TimeLapRecyclerViewAdapter.CustomViewHolder holder = new timerFragment.TimeLapRecyclerViewAdapter.CustomViewHolder(view);

            return holder;
        }

        @Override
        public int getItemViewType(int position) {
            if(bringTimeLaps.get(position).equals("first"))
            {
                return 0;
            }
            else
            {
                return 1;
            }
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
            if(bringTimeLaps.get(position).equals("first"))
            {
                ;
            }
            else {
                final timerFragment.TimeLapRecyclerViewAdapter.CustomViewHolder customViewHolder = (timerFragment.TimeLapRecyclerViewAdapter.CustomViewHolder) holder;

                customViewHolder.numberText.setText(Integer.toString(position));
                customViewHolder.timelapText.setText(bringTimeLaps.get(position));
            }
        }

        @Override
        public int getItemCount() {
            return (bringTimeLaps != null ? bringTimeLaps.size() : 1);
        }

        private class CustomViewHolder extends RecyclerView.ViewHolder {
            public TextView numberText;
            public TextView timelapText;

            public CustomViewHolder(View view) {
                super(view);
                numberText = (TextView) view.findViewById(R.id.item_timer_lap_numberText);
                timelapText = (TextView) view.findViewById(R.id.item_timer_lap_timeText);
            }
        }

        private class CustomViewHolder1 extends RecyclerView.ViewHolder {

            public CustomViewHolder1(View view) {
                super(view);
            }
        }
    }
}
