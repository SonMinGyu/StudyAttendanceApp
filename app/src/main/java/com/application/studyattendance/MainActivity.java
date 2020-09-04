package com.application.studyattendance;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;
import com.application.studyattendance.model.StudyModel;
import com.application.studyattendance.model.UserModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "mainmainmain";
    private FirebaseAuth auth;
    private FirebaseAuth.AuthStateListener authStateListener;
    private FirebaseRemoteConfig fFirebaseRemoteConfig;

    private EditText idText;
    private EditText passwordText;
    private boolean phpLoginSuccess = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        auth = FirebaseAuth.getInstance();

        /* 자동로그인 처리하고싶음
        try{
            //에러가 발생할 수 있는 코드
                String loginedUserUid = auth.getCurrentUser().getUid();

                final UserModel createUserModel = new UserModel();

                FirebaseDatabase.getInstance().getReference().child("users").child(loginedUserUid).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        createUserModel.equals(dataSnapshot);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

                if (createUserModel.isAutoLoginChecked)
                {
                    loginEvent();
                } else
                    {
                    auth.signOut();
                }

                Log.d(TAG,"오류발생");
            throw new Exception(); //강제 에러 출력
        }catch (Exception e){
            //에러시 수행
            e.printStackTrace(); //오류 출력(방법은 여러가지)
        }

         */


        //auth.signOut(); // 자동로그인

        // statusBar 색깔적용을 위한 remoteConfig
        fFirebaseRemoteConfig = FirebaseRemoteConfig.getInstance();
        String splash_background = fFirebaseRemoteConfig.getString(getString(R.string.rc_color));
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
        {
            getWindow().setStatusBarColor(Color.parseColor(splash_background));
        }

        idText = (EditText) findViewById(R.id.idText);
        passwordText = (EditText) findViewById(R.id.passwordText);

        Button loginButton = (Button) findViewById(R.id.loginButton);
        Button registerButton = (Button) findViewById(R.id.registerButton);

        // php통신을 이용한 로그인
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) { // register 버튼 클릭시 register class로 넘어가는 기능
                String userID = idText.getText().toString();
                String userPassword = passwordText.getText().toString();

                Response.Listener<String> responseListener = new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            boolean success = jsonObject.getBoolean("success");
                            if(success) // 로그인에 성공한 경우
                            {
                                phpLoginSuccess = true;
                                String userID = jsonObject.getString("userID");
                                String userPassword = jsonObject.getString("userPassword");
                                //Toast.makeText(getApplicationContext(), "로그인에 성공하였습니다.",Toast.LENGTH_SHORT).show();

                                //loginSuccessintent.putExtra("userID", userID);
                                //loginSuccessintent.putExtra("userPassword", userPassword);

                                //if(phpLoginSuccess) {
                                    loginEvent(); // firebaseauth로 로그인
                                //}
                            }
                            else // 로그인에 실패한 경우
                            {
                                Toast.makeText(getApplicationContext(), "로그인에 실패하였습니다.",Toast.LENGTH_SHORT).show();
                                return;
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                };
                LoginRequest loginRequest = new LoginRequest(userID, userPassword, responseListener);
                RequestQueue queue = Volley.newRequestQueue(MainActivity.this);
                queue.add(loginRequest);
            }

        });

        /*
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loginEvent();
            }
        });
         */

        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) { // register 버튼 클릭시 register class로 넘어가는 기능
                Intent registerintent = new Intent(MainActivity.this,RegisterActivity.class); // register intent 객체 생성
                MainActivity.this.startActivity(registerintent); // register intent 활성화
                finish();
            }
        });

        authStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if(user != null) // 로그인
                {
                    Intent loginSuccessintent = new Intent(MainActivity.this, MyStudyActivity.class);
                    MainActivity.this.startActivity(loginSuccessintent);
                    finish();
                }
                else // 로그아웃
                {

                }
            }
        };
    }

    void loginEvent()
    {
        auth.signInWithEmailAndPassword(idText.getText().toString(), passwordText.getText().toString()).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(!task.isSuccessful()) // 로그인 실패한 부분
                {
                    Toast.makeText(getApplicationContext(), "로그인에 실패하였습니다.", Toast.LENGTH_SHORT).show();
                }
                /*
                else
                {
                    Map<String, Object> taskMap = new HashMap<String, Object>();
                    String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
                    UserModel userModel = new UserModel();
                    if(autoLogin.isChecked())
                    {
                        taskMap.put("isAutoLoginChecked", "true");
                    }
                    else
                    {
                        taskMap.put("isAutoLoginChecked", "false");
                    }
                    FirebaseDatabase.getInstance().getReference().child("users").child(uid).updateChildren(taskMap);
                }

                 */
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        auth.addAuthStateListener(authStateListener);
    }

    @Override
    protected void onStop() {
        super.onStop();
        auth.removeAuthStateListener(authStateListener);
    }
}