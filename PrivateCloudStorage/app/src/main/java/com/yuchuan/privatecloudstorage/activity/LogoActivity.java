package com.yuchuan.privatecloudstorage.activity;

/**
 * Created by haroldmiao on 2015/3/9.
 */

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.ProgressBar;

import com.yuchuan.privatecloudstorage.R;

public class LogoActivity extends Activity {
    private ProgressBar progressBar;
    private Button backButton;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 去除标题
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.logo);

        progressBar = (ProgressBar) findViewById(R.id.pgBar);
        backButton = (Button) findViewById(R.id.btn_back);
        final Handler handler=new Handler();

        final Runnable runnable=new Runnable(){
            @Override
            public void run() {
                // TODO Auto-generated method stub
                //要做的事情，这里再次调用此Runnable对象，以实现每两秒实现一次的定时器操作
                Intent intent = new Intent(LogoActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
                //handler.postDelayed(this, 2000);
            }
        };
//
//        Handler handler = new Handler() {
//            public void hanldMessage(Message msg) {
//                switch(msg.what) {
//                    case 0:
//                        Intent intent = new Intent(LogoActivity.this, MainActivity.class);
//                        startActivity(intent);
//                }
//            }
//        };
        handler.postDelayed(runnable, 2000);

//        Intent intent = new Intent(this, MainActivity.class);
//        LogoActivity.this.startActivity(intent);

        //finish();

        backButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                handler.removeCallbacks(runnable);
                finish();
            }
        });

    }
}