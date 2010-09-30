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

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.feefactor.accounts.AccountHistory;

/**
 * @deprecated
 * @author netmobo
 */
public class AccountHistoryAdapter extends ArrayAdapter<AccountHistory> {
//	private DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
	private DateFormat dfTo = new SimpleDateFormat("HH:mm, MMM dd");

	static class ViewHolder {
		TextView tt;
		TextView bt;
	}

	private List<AccountHistory> m_items;

	public AccountHistoryAdapter(Context context, int textViewResourceId,
			List<AccountHistory> items) {
		super(context, textViewResourceId, items);
		this.m_items = items;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		ViewHolder vh;
		View v = convertView;

		if (v == null) {
			LayoutInflater vi = (LayoutInflater) getContext().getSystemService(
					Context.LAYOUT_INFLATER_SERVICE);
			v = vi.inflate(R.layout.row, null);

			vh = new ViewHolder();
			vh.tt = (TextView) v.findViewById(R.id.toptext);
			vh.bt = (TextView) v.findViewById(R.id.bottomtext);
			v.setTag(vh);

		} else {
			vh = (ViewHolder) v.getTag();
		}

		AccountHistory o = m_items.get(position);
		
		StringBuffer sb = new StringBuffer();
		Calendar tdate = o.getTransactionDate();
		//dfTo = new SimpleDateFormat("HH:mm, MMM dd");
		sb.append(tdate.get(Calendar.HOUR_OF_DAY)).append(":").append(tdate.get(Calendar.MINUTE)).append(", ");
		sb.append(tdate.get(Calendar.MONTH)).append("/").append(tdate.get(Calendar.DAY_OF_MONTH)).append("(mm/dd)");
		sb.append(" Amount: ").append(o.getAmountChange());

		vh.tt.setText(sb.toString());
		String type = o.getTransactionType();
		if (o.getTransactionType().equals("CDR")) {
			type =  "TDR";
		}
		vh.bt.setText("Type: " + type + " Description: "
				+ o.getDescription());

		return v;
	}
}
