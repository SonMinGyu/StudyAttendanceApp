package com.application.studyattendance;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.util.Log;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;
import com.application.studyattendance.model.UserModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegisterActivity extends AppCompatActivity {

    private static final String TAG = "mainmainmain";
    private boolean phpRegisterSuccess = false;
    boolean validateID = false;
    FirebaseAuth firebaseAuth;
    DatabaseReference reference;

    @Override
    protected void onCreate(Bundle savedInstanceState) { // 액티비티 시작시 처음으로 실행
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // statusBar 색깔적용을 위한 remoteConfig
        FirebaseRemoteConfig mFirebaseRemoteConfig = FirebaseRemoteConfig.getInstance();
        String splash_background = mFirebaseRemoteConfig.getString(getString(R.string.rc_color));
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
        {
            getWindow().setStatusBarColor(Color.parseColor(splash_background));
        }

        // 아이디값 찾아주기
        final EditText idText = (EditText) findViewById(R.id.idText);
        final EditText passwordText = (EditText) findViewById(R.id.passwordText);
        final EditText nameText = (EditText) findViewById(R.id.nameText);
        final EditText majorText = (EditText) findViewById(R.id.majorText);
        final EditText ageText = (EditText) findViewById(R.id.ageText);

        Button validateButton = (Button) findViewById(R.id.validateButton);
        Button registerButton = (Button) findViewById(R.id.registerButton);

        // 버튼에 칼라 적용
        validateButton.setBackgroundColor(Color.parseColor(splash_background));
        registerButton.setBackgroundColor(Color.parseColor(splash_background));

        validateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String userID = idText.getText().toString();
                if(userID.equals(""))
                {
                    Toast.makeText(getApplicationContext(), "ID는 빈칸일 수 없습니다.", Toast.LENGTH_SHORT).show();
                    return;
                }
                if(!(isEmail(userID)))
                {
                    Toast.makeText(getApplicationContext(), "잘못된 이메일 형식입니다.", Toast.LENGTH_SHORT).show();
                    return;
                }
                Response.Listener<String> responseListener = new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonResopnse = new JSONObject(response);
                            boolean success = jsonResopnse.getBoolean("success");
                            if(success)
                            {
                                Toast.makeText(getApplicationContext(), "사용할 수 있는 아이디 입니다.", Toast.LENGTH_SHORT).show();
                                validateID = true;
                            }
                            else
                            {
                                Toast.makeText(getApplicationContext(), "사용할 수 없는 아이디 입니다.", Toast.LENGTH_SHORT).show();
                                validateID = false;
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                };

                ValidateRequest validateRequest = new ValidateRequest(userID, responseListener);
                RequestQueue queue = Volley.newRequestQueue(RegisterActivity.this);
                queue.add(validateRequest);
            }
        });

        // 회원가입 버튼 클릭시 실행
        // 입력되어 있는 값 가져오기
        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String userID = idText.getText().toString();
                final String userPassword = passwordText.getText().toString();
                final String userName = nameText.getText().toString();
                final String userMajor = majorText.getText().toString();
                final int userAge;
                //final int userAge = Integer.parseInt(ageText.getText().toString());

                if((userID.equals("")) || (userPassword.equals("")) || (userName.equals("")) || (userMajor.equals("")))
                {
                    Toast.makeText(getApplicationContext(), "빈칸을 모두 채워주세요!", Toast.LENGTH_SHORT).show();
                    return;
                }

                try {
                    String str = ageText.getText().toString().trim();
                    userAge = Integer.parseInt(str);
                } catch(NumberFormatException e){
                    Toast.makeText(getApplicationContext(), "나이 칸을 채우거나 숫자만 입력하세요", Toast.LENGTH_SHORT).show();
                    return;
                }

                if(!validateID)
                {
                    Toast.makeText(getApplicationContext(), "회원가입에 실패하였습니다. 아이디 중복확인 해주세요!",Toast.LENGTH_SHORT).show();
                    return;
                }

                if((userPassword.length() < 6))
                {
                    Toast.makeText(getApplicationContext(), "비밀번호는 6자리 이상으로 해주세요!",Toast.LENGTH_SHORT).show();
                    return;
                }

                ////////////////////////////////////////
                // php통신을 이용한 회원가입
                Response.Listener<String> responseListener = new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            boolean success = jsonObject.getBoolean("success");
                            if(success)
                            {
                                phpRegisterSuccess = true;
                                //Toast.makeText(getApplicationContext(), "회원가입에 성공하였습니다.",Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                };
                RegisterRequest registerRequest = new RegisterRequest(userID, userPassword, userName, userMajor, userAge, responseListener);
                RequestQueue queue = Volley.newRequestQueue(RegisterActivity.this);
                queue.add(registerRequest); // volley를 이용해 서버로 요청을 함

                /////////////////////////////////////////////

                // firebase auth에 회원내용 생성
                FirebaseAuth.getInstance().createUserWithEmailAndPassword(idText.getText().toString(),
                        passwordText.getText().toString()).addOnCompleteListener(RegisterActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(phpRegisterSuccess) {
                            Toast.makeText(getApplicationContext(), "회원가입에 성공하였습니다.", Toast.LENGTH_SHORT).show();
                            // firebase realtime db에 uid를 통해서 데이터 넣기
                            String uid = task.getResult().getUser().getUid();
                            //String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
                            UserModel userModel = new UserModel();
                            userModel.userName = nameText.getText().toString();
                            userModel.userUid = firebaseAuth.getInstance().getCurrentUser().getUid();
                            FirebaseDatabase.getInstance().getReference().child("users").child(uid).setValue(userModel);

                            Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
                            RegisterActivity.this.startActivity(intent);
                            finish();
                            //startActivity(intent);
                        }
                    }
                });
            }

        });
    }

    // 올바른 email 형식인지 체크하는 메소드
    public static boolean isEmail(String email){
        boolean returnValue = false;
        String regex = "^[_a-zA-Z0-9-\\.]+@[\\.a-zA-Z0-9-]+\\.[a-zA-Z]+$";
        Pattern p = Pattern.compile(regex);
        Matcher m = p.matcher(email);
        if(m.matches()){
            returnValue = true;
        }
        return returnValue;
    }
}