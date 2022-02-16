package com.example.jome9.iscl;

/**
 * Created by jome9 on 2019-04-30.
 */

import android.app.Activity;
import android.widget.Toast;

public class BackpressCloseHandler {
    private long backKeyClickTime = 0;
    private Activity activity;

    public BackpressCloseHandler(Activity activity) {
        this.activity = activity;
    }
    public void onBackPressed() {
        if (System.currentTimeMillis() > backKeyClickTime + 2000) {
            backKeyClickTime = System.currentTimeMillis();
            showToast();
            return;
        }
        if (System.currentTimeMillis() <= backKeyClickTime + 2000) {
            activity.finish();
        }
    }
    public void showToast() {
        Toast.makeText(activity, "뒤로 가기 버튼을 한 번 더 누르면 종료됩니다.", Toast.LENGTH_SHORT).show();
    }
}
