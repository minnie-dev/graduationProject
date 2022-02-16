package com.example.jome9.iscl;

import android.app.Activity;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.skt.Tmap.TMapGpsManager;

import java.io.IOException;
import java.io.InputStream;

import jxl.Sheet;
import jxl.Workbook;
import jxl.read.biff.BiffException;

/**
 * Created by jome9 on 2019-04-12.
 */

public class Keepbicycle extends Activity {
    ListView list_excel;
    ArrayAdapter<String> arrayAdapter;
    EditText EditText1;
    ImageButton btn_start;
    Workbook workbook = null;
    Sheet sheet = null;
    int r[]=new int[1000];
    int i=0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_keepbicycle);

        list_excel = (ListView)findViewById(R.id.list_excel);
        arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1);
        EditText1 = (EditText)findViewById(R.id.EditText1);
        btn_start = (ImageButton)findViewById(R.id.btn_start);
        Excel();

        ImageButton btn_back = (ImageButton) findViewById(R.id.back_btn);

        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });


    }
    public void Excel() {
        btn_start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                try {
                    arrayAdapter.clear(); // 버튼 누를때 마다 저번 검색내용 삭제
                    InputStream inputStream = getBaseContext().getResources().getAssets().open("dd.xls");
                    workbook = Workbook.getWorkbook(inputStream);
                    sheet = workbook.getSheet(0);
                    String edt = EditText1.getText().toString();
                    int MaxColumn = 2, RowStart = 1, RowEnd = sheet.getColumn(MaxColumn - 1).length -1, ColumnStart = 0,
                            ColumnEnd = sheet.getRow(2).length - 1;

                    for(int row = RowStart; row <= RowEnd; row++) {
                        String excelload = sheet.getCell(ColumnStart, row).getContents(); // 자전거보관소명
                        String excelload1 = sheet.getCell(1, row).getContents(); // 소재지도로명주소
                        String excelload2 = sheet.getCell(2, row).getContents(); // 소재지지번주소
                        String excelload3 = sheet.getCell(3, row).getContents(); // 위도
                        String excelload4 = sheet.getCell(4, row).getContents(); // 경도

                        if(excelload1.contains(edt)) {//문자열에 검색하고자 하는 문자가 있는지 확인
                            arrayAdapter.add(excelload);
                            r[i]=row;
                            i++;
                        }
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                } catch (BiffException e) {
                    e.printStackTrace();
                } finally {
                    list_excel.setAdapter(arrayAdapter);
                    workbook.close();
                }
            }
        });

        list_excel.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getApplicationContext(),test.class);//다음 넘어갈 클래스 지정
                try {
                    InputStream inputStream = getBaseContext().getResources().getAssets().open("dd.xls");
                    workbook = Workbook.getWorkbook(inputStream);
                    sheet = workbook.getSheet(0);

                    String ExcelLat = sheet.getCell(3, r[position]).getContents(); // 위도값
                    String ExcelLon = sheet.getCell(4, r[position]).getContents(); // 경도값
                    String Name = sheet.getCell(0,r[position]).getContents();
                    intent.putExtra("ExcelLat", ExcelLat); //전환된 레이아웃에 ExcleLat값 전달
                    intent.putExtra("ExcelLon", ExcelLon); //전환된 레이아웃에 ExcleLon값 전달
                    intent.putExtra("name",Name);
                    startActivity(intent); // 레이아웃전환
                    finish();


                }

                catch (IOException e) {
                    e.printStackTrace();
                }
                catch (BiffException e) {
                    e.printStackTrace();
                }
            }
        });


    } // Excel()

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