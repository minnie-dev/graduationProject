package com.example.jome9.iscl;


/**
 * Created by jome9 on 2019-05-14.
 */
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

public class LoginActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //강의에서 final을 추가시켜줌
        final EditText idText = (EditText)findViewById(R.id.idText);
        final EditText passwordText = (EditText)findViewById(R.id.passwordText);
        final Button loginbtn = (Button)findViewById(R.id.loginbtn);
        final TextView registerbtn = (TextView)findViewById(R.id.registerbtn);
        final CheckBox idSave=(CheckBox)findViewById(R.id.idSave);
        final CheckBox pwdSave=(CheckBox)findViewById(R.id.pwdSave);

        SharedPreferences pref=getSharedPreferences("pref", Activity.MODE_PRIVATE);
        String id=pref.getString("id_save", "");
        String pwd=pref.getString("pwd_save", "");
        Boolean chk1=pref.getBoolean("chk1", false);
        Boolean chk2=pref.getBoolean("chk2", false);

        if(chk1==true){
            idText.setText(id);
            idSave.setChecked(chk1);
        }
        if(chk2==true){
            passwordText.setText(pwd);
            pwdSave.setChecked(chk2);
        }

        registerbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent registerIntent = new Intent(LoginActivity.this, RegisterActivity.class);
                LoginActivity.this.startActivity(registerIntent);
            }
        });

        loginbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String userID = idText.getText().toString();
                final String userPassword = passwordText.getText().toString();


                //4. 콜백 처리부분(volley 사용을 위한 ResponseListener 구현 부분)
                Response.Listener<String> responseListener = new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try{
                            JSONObject jsonResponse = new JSONObject(response);
                            boolean success = jsonResponse.getBoolean("success");

                            //Toast.makeText(getApplicationContext(), "success "+success, Toast.LENGTH_SHORT).show();

                            //서버에서 보내준 값이 true이면?
                            if(success){
                                String userID = jsonResponse.getString("userID");
                                Toast.makeText(getApplicationContext(), userID + " 님 반갑습니다!", Toast.LENGTH_SHORT).show();

                                //로그인에 성공했으므로 MainActivity로 넘어감
                                SharedPreferences pref = getSharedPreferences("pref", Activity.MODE_PRIVATE);
                                SharedPreferences.Editor editor = pref.edit();

                                //SharedPreferences에 각 아이디를 지정하고 EditText 내용을 저장한다.
                                editor.putString("id_save", idText.getText().toString());
                                editor.putString("pwd_save", passwordText.getText().toString());
                                editor.putBoolean("chk1", idSave.isChecked());
                                editor.putBoolean("chk2", pwdSave.isChecked());
                                editor.commit();

                                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                                intent.putExtra("userID", userID);
                                LoginActivity.this.startActivity(intent);
                                finish();

                            }else{//로그인 실패시
                                AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
                                builder.setMessage("※ 아이디와 패스워드를 확인하세요! ※ ")
                                        .setNegativeButton("retry", null)
                                        .create()
                                        .show();


                            }

                        }catch(JSONException e){
                            e.printStackTrace();
                        }
                    }
                };

                LoginRequest loginRequest = new LoginRequest(userID, userPassword, responseListener);
                RequestQueue queue = Volley.newRequestQueue(LoginActivity.this);
                queue.add(loginRequest);
            }
        });

    }
}

