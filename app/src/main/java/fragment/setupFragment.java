package fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.application.studyattendance.MainActivity;
import com.application.studyattendance.R;
import com.application.studyattendance.StudyPropertisActivity;
import com.google.firebase.auth.FirebaseAuth;

public class setupFragment extends Fragment {

    Button logoutButton;

    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_setup, container, false);

        logoutButton = (Button) view.findViewById(R.id.fragment_setup_logoutButton);

        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseAuth.getInstance().signOut();

                Intent intent = new Intent(getActivity(), MainActivity.class);
                getActivity().startActivity(intent);

                getActivity().finish();

                /* // fragment종료?
                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                fragmentManager.beginTransaction().remove(setupFragment.this).commit();
                fragmentManager.popBackStack();

                 */

            }
        });

        return view;
    }
}
