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

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

/**
 * @author netmobo
 */
public class PurchaseProductResultActivity extends Activity {
	private static final String TAG = "SIGNUPACTIVITY";
	
	public static final int ACTIVITY_CREDITCARDPAYMENTACTIVITY = 100;
	
	private Button downloadBtn;
	private Button checkBalance;
	private Button logout;
	private TextView balance;
	private TextView product;
	
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.purchase_results);
		initDisplay();
	}
	
	protected void initDisplay() {
		downloadBtn = (Button) findViewById(R.id.purchase_results_button);
		checkBalance = (Button) findViewById(R.id.purchase_results_checkbalance);
		logout = (Button) findViewById(R.id.purchase_results_logout);
		
		balance = (TextView) findViewById(R.id.purchase_results_balance);
		product = (TextView) findViewById(R.id.purchase_results_plugin);
		
		Intent intent = getIntent();
		if (intent != null) {
			Bundle extras = intent.getExtras();
			if (extras != null) {
				Log.d(TAG, "mProduct -- " + product);
				product.setText(extras.getString("product"));
				balance.setText(""+extras.getDouble("newbalance"));
			}
		}
		
		// Set Click Listener
		downloadBtn.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				downloadClicked();
			}
		});
		
		checkBalance.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				checkBalanceClicked();
			}
		});
		
		logout.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				logoutClicked();
			}
		});
	}
		
	private void downloadClicked() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setMessage("This is just a demo. There is nothing to download here.")
		.setPositiveButton("OK", new DialogInterface.OnClickListener() {
	           public void onClick(DialogInterface dialog, int id) {
	                dialog.dismiss();
	           }
	       });
		AlertDialog alert = builder.create();
		alert.show();
		return;
	}
	
	private void logoutClicked() {
		QuickstartApplication qsApp = (QuickstartApplication) getApplication();
		// need to logout
		qsApp.logout();
		Intent intent = new Intent(this,LoginActivity.class);
		intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		startActivity(intent);
	}
	
	private void checkBalanceClicked() {
		Intent intent  = new Intent(this, ViewAccountBalanceActivity.class);
		startActivity(intent);
	}
	
}
