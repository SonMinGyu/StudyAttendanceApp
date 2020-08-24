package fragment;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.application.studyattendance.GlideApp;
import com.application.studyattendance.R;
import com.application.studyattendance.StudyPropertisActivity;
import com.application.studyattendance.StudyRoomActivity;
import com.application.studyattendance.model.StudyModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

// searchStudy에서 초기에 모든 스터디 표시하는 fragment
public class searchStudyFragment1 extends Fragment {

    private static final String TAG = "mainmainmain";
    String bringStudyName;
    String bringStudyCategory;
    String searchString;
    TextView nullstudy;
    List<StudyModel> studyModels2 = new ArrayList<>();

    public View onCreateView(@NonNull final LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_study, container, false);

        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.studyfragment_recyclerview);
        recyclerView.setAdapter(new SearchStudyRecyclerViewAdapter());
        recyclerView.setLayoutManager(new LinearLayoutManager(inflater.getContext()));

        nullstudy = (TextView) view.findViewById(R.id.fragment_study_nulltext);
        String uid2 = FirebaseAuth.getInstance().getCurrentUser().getUid();
        FirebaseDatabase.getInstance().getReference().child("allStudy").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                studyModels2.clear();
                for(DataSnapshot item :dataSnapshot.getChildren())
                {
                    studyModels2.add(item.getValue(StudyModel.class));
                }

                Log.d(TAG,Integer.toString(studyModels2.size()));
                if(studyModels2.size() != 0)
                {
                    nullstudy.setVisibility(View.GONE);
                }
                else
                {
                    nullstudy.setText("스터디가 없습니다!\n새로운 스터디를 만들어보세요!");
                    nullstudy.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        return view;
    }


    class SearchStudyRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>
    {
        private List<StudyModel> studyModels = new ArrayList<>();
        private List<String> keyList = new ArrayList<>();
        private String uid;

        public SearchStudyRecyclerViewAdapter() {
            //uid = FirebaseAuth.getInstance().getCurrentUser().getUid();

            FirebaseDatabase.getInstance().getReference().child("allStudy").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    studyModels.clear();
                    keyList.clear();
                    for(DataSnapshot item :dataSnapshot.getChildren())
                    {
                        studyModels.add(item.getValue(StudyModel.class));
                        keyList.add(item.getValue(StudyModel.class).studyKey);
                    }
                    notifyDataSetChanged();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }

        @NonNull
        @Override // item_studyroom을 리사이클러뷰에 연결
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_studyroom,parent,false);
            searchStudyFragment1.SearchStudyRecyclerViewAdapter.CustomViewHolder holder = new searchStudyFragment1.SearchStudyRecyclerViewAdapter.CustomViewHolder(view);

            return holder;
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
            final searchStudyFragment1.SearchStudyRecyclerViewAdapter.CustomViewHolder customViewHolder = (searchStudyFragment1.SearchStudyRecyclerViewAdapter.CustomViewHolder) holder;
            //Picasso.get().load(studyModels.get(position).getProfile()).into(customViewHolder.imageView);
            GlideApp.with(customViewHolder.itemView)
                    .load(studyModels.get(position).getProfile())
                    .error(R.drawable.studyroom_logo)
                    .into(customViewHolder.imageView);
            //Log.d(TAG,studyModels.get(position).getProfile());

            customViewHolder.studyName.setText(studyModels.get(position).getStudyName());
            customViewHolder.category.setText(studyModels.get(position).getStudyCategory());
        }

        @Override
        public int getItemCount() {
            return (studyModels != null ? studyModels.size() : 0);
        }

        private class CustomViewHolder extends RecyclerView.ViewHolder {
            public ImageView imageView;
            public TextView studyName;
            public TextView category;

            public CustomViewHolder(View view) {
                super(view);
                imageView =(ImageView) view.findViewById(R.id.studyitem_imageview);
                studyName = (TextView) view.findViewById(R.id.studyitem_textview);
                category = (TextView) view.findViewById(R.id.studyitem_category);

                view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        int position = getAdapterPosition();
                        if(position != RecyclerView.NO_POSITION)
                        {
                            String studyKey = keyList.get(position);
                            Log.d(TAG, keyList.get(position));
                            Intent intent = new Intent(getActivity(), StudyPropertisActivity.class);
                            intent.putExtra("studykey", studyKey);
                            getActivity().startActivity(intent);
                            Toast.makeText(getContext(),"클릭",Toast.LENGTH_SHORT).show();
                        }

                    }
                });
                //for(int i = 0; i<studyModels.size(); i++) {
                // studyName.setText(studyModels.get(i).studyName);
                // category.setText(studyModels.get(i).studyCategory);
                //}
            }
        }
    }
}
