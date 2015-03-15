package com.yuchuan.privatecloudstorage.activity;

/**
 * Created by haroldmiao on 2015/3/9.
 */

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageButton;
import android.widget.Toast;

import com.beardedhen.androidbootstrap.BootstrapButton;
import com.beardedhen.androidbootstrap.BootstrapEditText;
import com.google.gson.Gson;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest;
import com.yuchuan.privatecloudstorage.R;
import com.yuchuan.privatecloudstorage.config.Config;
import com.yuchuan.privatecloudstorage.json.LoginData;

public class LoginActivity extends Activity {
    private BootstrapEditText userName, password;
    private CheckBox rem_pw, auto_login;
    private BootstrapButton  btn_login;
    private ImageButton btnQuit;
    private String userNameValue,passwordValue;
    private SharedPreferences sp;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //去除标题
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.login);

        //获得实例对象
        sp = this.getSharedPreferences("userInfo", Context.MODE_PRIVATE);
        userName = (BootstrapEditText) findViewById(R.id.et_zh);
        password = (BootstrapEditText) findViewById(R.id.et_mima);
        rem_pw = (CheckBox) findViewById(R.id.cb_mima);
        auto_login = (CheckBox) findViewById(R.id.cb_auto);
        btn_login = (BootstrapButton)findViewById(R.id.btn_login);
        btnQuit = (ImageButton)findViewById(R.id.img_btn);


        //判断记住密码多选框的状态
        if(sp.getBoolean("ISCHECK", false))
        {
            //设置默认是记录密码状态
            rem_pw.setChecked(true);
            userName.setText(sp.getString("USER_NAME", ""));
            password.setText(sp.getString("PASSWORD", ""));
            //判断自动登陆多选框状态
            if(sp.getBoolean("AUTO_ISCHECK", false))
            {
                //设置默认是自动登录状态
                auto_login.setChecked(true);
                //跳转界面
                Intent intent = new Intent(LoginActivity.this, LogoActivity.class);
                LoginActivity.this.startActivity(intent);
            }
        }

        btn_login.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                userNameValue = userName.getText().toString();
                passwordValue = password.getText().toString();

                //to server validate
                RequestParams params = new RequestParams();
//                params.addHeader("name", "value");
                params.addQueryStringParameter(Config.KEY_ACTION, Config.ACTION_LOGIN);

                params.addBodyParameter("user_name", userNameValue);
                params.addBodyParameter("password", passwordValue);

                HttpUtils http = new HttpUtils();
                http.send(HttpRequest.HttpMethod.POST,
                        Config.URL,
                        params,
                        new RequestCallBack<String>() {

                            @Override
                            public void onStart() {
                            }

                            @Override
                            public void onLoading(long total, long current, boolean isUploading) {
                                if (isUploading) {
                                } else {
                                }
                            }

                            @Override
                            public void onSuccess(ResponseInfo<String> responseInfo) {
                                Log.i("LoginActivity", responseInfo.result);

                                Gson gson = new Gson();
                                LoginData ld = gson.fromJson(responseInfo.result, LoginData.class);

                                if (ld.getStatus() == 0) {
                                    Toast.makeText(LoginActivity.this,"登录成功", Toast.LENGTH_SHORT).show();
                                    //登录成功和记住密码框为选中状态才保存用户信息
                                    if(rem_pw.isChecked())
                                    {
                                        //记住用户名、密码、
                                        Editor editor = sp.edit();
                                        editor.putString("USER_NAME", userNameValue);
                                        editor.putString("PASSWORD",passwordValue);
                                        editor.commit();
                                    }
                                    //跳转界面
                                    Intent intent = new Intent(LoginActivity.this, LogoActivity.class);
                                    LoginActivity.this.startActivity(intent);
                                    finish();
                                } else {

                                    Toast.makeText(LoginActivity.this,"用户名或密码错误，请重新登录", Toast.LENGTH_LONG).show();
                                }
                            }

                            @Override
                            public void onFailure(HttpException error, String msg) {
                            }
                        });
            }
        });

        //监听记住密码多选框按钮事件
        rem_pw.setOnCheckedChangeListener(new OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView,boolean isChecked) {
                if (rem_pw.isChecked()) {

                    System.out.println("记住密码已选中");
                    sp.edit().putBoolean("ISCHECK", true).commit();

                }else {

                    System.out.println("记住密码没有选中");
                    sp.edit().putBoolean("ISCHECK", false).commit();

                }

            }
        });

        //监听自动登录多选框事件
        auto_login.setOnCheckedChangeListener(new OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView,boolean isChecked) {
                if (auto_login.isChecked()) {
                    System.out.println("自动登录已选中");
                    sp.edit().putBoolean("AUTO_ISCHECK", true).commit();

                } else {
                    System.out.println("自动登录没有选中");
                    sp.edit().putBoolean("AUTO_ISCHECK", false).commit();
                }
            }
        });

        btnQuit.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }
}