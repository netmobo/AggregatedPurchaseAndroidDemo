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
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

/**
 * @author netmobo
 */
public class InputAmountActivity extends Activity {
	private static final String TAG = "INPUTAMOUNTACTIVITY";

	public static final int ACTIVITY_RECHARGE_ACCOUNT = 1;
	public static final int ACTIVITY_SETUP_ACCOUNT = 2;

	private TextView screenTitle;
	private TextView accountOwner;
	private TextView accountSerialNumber;
	// Brand Product Price / Current Balance
	private TextView textView1;
	// Amount To Pay
	private TextView textView2;
	private Spinner mPaymentMode;

	private TextView textView1B;
	private EditText editText2;

	private Button backBtn;
	private Button googleCheckout;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.amount_input);
		initDisplay();
	}

	public void initDisplay() {
		screenTitle = (TextView) findViewById(R.id.screen_title);
		textView1 = (TextView) findViewById(R.id.textview1);
		textView1B = (TextView) findViewById(R.id.textview1b);

		textView2 = (TextView) findViewById(R.id.textview2);
		editText2 = (EditText) findViewById(R.id.edittext2);

		accountOwner = (TextView) findViewById(R.id.amount_input_username);
		accountSerialNumber = (TextView) findViewById(R.id.amount_input_serialnumber);

		backBtn = (Button) findViewById(R.id.back_button);
		googleCheckout = (Button) findViewById(R.id.google_checkout);
		mPaymentMode = (Spinner) findViewById(R.id.payment_modes);
		
		ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
	            this, R.array.recharge_types, android.R.layout.simple_spinner_item);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		mPaymentMode.setAdapter(adapter);

		backBtn.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				backBtnClicked();
			}
		});
		googleCheckout.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				googleCheckoutBtnClicked();
			}
		});

		Bundle extras = getIntent().getExtras();
		if (extras == null) {
			return;
		}
		int layout = extras.getInt("com.feefactor.samples.android.apppurchase.layout");

		accountOwner.setText(extras.getString("com.feefactor.samples.android.apppurchase.username"));
		accountSerialNumber
				.setText("" + extras.getLong("com.feefactor.samples.android.apppurchase.accountSerialNumber"));
		
		switch (layout) {
		// externalize
		case ACTIVITY_RECHARGE_ACCOUNT:
			screenTitle.setText("Add Money to Balance");
			textView1.setText("Current Balance:");
			textView2.setText("Amount to Add:");
			textView1B.setText("$ " + extras.getString("com.feefactor.samples.android.apppurchase.oldBalance"));
			break;
		case ACTIVITY_SETUP_ACCOUNT:
			screenTitle.setText("Load Balance to Account");
			textView1.setText("Current Balance:");
			textView2.setText("Amount to Pay:");
			textView1B.setText("$ " + extras.getString("com.feefactor.samples.android.apppurchase.oldBalance"));
			break;
		}
	}

	public void backBtnClicked() {
		Bundle extras = getIntent().getExtras();
		if (extras == null) {
			finish();
			return;
		}
		int layout = extras.getInt("layout");
		if (layout == ACTIVITY_SETUP_ACCOUNT) {
			QuickstartApplication qsApp = (QuickstartApplication) getApplication();
			// need to logout
			qsApp.logout();
			Intent intent = new Intent(this, LoginActivity.class);
			intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(intent);
		}else if (layout == ACTIVITY_RECHARGE_ACCOUNT) {
			finish();
		}
	}

	public void googleCheckoutBtnClicked() {
		if (editText2.getText() == null || editText2.getText().toString().trim().length()<1) {
			// TODO GC
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setMessage("Please Enter A Valid Amount!")
			.setPositiveButton("OK", new DialogInterface.OnClickListener() {
		           public void onClick(DialogInterface dialog, int id) {
		                dialog.dismiss();
		           }
		       });
			AlertDialog alert = builder.create();
			alert.show();
			return;
		}
		Log.d(TAG, "Payment Mode: " + mPaymentMode.getSelectedItem().toString());
		
		if (mPaymentMode.getSelectedItem().toString().equals("Google Checkout")) {
			Intent intent = new Intent(this, GoogleCheckoutActivity.class);
			Log.d(TAG, "Amount entered: " + editText2.getText());
			// need to get String. It seems getText returns a SpannableString
			intent.putExtra("com.feefactor.samples.android.apppurchase.unitPriceAmount", editText2.getText().toString());
			intent.putExtra("com.feefactor.samples.android.apppurchase.accountID", Long.valueOf(accountSerialNumber.getText().toString()));

			// pass through
			intent.putExtras(getIntent().getExtras());
			startActivityForResult(intent, GoogleCheckoutActivity.ACTIVITY_GOOGLECHECKOUT);
		} else if (mPaymentMode.getSelectedItem().toString().equals("Credit Card")) {
		
			Intent intent = new Intent(this, CreditCardPaymentActivity.class);
			Log.d(TAG, "Amount entered: " + editText2.getText());
			// need to get String. It seems getText returns a SpannableString
			intent.putExtra("com.feefactor.samples.android.apppurchase.unitPriceAmount", editText2.getText().toString());
			intent.putExtra("com.feefactor.samples.android.apppurchase.accountID", Long.valueOf(accountSerialNumber.getText().toString()));
			intent.putExtra("com.feefactor.samples.android.apppurchase.PAYMENTGATEWAYID", QuickstartApplication.PAYMENT_GATEWAY_ID_CC);
	
			// pass through
			intent.putExtras(getIntent().getExtras());
			startActivityForResult(intent, CreditCardPaymentActivity.ACTIVITY_CREDITCARDPAYMENTACTIVITY);
		}
	}
	
	
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		// we can put several payment options here
		if (GoogleCheckoutActivity.ACTIVITY_GOOGLECHECKOUT == requestCode) {
			handleGoogle(resultCode, data);
		}
	}
	
	private void handleGoogle(int resultCode, Intent data) {
		switch (resultCode) {
		case RESULT_CANCELED:
			// do nothing. just go back inputting amount
			Log.d(TAG, "Got back");
			break;
		case RESULT_OK:
			// go to manage account
			gotoViewAccountBalance();
			finish();
			break;
		}
	}

	private void gotoViewAccountBalance() {
		Intent intent = new Intent(this, ViewAccountBalanceActivity.class);
		intent.putExtra("com.feefactor.samples.android.apppurchase.flagfrom", "GOOGLECHECKOUT");
		startActivity(intent);
	}
}
