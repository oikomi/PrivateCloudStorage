#AValidations android表单验证框架

![](/screenshots.gif)

> AValidations为库，可以直接导出jar包

> AValidationsDemo为演示，快速学习使用AValidations为库

## AValidations使用 ##

> 1.下载zip或者克隆AValidations项目

> 2.导入Eclipse，右键工程->preference->Android->library->Add,选择AValidations工程加入后 apply应用

> 3.继承ValidationExecutor写出自己的校验器：

    
    public class UserNameValidation extends ValidationExecutor {
    	public boolean doValidate(Context context, String text) {
    
    		String regex = "^[a-zA-Z](?=.*?[a-zA-Z])(?=.*?[0-9])[a-zA-Z0-9_]{7,11}$";
    		boolean result = Pattern.compile(regex).matcher(text).find();
    		if (!result) {
    			Toast.makeText(context, context.getString(R.string.e_username_hint), Toast.LENGTH_SHORT).show();
    			return false;
    		}
    		return true;
    	}
    }
    


> 4.使用`EditTextValidator`进行校验：
 

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


> 5.如果需要实现`没有填写表单时表单提交按钮不可点击效果` 需要设置`setButton(view)`和写Button背景的selector，如：


    <?xml version="1.0" encoding="utf-8"?>
    <selector xmlns:android="http://schemas.android.com/apk/res/android">
    
    <item android:drawable="@drawable/red_btn_normal" android:state_focused="true" android:state_pressed="false"/>
    <item android:drawable="@drawable/red_btn_selected" android:state_focused="true" android:state_pressed="true"/>
    <item android:drawable="@drawable/red_btn_selected" android:state_focused="false" android:state_pressed="true"/>
    <item android:drawable="@drawable/red_btn_disable" android:state_enabled="false"/>
    
    <item android:drawable="@drawable/red_btn_normal"/>
    
    </selector>





   Copyright 2014 ken.cai (http://www.shangpuyun.com)
 
   Licensed under the Apache License, Version 2.0 (the "License");
 	you may not use this file except in compliance with the License.
   You may obtain a copy of the License at
   http://www.apache.org/licenses/LICENSE-2.0
	Unless required by applicable law or agreed to in writing, software
 	distributed under the License is distributed on an "AS IS" BASIS,
 	WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 	See the License for the specific language governing permissions and
 	limitations under the License.
 
