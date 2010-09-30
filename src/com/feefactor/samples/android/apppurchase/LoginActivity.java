/*-
 * Copyright (c) 2010, NETMOBO LLC
 * All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without 
 * modification, are permitted provided that the following conditions are met:
 * 
 *     i.   Redistributions of source code must retain the above copyright 
 *          notice, this list of conditions and the following disclaimer.
 *     ii.  Redistributions in binary form must reproduce the above copyright 
 *          notice, this list of conditions and the following disclaimer in the 
 *          documentation and/or other materials provided with the 
 *          distribution.
 *     iii. Neither the name of NETMOBO LLC nor the names of its contributors 
 *          may be used to endorse or promote products derived from this 
 *          software without specific prior written permission.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" 
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE 
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE 
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE 
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR 
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF 
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS 
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN 
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) 
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE 
 * POSSIBILITY OF SUCH DAMAGE.
 */
package com.feefactor.samples.android.apppurchase;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;

import com.feefactor.samples.android.ProgressableRunnable;
import com.feefactor.samples.android.ProgressableTask;

/**
 * @author netmobo
 */
public class LoginActivity extends BaseActivity {
	private static final String TAG = "LOGINACTIVITY";
	
	public static final String PREFS_NAME = "MyPrefsFile";
	private static final String PREF_USERNAME = "username";
	private static final String PREF_PASSWORD = "password";
	
	private CheckBox rememberPassword;
	private EditText usernameEt;
	private EditText passwordEt;
	private Button loginBtn;
	private Button signupBtn;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.login);
		initDisplay();
	}

	protected void initDisplay() {

		usernameEt = (EditText) findViewById(R.id.login_username);
		passwordEt = (EditText) findViewById(R.id.login_password);

		String username = "";
		String password = "";

		if (QuickstartApplication.testMode) {
			username = (String) getString(R.string.username);
			password = (String) getString(R.string.password);
		}else {
			username = getSharedPreferences(PREFS_NAME,MODE_PRIVATE).getString(PREF_USERNAME, "");
			password = getSharedPreferences(PREFS_NAME,MODE_PRIVATE).getString(PREF_PASSWORD, "");
		}
		usernameEt.setText(username);
		passwordEt.setText(password);
		
		rememberPassword = (CheckBox) findViewById(R.id.login_remember_password);

		loginBtn = (Button) findViewById(R.id.login_login_button);
		signupBtn = (Button) findViewById(R.id.login_signup_button);
		// Set Click Listener
		loginBtn.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				loginClicked();
			}
		});
		signupBtn.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				signupClicked();
			}
		});

	}

	private void signupClicked() {
		Intent intent = new Intent(this, SignUpActivity.class);
		startActivity(intent);
	}

	public void loginClicked() {
		ProgressableRunnable runnable = new ProgressableRunnable() {
			public void run() {
				login();
			}

			public void onCancel() {
			}
		};

		ProgressableTask task = new ProgressableTask(this, runnable,
				R.string.logging_in);
		task.start();
	}
	
	public void gotoViewAccountBalance() {
		Intent intent  = new Intent(this, ViewAccountBalanceActivity.class);
		startActivity(intent);
	}

	public void login() {
		try {
			String username;
			String password;

			username = usernameEt.getText().toString();
			password = passwordEt.getText().toString();

			if (rememberPassword.isChecked()) {
				// save to preferences
				getSharedPreferences(PREFS_NAME,MODE_PRIVATE)
		        .edit()
		        .putString(PREF_USERNAME, username)
		        .putString(PREF_PASSWORD, password)
		        .commit();
			} else {
				getSharedPreferences(PREFS_NAME,MODE_PRIVATE)
		        .edit()
		        .putString(PREF_USERNAME, "")
		        .putString(PREF_PASSWORD, "")
		        .commit();
			}
			

			QuickstartApplication qsApp = (QuickstartApplication) getApplication();
			
			// at this point we log-in the user
			Log.d(TAG, "Log in...");
			qsApp.login(username, password);
			Log.d(TAG, "Logged in!");
			sendMessage(SUCCESS);
			
			
			gotoViewAccountBalance();

		} catch (Exception e) {
			e.printStackTrace();
			Log.e(TAG, e.getMessage());
			sendMessage(FAILED);
		}
	}
}