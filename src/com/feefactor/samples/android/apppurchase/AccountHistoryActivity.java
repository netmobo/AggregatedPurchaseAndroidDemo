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

import java.util.ArrayList;
import java.util.List;

import android.app.ListActivity;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.util.Log;

import com.feefactor.accounts.Account;
import com.feefactor.accounts.AccountHistory;

/**
 * @deprecated
 * @author netmobo
 */
public class AccountHistoryActivity extends ListActivity {
	private ProgressDialog mProgressDialog = null;
	private List<AccountHistory> mAccountHistories = null;
	private AccountHistoryAdapter mAdapter;
	private Runnable mViewOrders;

	private Runnable m_returnRes = new Runnable() {		
		public void run() {
			if (mAccountHistories != null && mAccountHistories.size() > 0) {
				mAdapter.notifyDataSetChanged();
				for (int i = 0; i < mAccountHistories.size(); i++)
					mAdapter.add(mAccountHistories.get(i));
			}
			mProgressDialog.dismiss();
			mAdapter.notifyDataSetChanged();
		}
	};
	
	

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.account_histories);
		mAccountHistories = new ArrayList<AccountHistory>();
		this.mAdapter = new AccountHistoryAdapter(this, R.layout.row,
				mAccountHistories);
		setListAdapter(this.mAdapter);

		mViewOrders = new Runnable() {
			public void run() {
				getAccountHistories();
			}
		};
		Thread thread = new Thread(mViewOrders);
		thread.start();
		mProgressDialog = ProgressDialog.show(this, "Please wait...",
				"Retrieving data ...", true);
	}

	private void getAccountHistories() {
		try {
			QuickstartApplication qsApp = (QuickstartApplication) getApplicationContext();
			Account myAccount = qsApp.getAccount();

			// recent activity
			List<AccountHistory> accountHistories = qsApp.getAccounts()
					.getAccountHistories(myAccount.getSerialNumber(),
							"SERIALNUMBER=" + myAccount.getSerialNumber(),
							"TRANSACTIONDATE DESC", 10, 1);
			qsApp.setAccountHistories(accountHistories);

			mAccountHistories = qsApp.getAccountHistories();

			Log.i("getAccountHistories", "" + mAccountHistories.size());
		} catch (Exception e) {
			Log.e("getAccountHistories", e.getMessage());
		}
		runOnUiThread(m_returnRes);
	}
}
