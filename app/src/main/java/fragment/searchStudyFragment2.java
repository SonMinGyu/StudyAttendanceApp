package fragment;

import android.content.Context;
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

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.application.studyattendance.GlideApp;
import com.application.studyattendance.MainActivity;
import com.application.studyattendance.R;
import com.application.studyattendance.model.StudyModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.zip.Inflater;

// searchStudy에서 검색한 스터디 표시하는 fragment
public class searchStudyFragment2 extends searchStudyFragment{

    private static final String TAG = "mainmainmain";
    TextView nullstudy;
    List<StudyModel> studyModels2 = new ArrayList<>();
    String stSearchKeyword = key;

    public View onCreateView(@NonNull final LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_study, container, false);

        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.studyfragment_recyclerview);
        recyclerView.setAdapter(new SearchStudyRecyclerViewAdapter2());
        recyclerView.setLayoutManager(new LinearLayoutManager(inflater.getContext()));

        nullstudy = (TextView) view.findViewById(R.id.fragment_study_nulltext);
        String uid2 = FirebaseAuth.getInstance().getCurrentUser().getUid();
        FirebaseDatabase.getInstance().getReference().child("allStudy").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                studyModels2.clear();
                for(DataSnapshot item :dataSnapshot.getChildren())
                {
                    //System.out.println(item.getValue(StudyModel.class).studyName);
                    Log.d(TAG,item.getValue(StudyModel.class).studyName);
                    //System.out.println(stSearchKeyword);

                    if(item.getValue(StudyModel.class).studyName.contains(stSearchKeyword)
                            || item.getValue(StudyModel.class).studyCategory.contains(stSearchKeyword))
                    {
                        studyModels2.add(item.getValue(StudyModel.class));
                    }
                }

                if(studyModels2.size() != 0)
                {
                    nullstudy.setVisibility(View.GONE);
                }
                else
                {
                    nullstudy.setText("검색하신 스터디가 없습니다.");
                    nullstudy.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        return view;
    }

    class SearchStudyRecyclerViewAdapter2 extends RecyclerView.Adapter<RecyclerView.ViewHolder>
    {
        /*
        LayoutInflater layoutInflater = (LayoutInflater)getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = layoutInflater.inflate(R.layout.fragment_search_study, null);
        EditText searchKeyword = (EditText)view.findViewById(R.id.search_study_edittext);
        String stSearchKeyword = searchKeyword.getText().toString();
         */

        private List<StudyModel> searchStudyModels = new ArrayList<>();
        private String uid;

        public SearchStudyRecyclerViewAdapter2() {
            uid = FirebaseAuth.getInstance().getCurrentUser().getUid();

            FirebaseDatabase.getInstance().getReference().child("allStudy").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    //allStudyModels.clear();
                    searchStudyModels.clear();

                    for(DataSnapshot item :dataSnapshot.getChildren())
                    {
                        //System.out.println(item.getValue(StudyModel.class).studyName);
                        Log.d(TAG,item.getValue(StudyModel.class).studyName);
                        //System.out.println(stSearchKeyword);

                        if(item.getValue(StudyModel.class).studyName.contains(stSearchKeyword)
                                || item.getValue(StudyModel.class).studyCategory.contains(stSearchKeyword))
                        {
                            searchStudyModels.add(item.getValue(StudyModel.class));
                        }
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
            searchStudyFragment2.SearchStudyRecyclerViewAdapter2.CustomViewHolder holder = new searchStudyFragment2.SearchStudyRecyclerViewAdapter2.CustomViewHolder(view);

            return holder;
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
            final searchStudyFragment2.SearchStudyRecyclerViewAdapter2.CustomViewHolder customViewHolder = (searchStudyFragment2.SearchStudyRecyclerViewAdapter2.CustomViewHolder) holder;
            //Picasso.get().load(studyModels.get(position).getProfile()).into(customViewHolder.imageView);
            GlideApp.with(customViewHolder.itemView)
                    .load(searchStudyModels.get(position).getProfile())
                    .error(R.drawable.studyroom_logo)
                    .into(customViewHolder.imageView);
            //Log.d(TAG,studyModels.get(position).getProfile());

            customViewHolder.studyName.setText(searchStudyModels.get(position).getStudyName());
            customViewHolder.category.setText(searchStudyModels.get(position).getStudyCategory());
        }

        @Override
        public int getItemCount() {
            return (searchStudyModels != null ? searchStudyModels.size() : 0);
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

                //for(int i = 0; i<studyModels.size(); i++) {
                // studyName.setText(studyModels.get(i).studyName);
                // category.setText(studyModels.get(i).studyCategory);
                //}
            }
        }
    }

}
