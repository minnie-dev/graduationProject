package com.example.jome9.iscl;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by alsgm on 2019-10-14.
 */

public class DBHelper extends SQLiteOpenHelper{

    // DBHelper 생성자로 관리할 DB 이름과 버전 정보를 받음
    public DBHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE Record  (num INTEGER PRIMARY KEY AUTOINCREMENT, day TEXT, now TEXT, arrive TEXT, m_time TEXT, s_time TEXT, arrive11 TEXT, avg TEXT);");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
    public void insert(int num,String day, String now, String arrive, String m_time, String s_time, String arrive11, String avg) {
        // 읽고 쓰기가 가능하게 DB 열기
        SQLiteDatabase db = getWritableDatabase();
        // DB에 입력한 값으로 행 추가
        db.execSQL("INSERT INTO Record VALUES (NULL,'"+day+"','" + now + "', '" + arrive + "', '" + m_time + "', '"+s_time+"', '"+arrive11+"' , '"+avg+"');");
        db.close();
    }

    public void delete() {
        SQLiteDatabase db = getWritableDatabase();
        // 입력한 항목과 일치하는 행 삭제
        db.execSQL("DROP TABLE Record;");
        db.execSQL("CREATE TABLE Record (num INTEGER PRIMARY KEY AUTOINCREMENT, day TEXT, now TEXT, arrive TEXT, m_time TEXT, s_time TEXT, arrive11 TEXT, avg TEXT);");
        db.close();
    }

    public String getResult() {
        // 읽기가 가능하게 DB 열기
        SQLiteDatabase db = getReadableDatabase();
        String result="";


        // DB에 있는 데이터를 쉽게 처리하기 위해 Cursor를 사용하여 테이블에 있는 모든 데이터 출력
        Cursor cursor = db.rawQuery("SELECT * FROM Record", null);
        while (cursor.moveToNext()) {
            result +=" "
                    +cursor.getString(0)
                    +" . "
                    + "  날 짜  "
                    +cursor.getString(1)
                    + "\n"
                    //+ "         출발 시간  "
                    //+ cursor.getString(2)
                    //+ "\n"
                    // + "         도착 시간  "
                    //+ cursor.getString(3)
                    //+ "\n"
                    + "        걸린 시간  "
                    + cursor.getString(4)
                    + " 분 "
                    + cursor.getString(5)
                    + " 초 "
                    + "\n"
                    + "        거리  "
                    + cursor.getString(6)
                    +"m"
                    + "\n"
                    + "        속도  "
                    + cursor.getString(7)
                    + "km/h"
                    + "\n"
                    + "\n";

        }

        return result;
    }


}