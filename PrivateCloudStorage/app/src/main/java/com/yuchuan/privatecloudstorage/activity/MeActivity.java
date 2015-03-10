package com.yuchuan.privatecloudstorage.activity;

import android.app.Activity;
import android.os.Bundle;
import android.os.PersistableBundle;

import com.yuchuan.privatecloudstorage.R;

/**
 * Created by haroldmiao on 2015/3/1.
 */
public class MeActivity extends Activity {

    @Override
    public void onCreate(Bundle savedInstanceState, PersistableBundle persistentState) {
        super.onCreate(savedInstanceState, persistentState);
        setContentView(R.layout.me);
    }
}
