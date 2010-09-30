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

import com.feefactor.ClientConfig;
import com.feefactor.FeefactorCheckedException;
import com.feefactor.paymentsystems.CardPayments;
import com.feefactor.samples.android.ProgressableRunnable;
import com.feefactor.samples.android.ProgressableTask;
import com.feefactor.subscriber.User;

/**
 * @author netmobo
 */
public class CreditCardPaymentActivity extends BaseActivity {
	private static final String TAG = "CREDITCARDPAYMENTACTIVITY";
	
	public static final int ACTIVITY_CREDITCARDPAYMENTACTIVITY = 100;
	
	private Button submitBtn;
	private Button cancelBtn;
	private EditText amount;
	private Spinner creditCardType;
	private Spinner expirationMonth;
	private Spinner expirationYear;
	private EditText creditCardNumber;
	private EditText cvv;
	private EditText emailEt;
	private EditText firstName;
	private EditText lastName;
	private EditText street;
	private EditText city;
	private Spinner country;
	private EditText state;
	private EditText zipCode;
	private EditText phone;
	private TextView dummyCCNumbers;
	private ProgressableRunnable viewCreditCard;
	
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.creditcard_payment);
		initDisplay();
	}
	
	protected void initDisplay() {
		submitBtn = (Button) findViewById(R.id.cc_submit_button);
		cancelBtn = (Button) findViewById(R.id.cc_cancel_button);
		
		amount = (EditText) findViewById(R.id.cc_amount);
		creditCardType = (Spinner) findViewById(R.id.cc_type);
		creditCardNumber = (EditText) findViewById(R.id.cc_creditcardnumber);
		expirationMonth = (Spinner) findViewById(R.id.cc_expiration_month);
		expirationYear = (Spinner) findViewById(R.id.cc_expiration_year);
		cvv = (EditText) findViewById(R.id.cc_cvv);
		emailEt = (EditText) findViewById(R.id.cc_email);
		firstName = (EditText) findViewById(R.id.cc_firstname);
		lastName = (EditText) findViewById(R.id.cc_lastname);
		street = (EditText) findViewById(R.id.cc_street);
		city = (EditText) findViewById(R.id.cc_city);
		country = (Spinner) findViewById(R.id.cc_country);
		state = (EditText) findViewById(R.id.cc_state);
		zipCode = (EditText) findViewById(R.id.cc_zipcode);
		phone = (EditText) findViewById(R.id.cc_phone);
		dummyCCNumbers = (TextView) findViewById(R.id.cc_dummy_ccnumbers);
		
		dummyCCNumbers.setText(R.string.dummy_cc_numbers_list);
		
		ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
	            this, R.array.cc_type_array, android.R.layout.simple_spinner_item);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		creditCardType.setAdapter(adapter);
		
		ArrayAdapter<CharSequence> adapter2 = ArrayAdapter.createFromResource(
	            this, R.array.months_array, android.R.layout.simple_spinner_item);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		expirationMonth.setAdapter(adapter2);
		
		ArrayAdapter<CharSequence> adapter3 = ArrayAdapter.createFromResource(
	            this, R.array.exp_year_list, android.R.layout.simple_spinner_item);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		expirationYear.setAdapter(adapter3);
		
		ArrayAdapter<CharSequence> adapter4 = ArrayAdapter.createFromResource(
	            this, R.array.countries_list, android.R.layout.simple_spinner_item);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		country.setAdapter(adapter4);
		
		//setting default values;
		creditCardNumber.setText("9999123456789105");
		expirationMonth.setSelection(11);
		expirationYear.setSelection(10);
		cvv.setText("123");
		emailEt.setText("email@email.com");
		firstName.setText("firstname");
		lastName.setText("lastname");
		street.setText("street");
		city.setText("city");
		state.setText("NY");
		zipCode.setText("1234");
		phone.setText("12345678900");
		
		Intent intent = getIntent();
		if (intent != null) {
			Bundle extras = intent.getExtras();
			String unitPriceAmount = extras.getString("com.feefactor.samples.android.apppurchase.unitPriceAmount");
			amount.setText(unitPriceAmount);
		}
		
		// Set Click Listener
		submitBtn.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				submitClicked();
			}
		});
		cancelBtn.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				cancelClicked();
			}
		});
	}
		
	private void submitClicked() {
		String amount;
		String creditCardNumber;
		String creditCardType;
		String cvv;
		String creditCardExpirationMonth;
		String creditCardExpirationYear;
		String email;
		String firstName;
		String lastName;
		String street;
		String city;
		String country;
		String state;
		String zipCode;
		String phone;
		
		amount = this.amount.getText().toString().trim(); 
		creditCardNumber = this.creditCardNumber.getText().toString().trim();
		cvv = this.cvv.getText().toString().trim();
		creditCardType = this.creditCardType.getSelectedItem().toString().trim();
		creditCardExpirationMonth = this.expirationMonth.getSelectedItem().toString().trim();
		creditCardExpirationYear = this.expirationYear.getSelectedItem().toString().trim();
		email = this.emailEt.getText().toString().trim();
		firstName = this.firstName.getText().toString().trim();
		lastName = this.lastName.getText().toString().trim();
		street = this.street.getText().toString().trim();
		city = this.city.getText().toString().trim();
		phone = this.phone.getText().toString().trim();
		country = this.country.getSelectedItem().toString().trim();
		state = this.state.getText().toString().trim();
		zipCode = this.zipCode.getText().toString().trim();
		
		if (amount.length() == 0 || creditCardNumber.length() == 0 || cvv.length() == 0
				|| creditCardExpirationMonth.length() == 0 || creditCardExpirationYear.length() == 0
				|| email.length() == 0 || firstName.length() == 0 || lastName.length() == 0
				|| street.length() == 0 || city.length() == 0 || country.length() == 0
				|| state.length() == 0 || zipCode.length() == 0 || phone.length() == 0
				|| creditCardType.length() == 0) {
			// TODO GC
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setMessage("Required fields are missing!")
			.setPositiveButton("OK", new DialogInterface.OnClickListener() {
		           public void onClick(DialogInterface dialog, int id) {
		                dialog.dismiss();
		           }
		       });
			AlertDialog alert = builder.create();
			alert.show();
			return;
		}
		
		try {
			viewCreditCard = new ProgressableRunnable() {
				public void run() {
					rechargeCreditCard();
				}

				public void onCancel() {
				}
			};
			ProgressableTask task = new ProgressableTask(this, viewCreditCard,
					R.string.recharge);
			task.start();
			
		} catch (Exception e) {
			Log.e(TAG, e.getMessage());
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setMessage("There was in error in recharging. Please try again later! " + e.getMessage())
			.setPositiveButton("OK", new DialogInterface.OnClickListener() {
		           public void onClick(DialogInterface dialog, int id) {
		                dialog.dismiss();
		           }
		       });
			AlertDialog alert = builder.create();
			alert.show();
			return;
		}
	}
	
	public void rechargeCreditCard() {
		
		String amount;
		String creditCardNumber;
		String creditCardType;
		String cvv;
		String creditCardExpirationMonth;
		String creditCardExpirationYear;
		String email;
		String firstName;
		String lastName;
		String street;
		String city;
		String country;
		String state;
		String zipCode;
		String phone;
		
		amount = this.amount.getText().toString().trim(); 
		creditCardNumber = this.creditCardNumber.getText().toString().trim();
		cvv = this.cvv.getText().toString().trim();
		creditCardType = this.creditCardType.getSelectedItem().toString().trim();
		creditCardExpirationMonth = this.expirationMonth.getSelectedItem().toString().trim();
		creditCardExpirationYear = this.expirationYear.getSelectedItem().toString().trim();
		email = this.emailEt.getText().toString().trim();
		firstName = this.firstName.getText().toString().trim();
		lastName = this.lastName.getText().toString().trim();
		street = this.street.getText().toString().trim();
		city = this.city.getText().toString().trim();
		phone = this.phone.getText().toString().trim();
		country = this.country.getSelectedItem().toString().trim();
		state = this.state.getText().toString().trim();
		zipCode = this.zipCode.getText().toString().trim();
		
		QuickstartApplication qsApp = (QuickstartApplication) getApplication();
		
		User user = qsApp.getUser();
		Log.d(TAG, "Setting up config...");
		ClientConfig config = qsApp.setupRtbeUserAuthClientConfig(QuickstartApplication.BRANDID, user.getUsername(), user.getPassword());
		
		CardPayments cps = new CardPayments(config);
		Log.d(TAG, "Recharging...");
		
		Intent intent = getIntent();
		Bundle extras = intent.getExtras();
		
		try {
			Log.d(TAG, "payment gateway id - " + QuickstartApplication.PAYMENT_GATEWAY_ID_CC);
			Log.d(TAG, "account id - " + extras.getLong("com.feefactor.samples.android.apppurchase.accountID"));
			Log.d(TAG, "amount - " + Double.valueOf(amount));
		cps.rechargeAccountViaCC(QuickstartApplication.PAYMENT_GATEWAY_ID_CC, extras.getLong("com.feefactor.samples.android.apppurchase.accountID"),
				Double.valueOf(amount), firstName, lastName, email, phone, street, "", city, state, zipCode, country,
				creditCardNumber, creditCardExpirationMonth, creditCardExpirationYear,
				creditCardType, cvv, "", "", "", "", false);
		} catch (FeefactorCheckedException e) {
			sendMessage("Credit Card recharge failed. Try again later. Error Message: " + e.getMessage());
			return;
		} catch (NumberFormatException e) {
			sendMessage("Invalid data entered!");
			return;
		}
		
		intent  = new Intent(this, ViewAccountBalanceActivity.class);
		startActivity(intent);
	}
	
	
	private void cancelClicked() {
		finish();
	}
}
