/**
 *  Copyright 2014 ken.cai (http://www.shangpuyun.com)
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *	you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 *	Unless required by applicable law or agreed to in writing, software
 *	distributed under the License is distributed on an "AS IS" BASIS,
 *	WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *	See the License for the specific language governing permissions and
 *	limitations under the License.
 *
 */
package com.cndemoz.avalidations.demo;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.cndemoz.avalidations.EditTextValidator;
import com.cndemoz.avalidations.ValidationModel;
import com.cndemoz.avalidations.validations.PasswordValidation;
import com.cndemoz.avalidations.validations.UserNameValidation;

/**
 * 登陆校验演示
 * @Description: 
 * @author ken.cai
 * @date 2014-11-21 下午9:42:53 
 * @version V1.0   
 * 
 */
public class LoginActivity extends Activity implements OnClickListener{
    private EditText usernameEditText;
    private EditText passwordEditText;
    private Button loginButton;
    private EditTextValidator editTextValidator;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
	super.onCreate(savedInstanceState);
	setContentView(R.layout.activity_login);

	usernameEditText = (EditText) findViewById(R.id.login_username_edittext);
	passwordEditText = (EditText) findViewById(R.id.login_password_edittext);
	loginButton = (Button) findViewById(R.id.login_button);

	loginButton.setOnClickListener(this);
	
	editTextValidator = new EditTextValidator(this)
		.setButton(loginButton)
		.add(new ValidationModel(usernameEditText,new UserNameValidation()))
		.add(new ValidationModel(passwordEditText,new PasswordValidation()))
		.execute();

    }

    @Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.login_button:

			if (editTextValidator.validate()) {
				Toast.makeText(this, "通过校验", Toast.LENGTH_SHORT).show();
			}
			break;
		}
	}


}
