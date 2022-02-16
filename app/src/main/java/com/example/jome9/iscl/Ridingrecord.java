package com.example.jome9.iscl;

import android.app.Activity;
import android.content.Intent;
import android.media.Image;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Created by jome9 on 2019-04-12.
 */


public class Ridingrecord extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ridingrecord);

        ImageButton btnOpenDrawer = (ImageButton) findViewById(R.id.btn_mainmenu); // 드로어 화면을 열 버튼 객체 참조
        final DrawerLayout drawerLayout = (DrawerLayout) findViewById(R.id.drawerLayout); // 전체화면인 DrawerLayout 객체 참조
        final View drawerView = (View) findViewById(R.id.drawer); // Drawer 화면(뷰) 객체 참조
        LinearLayout logininfoBtn = (LinearLayout) findViewById(R.id.btn_logininfo);
        TextView logininformationText = (TextView) findViewById(R.id.logininformation);
        LinearLayout findwayBtn = (LinearLayout) findViewById(R.id.btn_findway);
        LinearLayout findbicycleBtn = (LinearLayout) findViewById(R.id.btn_findbicycle);
        LinearLayout weatherBtn = (LinearLayout) findViewById(R.id.btn_weather);
        LinearLayout ridingrecordBtn = (LinearLayout) findViewById(R.id.btn_ridingrecord);
        LinearLayout bluetoothBtn = (LinearLayout) findViewById(R.id.btn_bluetoothBtn);
        LinearLayout helpBtn = (LinearLayout) findViewById(R.id.btn_help);
        LinearLayout record = (LinearLayout) findViewById(R.id.record); // 기록

        Intent okok = getIntent();
        String day=okok.getStringExtra("날짜");
        String now= okok.getStringExtra("출발 시간");
        String arrive=okok.getStringExtra("도착 시간");
        String m_time = okok.getStringExtra("걸린 분")+" 분";
        String s_time = okok.getStringExtra("걸린 초")+" 초";
        String arrive11=okok.getStringExtra("거리")+" m";
        String avg = okok.getStringExtra("속도")+" km/h";


        TextView day1=(TextView)findViewById(R.id.day);
        day1.setText(day);
        TextView now1 =(TextView)findViewById(R.id.now);
        now1.setText(now);
        TextView arrive1 =(TextView)findViewById(R.id.arrive);
        arrive1.setText(arrive);
        TextView minute =(TextView)findViewById(R.id.m_time);
        minute.setText(m_time);
        TextView second =(TextView)findViewById(R.id.s_time);
        second.setText(s_time);
        TextView arrive111 =(TextView)findViewById(R.id.arrive11);
        arrive111.setText(arrive11);
        TextView speed1 =(TextView)findViewById(R.id.speed);
        speed1.setText(avg);

        Intent intent = getIntent();
        final String userID = intent.getStringExtra("userID");
        logininformationText.setText(userID);

        //메인메뉴버튼(드로어 열기)
        btnOpenDrawer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawerLayout.openDrawer(drawerView);
            }
        });//메인메뉴버튼끝
        findwayBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                intent.putExtra("userID",userID);
                startActivity(intent);
                finish();
            }
        });
        findbicycleBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), Findbicycle.class);
                intent.putExtra("userID",userID);
                startActivity(intent);
                finish();
            }
        });
        weatherBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), Weather.class);
                intent.putExtra("userID",userID);
                startActivity(intent);
                finish();
            }
        });
        ridingrecordBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                drawerLayout.closeDrawer(drawerView);
            }
        });
        helpBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), Help.class);
                intent.putExtra("userID",userID);
                startActivity(intent);
                finish();
            }
        });
        //달린기록으로 가는 버튼
        record.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(getApplicationContext(), RidingList.class);
                intent.putExtra("userID",userID);
                startActivity(intent);
                finish();
            }
        });

    }

    //두번누르면 종료
    private final long FINISH_INTERVAL_TIME = 2000;
    private long   backPressedTime = 0;
    @Override
    public void onBackPressed() {
        long tempTime = System.currentTimeMillis();
        long intervalTime = tempTime - backPressedTime;

        if (0 <= intervalTime && FINISH_INTERVAL_TIME >= intervalTime)
        {
            super.onBackPressed();
        }
        else
        {
            backPressedTime = tempTime;
            Toast.makeText(getApplicationContext(), "뒤로가기버튼을 한번 더 누르면 메인화면으로 이동합니다.", Toast.LENGTH_SHORT).show();
        }
    }

}



