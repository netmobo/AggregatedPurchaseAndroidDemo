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

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Properties;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

import com.feefactor.ClientConfig;
import com.feefactor.FeefactorCheckedException;
import com.feefactor.accounts.Account;
import com.feefactor.accounts.Accounts;
import com.feefactor.paymentsystems.CardPayments;
import com.feefactor.paymentsystems.PaymentGateway;
import com.feefactor.samples.android.ProgressableRunnable;
import com.feefactor.samples.android.ProgressableTask;
import com.feefactor.subscriber.User;
import com.feefactor.subscriber.UserQuestion;
import com.feefactor.subscriber.SelfSignUp;
import com.feefactor.subscriber.Users;
import com.utility.StringUtility;

/**
 * @author netmobo
 */
public class SignUpActivity extends BaseActivity {
	private static final String TAG = "SIGNUPACTIVITY";
	
	private Button signupBtn;
	private Button cancelBtn;
	private EditText usernameEt;
	private EditText passwordEt;
	private EditText emailEt;
	
	private EditText userQuestionEt;
	private EditText userQuestionAnswerEt;
	
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.signup);
		initDisplay();
	}
	
	protected void initDisplay() {
		signupBtn = (Button) findViewById(R.id.signup_register_button);
		cancelBtn = (Button) findViewById(R.id.signup_cancel_button);
		
		usernameEt = (EditText) findViewById(R.id.username);
		passwordEt = (EditText) findViewById(R.id.password);
		emailEt = (EditText) findViewById(R.id.email);
		
		userQuestionEt = (EditText) findViewById(R.id.security_question);
		userQuestionAnswerEt = (EditText) findViewById(R.id.security_answer);
				
		String username;
		String password;
		String email;
		String question;
		String answer;
		
		if (QuickstartApplication.testMode) {
			// if we are in test mode, autogenerate account
			long random = new Double(Math.random()*1000).longValue();
			username = random + getString(R.string.username);
			password = (String) getString(R.string.password);
			email = random + getString(R.string.email);
			// default
			question = "What was your childhood nickname?";
			answer = "Boy Droid";
			usernameEt.setText(username);
			passwordEt.setText(password);
			emailEt.setText(email);
			userQuestionEt.setText(question);
			userQuestionAnswerEt.setText(answer);
		}
		
		
		// Set Click Listener
		signupBtn.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				signupClicked();
			}
		});
		cancelBtn.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				cancelClicked();
			}
		});
	}
	public void signupClicked() {
		ProgressableRunnable runnable = new ProgressableRunnable() {
			public void run() {
				signup();
			}

			public void onCancel() {
				cancelClicked();
			}
		};

		ProgressableTask task = new ProgressableTask(this, runnable, R.string.signup);
		task.start();
	}
	
		
	private void signup() {
		String username;
		String password;
		String email;
		String question;
		String answer; 
		
		username = usernameEt.getText().toString().trim();
		password = passwordEt.getText().toString().trim();
		email = emailEt.getText().toString().trim();
		question = userQuestionEt.getText().toString().trim();
		answer = userQuestionAnswerEt.getText().toString().trim();
		
		long BRANDID = QuickstartApplication.BRANDID;
		
		// BARE MINIMUM
		User user = new User();
		user.setUsername(username);
		user.setPassword(password);
		user.setBillingEmailAddress(email);
		user.setBrandID(BRANDID);
		// January 1 2020
		Calendar cal = Calendar.getInstance();
		cal.setTimeInMillis(1577836800000L);
		user.setExpiration(cal);
		user.setBillingSchedule("0 0 01 * * *");		

		try {
			QuickstartApplication qsApp = (QuickstartApplication) getApplication();
			
			Log.d(TAG, "Setting up config...");
			ClientConfig config = qsApp.setupBrandAuthClientConfig(QuickstartApplication.BRANDID, QuickstartApplication.DOMAIN);
			
			SelfSignUp userSelfSignUps = new SelfSignUp(config);
			
			Log.d(TAG, "Inserting user...");
			long userId = userSelfSignUps.insertUser(user, QuickstartApplication.REASON);
//			long userId = users.insertUser(user, QuickstartApplication.REASON);			
			Log.i(TAG, "insertUser: " + userId);
			
			// now we have a user, use it for the auth
			config = qsApp.setupRtbeUserAuthClientConfig(QuickstartApplication.BRANDID, username, password);
			
			Users users = new Users(config);
			Accounts accounts = new Accounts(config);
			UserQuestion userQuestion = new UserQuestion();
			userQuestion.setUserID(userId);
			userQuestion.setQuestion(question);
			userQuestion.setAnswer(answer);
			Log.d(TAG, "Inserting User Question...");
			users.insertUserQuestion(userQuestion, QuickstartApplication.REASON);
			
			// insert account
			Account account = new Account();
			// REQUIRED FIELDS
			// FOR FEEFACTOR --> account name must ALWAYS be the same as the username (1:1)
			account.setAccountID(user.getUsername());
			account.setBrandID(BRANDID);
			account.setType("RECHARGEABLE");
			account.setStatus("ACTIVE");
			account.setCurrencyID(QuickstartApplication.CURRENCY_ID);
			account.setUserID(userId);
			
			// RECOMMENDED FIELDS
			account.setCreator(QuickstartApplication.REASON);
			account.setAutoActivate(true);			

			Log.d(TAG, "Retrieving Account...");
			long serialNumber = 1;
			List<Account> acts = accounts.getAccounts("", "", 1, 1);
			if(acts!=null && !acts.isEmpty()){
			    serialNumber = acts.get(0).getSerialNumber();
			}
			
			// at this point we log-in the user
			Log.d(TAG, "Log in...");
			qsApp.login(username, password);
			
			CardPayments cardPayments = new CardPayments(config);
			List<PaymentGateway> paymentGateways = cardPayments.getBrandPaymentGateways("BRANDID=" + QuickstartApplication.BRANDID + " AND TYPE='GOOGLECHECKOUT'", "", 1, 1);
			
			PaymentGateway pg = paymentGateways.get(0);

			Properties props = StringUtility.stringToProperties(pg
					.getAuthentication(), "=", "[;\r\n]", true);

			String tmp = props.getProperty("ENVIRONMENT","SANDBOX");
			int environment = GoogleCheckoutActivity.SANDBOX;
			if (tmp.equalsIgnoreCase("live")) {
				environment = GoogleCheckoutActivity.LIVE;
			}
			String merchantID = props.getProperty("MERCHANTID");
			String merchantKey = props.getProperty("MERCHANTKEY");
			String currency = props.getProperty("CURRENCY");

			Intent intent = new Intent(this, InputAmountActivity.class);
			
			intent.putExtra("com.feefactor.samples.android.apppurchase.PAYMENTGATEWAYID", pg.getPaymentGatewayID());
			intent.putExtra("com.feefactor.samples.android.apppurchase.ENVIRONMENT", environment);
			intent.putExtra("com.feefactor.samples.android.apppurchase.MERCHANTID", merchantID);
			intent.putExtra("com.feefactor.samples.android.apppurchase.MERCHANTKEY", merchantKey);
			intent.putExtra("com.feefactor.samples.android.apppurchase.CURRENCY", currency);
			
			intent.putExtra("com.feefactor.samples.android.apppurchase.accountSerialNumber", serialNumber);
			intent.putExtra("com.feefactor.samples.android.apppurchase.oldBalance", "0.00");
			intent.putExtra("com.feefactor.samples.android.apppurchase.username", username);
			intent.putExtra("com.feefactor.samples.android.apppurchase.layout", InputAmountActivity.ACTIVITY_SETUP_ACCOUNT);
			
			// proceed to input amount activity
			startActivity(intent);
			sendMessage(SUCCESS);
		} catch (FeefactorCheckedException e) {
			Log.e(TAG, e.getMessage());
			e.printStackTrace();
			sendMessage(FAILED);
		}
	}
	
	
	private void cancelClicked() {
		finish();
	}
}