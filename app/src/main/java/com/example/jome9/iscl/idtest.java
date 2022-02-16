package com.example.jome9.iscl;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;

/**
 * Created by jome9 on 2019-10-19.
 */

public class idtest extends Activity{
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.idtest);

        final EditText ID = (EditText) findViewById(R.id.userID);
        Button lego = (Button) findViewById(R.id.btn);

        lego.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                intent.putExtra("userID",ID.getText().toString());
                startActivity(intent);
            }
        });

    }
}
