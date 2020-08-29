package com.application.studyattendance;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.application.studyattendance.model.FineModel;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class FineReasonActivity extends Activity {

    private RecyclerView fineHistoryRecyclerView;
    List<FineModel> fineModels2 = new ArrayList<>();
    TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fine_reason);

        textView = (TextView) findViewById(R.id.fine_reason_textView2);

        Intent intent = getIntent();

        FirebaseDatabase.getInstance().getReference().child("allStudy").child(getIntent().getExtras().getString("studyK"))
                .child("studyUsers").child(getIntent().getExtras().getString("selectedUid"))
                .child("fine_history").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot item: snapshot.getChildren()) {
                    fineModels2.add(item.getValue(FineModel.class));
                    System.out.println("mainmainmain" + item.getValue(FineModel.class).fine_day);
                }

                if(fineModels2.size() > 0)
                {
                    textView.setVisibility(View.GONE);
                }
                else
                {
                    textView.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        fineHistoryRecyclerView = (RecyclerView) findViewById(R.id.fine_reason_recyclerView);
        fineHistoryRecyclerView.setAdapter(new FineHistoryRecyclerViewAdapter());
        fineHistoryRecyclerView.setLayoutManager(new LinearLayoutManager(this));

    }

    // 벌금내역 리사이클러뷰
    class FineHistoryRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>
    {
        private List<FineModel> fineModels = new ArrayList<>();

        public FineHistoryRecyclerViewAdapter() {

            FirebaseDatabase.getInstance().getReference().child("allStudy").child(getIntent().getExtras().getString("studyK"))
                    .child("studyUsers").child(getIntent().getExtras().getString("selectedUid"))
                    .child("fine_history").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    for (DataSnapshot item: snapshot.getChildren()) {
                        fineModels.add(item.getValue(FineModel.class));
                        System.out.println("mainmainmain" + item.getValue(FineModel.class).fine_day);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }

        @NonNull
        @Override // item_studyroom을 리사이클러뷰에 연결
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_fine_history,parent,false);
            FineReasonActivity.FineHistoryRecyclerViewAdapter.CustomViewHolder holder = new FineReasonActivity.FineHistoryRecyclerViewAdapter.CustomViewHolder(view);

            return holder;
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
            final FineReasonActivity.FineHistoryRecyclerViewAdapter.CustomViewHolder customViewHolder = (FineReasonActivity.FineHistoryRecyclerViewAdapter.CustomViewHolder) holder;

            customViewHolder.fineDay.setText(fineModels.get(position).fine_day);
            customViewHolder.fineReason.setText(fineModels.get(position).fine_reason);
        }

        @Override
        public int getItemCount() {
            return (fineModels != null ? fineModels.size() : 0);
        }

        private class CustomViewHolder extends RecyclerView.ViewHolder {
            public TextView fineDay;
            public TextView fineReason;

            public CustomViewHolder(View view) {
                super(view);
                fineDay = (TextView) view.findViewById(R.id.item_fine_history_fineDay);
                fineReason = (TextView) view.findViewById(R.id.item_fine_history_fineReason);
            }
        }
    }
}
