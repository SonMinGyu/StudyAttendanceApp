package fragment;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.application.studyattendance.GlideApp;
import com.application.studyattendance.MyStudyActivity;
import com.application.studyattendance.R;
import com.application.studyattendance.StudyRoomActivity;
import com.application.studyattendance.createStudyActivity;
import com.application.studyattendance.model.StudyModel;
import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

public class studyFragment extends Fragment {

    private static final String TAG = "mainmainmain";
    String bringStudyName;
    String bringStudyCategory;
    TextView nullstudy;
    List<StudyModel> studyModels2 = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_study, container, false);

        final RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.studyfragment_recyclerview);
        recyclerView.setAdapter(new StudyRecyclerViewAdapter());
        recyclerView.setLayoutManager(new LinearLayoutManager(inflater.getContext()));

        nullstudy = (TextView) view.findViewById(R.id.fragment_study_nulltext);
        String uid2 = FirebaseAuth.getInstance().getCurrentUser().getUid();
        FirebaseDatabase.getInstance().getReference().child("study").child(uid2).addValueEventListener(new ValueEventListener() {
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
                    nullstudy.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        return view;
    }

    class StudyRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>
    {
        private List<StudyModel> studyModels = new ArrayList<>();
        private ArrayList<String> keyList = new ArrayList<>();
        private String uid;

        public StudyRecyclerViewAdapter() {
            uid = FirebaseAuth.getInstance().getCurrentUser().getUid();

            FirebaseDatabase.getInstance().getReference().child("study").child(uid).addValueEventListener(new ValueEventListener() {
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
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_studyroom, parent, false);

            CustomViewHolder holder = new CustomViewHolder(view);

            return holder;
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
            final CustomViewHolder customViewHolder = (CustomViewHolder) holder;


            //Log.d(TAG,studyModels.get(position).getStudyKey());

            //Picasso.get().load(studyModels.get(position).getProfile()).into(customViewHolder.imageView);

            Glide.with(customViewHolder.itemView.getContext())
                    .load(studyModels.get(position).getProfile())
                    .error(R.drawable.studyroom_logo)
                    .into(((CustomViewHolder)holder).imageView);
            //Log.d(TAG,studyModels.get(position).getProfile());

            customViewHolder.studyName.setText(studyModels.get(position).getStudyName());
            customViewHolder.category.setText(studyModels.get(position).getStudyCategory());
        }

        @Override
        public int getItemCount() {
            return (studyModels != null ? studyModels.size() : 1);
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
                                                    Intent intent = new Intent(getActivity(), StudyRoomActivity.class);
                                                    intent.putExtra("studykey", studyKey);
                                                    getActivity().startActivity(intent);
                                                    //Toast.makeText(getContext(),"클릭",Toast.LENGTH_SHORT).show();
                                                }

                                            }
                                        });

            }
        }
    }
}
