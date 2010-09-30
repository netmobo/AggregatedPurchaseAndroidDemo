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

import java.util.List;

import com.feefactor.FeefactorCheckedException;
import com.feefactor.accounts.Account;
import com.feefactor.accounts.Accounts;
import com.feefactor.charging.Transactions;
import com.feefactor.samples.android.ProgressableRunnable;
import com.feefactor.samples.android.ProgressableTask;
import com.feefactor.services.BrandProduct;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.TextView;

/**
 * @author netmobo
 */
public class PurchaseProductActivity extends BaseActivity {

	private static final String TAG = "PURCHASEPRODUCTACTIVITY";
	private List<RatedBrandProduct> products;
	
	//Field to determine list index number of product selected
	private int position;

	//Android Layout variables
	private ListView listView;
	private ProgressableRunnable viewPurchaseProduct;
	private ProgressDialog progressDialog = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.purchaseproduct);

		TextView accountBalance = (TextView)findViewById(R.id.purchase_text_balance);
		listView = (ListView) findViewById(R.id.purchaseproduct_listview);
		listView.setOnItemSelectedListener(selectListener);
		listView.setOnItemClickListener(clickListener);
		
		QuickstartApplication app = (QuickstartApplication) getApplication();
		
		//Retrieving account
		Account account = app.getAccount();
	    double balance = (account.getBalance());
	    accountBalance.setText(String.valueOf(balance));
		
		new LoadProductTask().execute();
		showProgress();
	}
	
	private class LoadProductTask extends AsyncTask<Void, Void, List<RatedBrandProduct>> {
		protected List<RatedBrandProduct> doInBackground(Void... params) {
			return retrieveProducts();
		}
		
	     protected void onPostExecute(List<RatedBrandProduct> result) {
	    	 registerListProducts(result);
	     }
	 }
	
	private List<RatedBrandProduct> retrieveProducts() {
		QuickstartApplication app = (QuickstartApplication) getApplication();
		return app.getProducts();
	}
	
	private void registerListProducts(List<RatedBrandProduct> products) {
		Log.d(TAG, "register list products...");
		Log.d(TAG, "products.size() - " + products.size());
		this.products = products;
		ProductAdapter prodAdapter = new ProductAdapter(this, R.layout.productrow, products);
		Log.d(TAG, "prodAdapter - " + prodAdapter);
		Log.d(TAG, "listView - " + listView);
		listView.setAdapter(prodAdapter);
		
		tablePopulated = true;
	}
	
	private boolean tablePopulated;
	
	private void showProgress() {
		ProgressableRunnable runnable = new ProgressableRunnable() {
			public void run() {
				blockTillPopulated();
			}

			public void onCancel() {
			}
		};
		
		ProgressableTask task =  new ProgressableTask(this, runnable, R.string.msg_load_history);
		task.start();
	}
	
	public void blockTillPopulated() {
		while(!tablePopulated) {
			try {
				Thread.sleep(100);
			}catch (Exception e) { }
		}
	}
	
	private void purchaseProduct(BrandProduct bp) {
		QuickstartApplication app = (QuickstartApplication)getApplication();
		Transactions tu = app.getTransactions();
		String bsName = app.getBrandService().getServiceName();

		try {
			tu.chargeAccount(QuickstartApplication.BRANDID, app.getAccount().getAccountID(), bsName, 
					bp.getProductCode(), 1l, 
					"Purchase Product: " + bp.getDescription() + " (" + bp.getProductCode() + ")");
			
		} catch (FeefactorCheckedException e) {
			e.printStackTrace();
		}
	}

	

	private OnItemClickListener clickListener = new OnItemClickListener() {
		public void onItemClick(AdapterView<?> parent, View view, 
				int position, long id) {
			
			BrandProduct bp = (BrandProduct)listView.getItemAtPosition(position);		
			
			purchaseProduct(bp);
		}
	};

	private OnItemSelectedListener selectListener = new OnItemSelectedListener() {
		public void onItemSelected(AdapterView<?> parent, View view,
				int position, long id) {
			Log.i("TEST", 
					String.valueOf("Select -> " + listView.getItemAtPosition(position)));
		}

		public void onNothingSelected(AdapterView<?> arg0) {
			// do nothing...
		}
	};

	private class ProductAdapter extends ArrayAdapter<RatedBrandProduct> {

		private List<RatedBrandProduct> products;

		public ProductAdapter(Context context, int textViewResourceId, List<RatedBrandProduct> objects) {
			super(context, textViewResourceId, objects);

			this.products = objects;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View v = convertView;
			if (v == null) {
				LayoutInflater vi = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				v = vi.inflate(R.layout.productrow, null);
			}
			
			RatedBrandProduct bp = products.get(position);
			if (bp != null) {
				TextView pn = (TextView) v.findViewById(R.id.product_name);
				TextView pp = (TextView) v.findViewById(R.id.product_points);
				Button buy = (Button) v.findViewById(R.id.product_buy);
				final int position2 = position;
				if (pn != null) {
					pn.setText(bp.getDescription());
				}

				if (pp != null) {
					double price = bp.getInitialPrice();
					pp.setText(String.valueOf(price));
				}
				
				buy.setOnClickListener(new OnClickListener() {
					public void onClick(View v) {
						buyClicked(position2);
					}
				});
			}
			return v;
		}
		
		
	}
	
	private void buyClicked(int position) {
		this.position = position;
		viewPurchaseProduct = new ProgressableRunnable() {
			public void run() {
				purchaseProduct();
			}

			public void onCancel() {
				
			}
		};
		ProgressableTask task = new ProgressableTask(this, viewPurchaseProduct,
				R.string.purchase);
		task.start();
	}
	
	private void purchaseProduct() {
		Log.d(TAG, "position - " + position);
		RatedBrandProduct bp = products.get(position);
		Log.d(TAG, "bp - " + bp.getProductCode());
		QuickstartApplication qsApp = (QuickstartApplication) getApplication();
		Transactions transactions = qsApp.getTransactions();
		try {
			Log.d(TAG, "brandservicename - " + qsApp.getBrandService().getServiceName());
			transactions.chargeAccount(qsApp.BRANDID, qsApp.getAccount().getAccountID(), qsApp.getBrandService().getServiceName(), bp.getProductCode(), 1, "Android test app");
			if (progressDialog != null) {
				progressDialog.dismiss();
			}
			Accounts accts = qsApp.getAccounts();
			Intent intent  = new Intent(this, PurchaseProductResultActivity.class);
			Account account;
			try {
				account = accts.getAccount(qsApp.getAccount().getSerialNumber());
				intent.putExtra("product", bp.getDescription());
				intent.putExtra("newbalance", account.getBalance());
				startActivity(intent);
			} catch (FeefactorCheckedException e) {
				e.printStackTrace();
			}
		} catch (FeefactorCheckedException e) {
			sendMessage("Purchase failed. Please try again later.");
			return;
		} catch (Exception e) {
			sendMessage(e.getMessage());
			return;
		}
	}

	@Override
	protected void initDisplay() {
	}
	
}
