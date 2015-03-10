package com.yuchuan.privatecloudstorage.activity;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.beardedhen.androidbootstrap.BootstrapButton;
import com.beardedhen.androidbootstrap.BootstrapEditText;
import com.yuchuan.privatecloudstorage.R;

/**
 * Created by haroldmiao on 2015/2/26.
 */
public class RenameDialog extends Dialog {
    private Context context;
    private BootstrapEditText dir;
    private BootstrapButton btnok;
    private BootstrapButton btnCancle;
    private LeaveMyDialogListener listener;

    public interface LeaveMyDialogListener{
        public void onClick(View view,String dirD);
    }

    public RenameDialog(Context context) {
        super(context);
        // TODO Auto-generated constructor stub
        this.context = context;
    }

    public RenameDialog(Context context, LeaveMyDialogListener listener) {
        super(context);
        // TODO Auto-generated constructor stub
        this.context = context;
        this.listener = listener;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.i("RenameDialog", "onCreate");
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.activity_rename_dialog);

        btnok     = (BootstrapButton)findViewById(R.id.btn_ok);
        btnCancle = (BootstrapButton) findViewById(R.id.btn_cancle);
        dir       = (BootstrapEditText)findViewById(R.id.et_dir);

        btnok.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                listener.onClick(v, dir.getText().toString());
                RenameDialog.this.dismiss();
            }
        });

        btnCancle.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                //listener.onClick(v, dir.getText().toString());
                RenameDialog.this.dismiss();
            }
        });
    }

//    @Override
//    public void onClick(View v) {
//        // TODO Auto-generated method stub
//
//        listener.onClick(v, dir.getText().toString());
//        MkdirDialog.this.dismiss();
//    }
}