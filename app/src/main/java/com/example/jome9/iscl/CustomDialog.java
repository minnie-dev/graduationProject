package com.example.jome9.iscl;


import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import org.w3c.dom.Text;

/**
 * Created by kj.lee on 2017. 8. 31..
 */

public class CustomDialog extends Dialog
{
    CustomDialog m_oDialog;
    public CustomDialog(Context context)
    {
        super(context, android.R.style.Theme_Translucent_NoTitleBar);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        WindowManager.LayoutParams lpWindow = new WindowManager.LayoutParams();
        lpWindow.flags = WindowManager.LayoutParams.FLAG_DIM_BEHIND;
        lpWindow.dimAmount = 0.5f;
        getWindow().setAttributes(lpWindow);

//        setContentView(R.layout.custom_dialog);

        m_oDialog = this;

        ImageView oView = (ImageView) this.findViewById(R.id.textView);
        oView.setImageResource(R.drawable.aa);

        Button oBtn = (Button)this.findViewById(R.id.btnOK);
        oBtn.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                onClickBtn(v);
            }
        });
    }

    public void onClickBtn(View _oView)
    {
        this.dismiss();
    }
}