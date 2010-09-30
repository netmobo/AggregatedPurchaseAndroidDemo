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

import java.util.Properties;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

import com.feefactor.FeefactorCheckedException;
import com.feefactor.accounts.Account;
import com.feefactor.accounts.Accounts;
import com.feefactor.paymentsystems.PaymentGateway;
import com.feefactor.subscriber.User;
import com.utility.StringUtility;

/**
 * @author netmobo
 */
public class ViewAccountBalanceActivity extends Activity {
	private static final String TAG = "VIEWACCOUNTBALANCEACTIVITY";
	private TextView accountOwner;
	
	private TextView accountSerialNumber;
	private Button addMoneyToBalanceBtn;
	private Button cancelBtn;
	private Button purchaseBtn;
	private Button viewBalanceHistoryBtn;

	// Brand Product Price / Current Balance
	private TextView currentBalance;
	
	// Need handler for callbacks to the UI thread
	private final Handler handler = new Handler();
	
	private ProgressDialog progressDialog = null;

	// Create runnable for posting
	final Runnable mUpdateAccountUI = new Runnable() {
		public void run() {
			QuickstartApplication qsApp = (QuickstartApplication) getApplicationContext();
			Account myAccount = qsApp.getAccount();			
			currentBalance.setText("$" + (myAccount.getCreditLimit()+myAccount.getBalance()));
			progressDialog.dismiss();
		}
	};
	
	public void addMoneyToBalanceClicked() {		
		QuickstartApplication qsApp = (QuickstartApplication) getApplication();
		
		PaymentGateway pg = qsApp.getPaymentGateway(); 
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
		
		Account account = qsApp.getAccount();
		User user = qsApp.getUser();
		
		intent.putExtra("com.feefactor.samples.android.apppurchase.accountSerialNumber", account.getSerialNumber());
		intent.putExtra("com.feefactor.samples.android.apppurchase.oldBalance", ""+(account.getBalance() + account.getCreditLimit()));
		intent.putExtra("com.feefactor.samples.android.apppurchase.username", user.getUsername());
		intent.putExtra("com.feefactor.samples.android.apppurchase.layout", InputAmountActivity.ACTIVITY_RECHARGE_ACCOUNT);
		
		Log.d(TAG, intent.getExtras().toString());
		startActivity(intent);
	}

	public void purchaseButtonClicked() {		
		Intent intent = new Intent(this, PurchaseProductActivity.class);
		startActivity(intent);
	}
	
	public void cancelClicked() {
		QuickstartApplication qsApp = (QuickstartApplication) getApplication();
		// need to logout
		qsApp.logout();
		Intent intent = new Intent(this,LoginActivity.class);
		intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		startActivity(intent);
	}
	
	@SuppressWarnings("deprecation")
	public void viewBalanceButtonClicked() {
		Intent intent = new Intent(this,AccountHistoryActivity.class);
		intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		startActivity(intent);
	}
	
	public void initDisplay() {
		currentBalance = (TextView) findViewById(R.id.view_balance_current_balance);
		accountOwner = (TextView) findViewById(R.id.view_balance_username);
		accountSerialNumber = (TextView) findViewById(R.id.view_balance_serialnumber);

		cancelBtn = (Button) findViewById(R.id.view_balance_cancel);
		addMoneyToBalanceBtn = (Button) findViewById(R.id.view_balance_add_money_to_balance);
		purchaseBtn = (Button) findViewById(R.id.view_balance_purchase);
		viewBalanceHistoryBtn = (Button) findViewById(R.id.view_balance_account_history);
		
		cancelBtn.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				cancelClicked();
			}
		});
		addMoneyToBalanceBtn.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				addMoneyToBalanceClicked();
			}
		});
		
		purchaseBtn.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				purchaseButtonClicked();
			}
		});
		
		viewBalanceHistoryBtn.setOnClickListener(new OnClickListener() {
			
			public void onClick(View arg0) {
				viewBalanceButtonClicked();
			}
		});

		// AUTOFILL		
		QuickstartApplication qsApp = (QuickstartApplication) getApplication();
		// since we are logged in, get the details
		Account account = qsApp.getAccount();
		User user = qsApp.getUser();
		Log.d(TAG, "mAccountOwner - " + accountOwner);
		Log.d(TAG, "user - " + user);
		accountOwner.setText(user.getUsername());
		accountSerialNumber.setText("" + account.getSerialNumber());
		currentBalance.setText("$"+(account.getBalance() + account.getCreditLimit()));
		
		Intent intent = getIntent();
		Bundle extras = intent.getExtras();
		String flag = "";
		if (extras != null) {
			flag = extras.getString("com.feefactor.samples.android.apppurchase.flagfrom");
		}
		if (flag != null && flag.equals("GOOGLECHECKOUT")) {
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setMessage("It may take time for Google Checkout payments to be reflected. Check back for Account Balance later.")
			.setPositiveButton("OK", new DialogInterface.OnClickListener() {
		           public void onClick(DialogInterface dialog, int id) {
		                dialog.dismiss();
		           }
		       });
			AlertDialog alert = builder.create();
			alert.show();
		}
		
	}

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.view_balance);
		initDisplay();
	}

	protected void onStart() {
		super.onStart();
		reloadAccount();
	}
	
	private void reloadAccount() {
		// offload to thread 
        Thread t = new Thread() {
            public void run() {
            	QuickstartApplication qsApp = (QuickstartApplication) getApplicationContext();
            	//initializing utility
        		Accounts accounts = qsApp.getAccounts();
            	
            	try {
            		Log.d(TAG, "Retrieving account...");
            		//Retrieving account
            		Account myAccount = accounts.getAccount(qsApp.getAccount().getSerialNumber());        			
        			// loading account to Quickstart app
        			qsApp.setAccount(myAccount);
        			Log.d(TAG, "Account Retrieved.");
        			//account retrieved
        			handler.post(mUpdateAccountUI);
        		}catch (FeefactorCheckedException e) {
        			//exception caught
        			Log.e(TAG, e.getMessage());
        		}
            }
        };
        t.start();
        progressDialog = ProgressDialog.show(this, "Please wait...",
				"Retrieving data ...", true);
	}
}
