package com.yuchuan.privatecloudstorage.activity;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.gson.Gson;
import com.yuchuan.privatecloudstorage.R;
import com.yuchuan.privatecloudstorage.json.StorageInfo;
import com.yuchuan.privatecloudstorage.net.GetStorageInfo;

/**
 * Created by haroldmiao on 2015/3/11.
 */
public class MeActivity extends Activity {
    private TextView name;
    private TextView used;
    public ProgressBar bar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.me);

        initView();
        getStorageInfo();
    }

    private void initView() {
        name = (TextView) findViewById(R.id.tv_name);
        used = (TextView) findViewById(R.id.tv_used);
        bar = (ProgressBar) findViewById(R.id.pb_storage);
    }

    private void getStorageInfo() {
        new GetStorageInfo("kkk", new GetStorageInfo.SuccessCallback() {
            @Override
            public void onSuccess(String result) {
                Log.i("MainActivity", "onSuccess");
                if (result.equals("{}")) {

                } else {
                    Gson gson = new Gson();
                    StorageInfo si = gson.fromJson(result, StorageInfo.class);
                    used.setText("已使用" + si.getUsed() + "M" + "  总共： " + si.getAll() + "M");
                    bar.setMax(Integer.parseInt(si.getAll()));
                    bar.setProgress(Integer.parseInt(si.getUsed()));
                }
            }
        }, new GetStorageInfo.FailCallback() {
            @Override
            public void onFail() {
                Log.i("MainActivity", "onFail");
            }
        });

    }
}
