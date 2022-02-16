package com.example.jome9.iscl;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;

/**
 * Created by jome9 on 2019-06-17.
 */

public class help_keepbicycle extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help_keepbicycle);

        ImageButton btn_back = (ImageButton) findViewById(R.id.back_btn);
        final LinearLayout btn_searchhelp = (LinearLayout) findViewById(R.id.searchhelp_btn);
        final LinearLayout btn_nearhelp = (LinearLayout) findViewById(R.id.nearhelp_btn);
        final LinearLayout p_searchhelp = (LinearLayout) findViewById(R.id.searchhelp);
        final LinearLayout p_nearhelp = (LinearLayout) findViewById(R.id.nearhelp);

        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        btn_nearhelp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                p_searchhelp.setVisibility(View.INVISIBLE);
                btn_searchhelp.setBackgroundResource(R.drawable.no_color_gray_round_shape);
                p_nearhelp.setVisibility(View.VISIBLE);
                btn_nearhelp.setBackgroundResource(R.drawable.gary_round_shape);
            }
        });
        btn_searchhelp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                p_searchhelp.setVisibility(View.VISIBLE);
                btn_searchhelp.setBackgroundResource(R.drawable.gary_round_shape);
                p_nearhelp.setVisibility(View.INVISIBLE);
                btn_nearhelp.setBackgroundResource(R.drawable.no_color_gray_round_shape);
            }
        });
        }
    }
