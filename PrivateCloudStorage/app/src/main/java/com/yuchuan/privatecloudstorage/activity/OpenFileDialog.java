package com.yuchuan.privatecloudstorage.activity;

import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.view.View;
import android.widget.ProgressBar;

import com.beardedhen.androidbootstrap.BootstrapButton;
import com.yuchuan.privatecloudstorage.R;

/**
 * Created by haroldmiao on 2015/3/12.
 */
public class OpenFileDialog extends Dialog {
    private Context context;
    private BootstrapButton cancel;
    private ProgressBar bar;
    private LeaveMyDialogListener listener;
    private boolean mCacheFileFinishRegistered;
    private CacheFileFinishReceiver mCacheFileFinishReceiver;
    private boolean mCacheFileProgressRegistered;
    private CacheFileProgressReceiver mCacheFileProgressReceiver;


    public interface LeaveMyDialogListener{
        public void onClick();
    }

    public OpenFileDialog(Context context) {
        super(context);
        // TODO Auto-generated constructor stub
        this.context = context;
    }

    public OpenFileDialog(Context context, LeaveMyDialogListener listener) {
        super(context);
        // TODO Auto-generated constructor stub
        this.context = context;
        this.listener = listener;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.open_file_dialog);
        initView();

        mCacheFileFinishReceiver = new CacheFileFinishReceiver();

        mCacheFileProgressReceiver = new CacheFileProgressReceiver();

        registerCacheFileFinishReceiver();
        registerCacheFileProgressReceiver();

        cancel.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                LocalBroadcastManager cancelCacheFile = LocalBroadcastManager.getInstance(context);
                Intent i = new Intent();
                i.setAction("CancleCacheFile");
                i.putExtra("cancleCacheFile", "ok");
                cancelCacheFile.sendBroadcast(i);
                unregisterCacheFileFinishReceiver();
                unregisterCacheFileProgressReceiver();
                //listener.onClick(v, dir.getText().toString());
                OpenFileDialog.this.dismiss();
            }
        });

        listener.onClick();
    }

//    @Override
//    protected void onStart() {
//        super.onStart();
//        registerCacheFileFinishReceiver();
//    }
//
    @Override
    protected void onStop() {
        super.onStop();
        unregisterCacheFileFinishReceiver();
    }

    private void initView() {
        cancel = (BootstrapButton) findViewById(R.id.btn_cancle);
        bar = (ProgressBar) findViewById(R.id.progressBar);
    }

    private void registerCacheFileProgressReceiver() {
        unregisterCacheFileProgressReceiver();
        IntentFilter intentToReceiveFilter = new IntentFilter();
        intentToReceiveFilter
                .addAction("CacheFileProgress");
        LocalBroadcastManager.getInstance(context).registerReceiver(
                mCacheFileProgressReceiver, intentToReceiveFilter);
        mCacheFileProgressRegistered = true;
    }

    private void unregisterCacheFileProgressReceiver() {
        if (mCacheFileProgressRegistered) {
            LocalBroadcastManager.getInstance(context).unregisterReceiver(
                    mCacheFileProgressReceiver);
            mCacheFileProgressRegistered = false;
        }
    }


    private void registerCacheFileFinishReceiver() {
        unregisterCacheFileFinishReceiver();
        IntentFilter intentToReceiveFilter = new IntentFilter();
        intentToReceiveFilter
                .addAction("CacheFileFinish");
        LocalBroadcastManager.getInstance(context).registerReceiver(
                mCacheFileFinishReceiver, intentToReceiveFilter);
        mCacheFileFinishRegistered = true;
    }

    private void unregisterCacheFileFinishReceiver() {
        if (mCacheFileFinishRegistered) {
            LocalBroadcastManager.getInstance(context).unregisterReceiver(
                    mCacheFileFinishReceiver);
            mCacheFileFinishRegistered = false;
        }
    }

    public class CacheFileFinishReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(
                    "CacheFileFinish")) {
                OpenFileDialog.this.dismiss();
            }
        }
    }

    public class CacheFileProgressReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(
                    "CacheFileProgress")) {
                int progress = intent.getIntExtra("Progress", 0);
                bar.setProgress(progress);

            }
        }
    }
}
