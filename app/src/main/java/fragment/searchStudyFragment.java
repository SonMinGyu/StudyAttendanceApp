package fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.application.studyattendance.R;

public class searchStudyFragment extends Fragment {

    private static final String TAG = "mainmainmain";
    public static String key;
    EditText searchStudyEdittext;
    Button searchStudyButton;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_search_study, container, false);

        getFragmentManager().beginTransaction().replace(R.id.search_study_framelayout, new searchStudyFragment1()).commit();

        searchStudyEdittext = (EditText)view.findViewById(R.id.search_study_edittext);
        searchStudyButton = (Button) view.findViewById(R.id.search_study_botton);

        searchStudyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                /*
                Log.d(TAG,searchStudyEdittext.getText().toString());

                Fragment searchStudyFragment = new searchStudyFragment2();
                Bundle bundle = new Bundle();
                bundle.putString("text",searchStudyEdittext.getText().toString());
                searchStudyFragment.setArguments(bundle);
                 */

                key = searchStudyEdittext.getText().toString();

                Fragment myStudyFragment = getFragmentManager().findFragmentById(R.id.search_study_framelayout);
                getFragmentManager().beginTransaction().replace(R.id.search_study_framelayout, new searchStudyFragment2()).commit();

                /*
                RecyclerView recyclerView2 = (RecyclerView) view.findViewById(R.id.search_study_recyclerview);
                recyclerView2.setAdapter(new SearchStudyRecyclerViewAdapter2());
                recyclerView2.setLayoutManager(new LinearLayoutManager(inflater.getContext()));

                 */

            }
        });

        return view;
    }
}
