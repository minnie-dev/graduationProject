


package com.example.jome9.iscl;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.View.OnClickListener;



/**
 * Created by alsgm on 2019-10-15.
 */

public class RidingList extends AppCompatActivity {
    final Context context = this;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ridinglist);

        ImageButton btn_back = (ImageButton) findViewById(R.id.back_btn);

        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        final TextView result = (TextView) findViewById(R.id.result);
        final DBHelper dbHelper = new DBHelper(getApplicationContext(), "Record.db", null, 1);
        result.setText(dbHelper.getResult());


        Button delete = (Button) findViewById(R.id.delete);
        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.delete:
                        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                                context);

                        // 제목셋팅
                        alertDialogBuilder.setTitle("DELETE");

                        // AlertDialog 셋팅
                        alertDialogBuilder
                                .setMessage("삭제된 자료는 복구가 불가능합니다. 전체 삭제하시겠습니까?")
                                .setCancelable(false)
                                .setNegativeButton("YES",
                                        new DialogInterface.OnClickListener() {
                                            public void onClick(
                                                    DialogInterface dialog, int id) {
                                                dbHelper.delete();
                                                //result.setText(dbHelper.getResult());
                                                result.setText("모두 삭제되었습니다.");
                                            }
                                        })
                                .setPositiveButton("NO",
                                        new DialogInterface.OnClickListener() {
                                            public void onClick(
                                                    DialogInterface dialog, int id) {
                                                // 다이얼로그를 취소한다
                                                dialog.cancel();
                                            }
                                        });

                        // 다이얼로그 생성
                        AlertDialog alertDialog = alertDialogBuilder.create();

                        // 다이얼로그 보여주기
                        alertDialog.show();
                        break;

                    default:
                        break;
                }
            }
        });
    }
}