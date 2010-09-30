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
import java.util.ResourceBundle;

import android.app.Application;
import android.util.Log;

import com.feefactor.BrandAuthDetail;
import com.feefactor.ClientConfig;
import com.feefactor.FeefactorCheckedException;
import com.feefactor.RtbeUserAuthDetail;
import com.feefactor.Server;
import com.feefactor.accounts.Account;
import com.feefactor.accounts.AccountHistory;
import com.feefactor.accounts.Accounts;
import com.feefactor.charging.Transactions;
import com.feefactor.paymentsystems.CardPayments;
import com.feefactor.paymentsystems.PaymentGateway;
import com.feefactor.services.BrandProduct;
import com.feefactor.services.BrandProductPrice;
import com.feefactor.services.BrandService;
import com.feefactor.services.BrandServices;
import com.feefactor.subscriber.Brand;
import com.feefactor.subscriber.Brands;
import com.feefactor.subscriber.Currency;
import com.feefactor.subscriber.User;
import com.feefactor.subscriber.Users;

/**
 * @author netmobo
 */
public class QuickstartApplication extends Application {
	public static final String TAG = "QUICKSTARTAPPLICATION";
	
	public static boolean testMode = false;

	private boolean isLoggedIn;

	private List<AccountHistory> accountHistories;
	private Account account = null;
	private User user = null;
	private Brand brand = null;
	private BrandProduct product = null;
	private BrandProductPrice price = null;
	private List<Currency> currencies;
	private PaymentGateway paymentGateway;

	private BrandServices brandServices;
	private Accounts accounts;
	private Brands brands;
	private Users users;
	private CardPayments cardPayments;
	private Transactions transactions;

	private ClientConfig config;

	// Pricing will be based on this APPCODE
	public final static String PRODUCT_CODE = "QUICKSTART1";

	// Remember these information
	// How can user get the ff info?
	public static long BRANDSERVICE_ID;
	public static long PAYMENT_GATEWAY_ID;
	public static long PAYMENT_GATEWAY_ID_CC;
	public static long CURRENCY_ID;
	
	// for AUTH
	public static long BRANDID;
	public static String DOMAIN;
	
	private String vhost;
	private int vport;
	private String vuri;
	
	// TODO: FIX THIS! USE LOGIN (RTBEUSER) INSTEAD
	public final static String TEMP_ADMIN_FOR_PAYMENT = "androidtest";
	
	private BrandService mBrandService;

	// REASON for the transactions
	public final static String REASON = "Quickstart Sample App 1";

	// Server Configuration
	/*public final static String HOST = Server.VOLTAIRE_FF.getHost();
	public final static int PORT = Server.VOLTAIRE_FF.getPort();
	public final static String PREFIX = Server.VOLTAIRE_FF.getPrefix();*/
	
	@Override
	public void onCreate() {
		super.onCreate();
		
		ResourceBundle bundle = 
			ResourceBundle.getBundle("com.feefactor.samples.android.apppurchase.config");
		
		if(bundle != null) {
			vhost = bundle.getString("voltaire.ws.host");
			vport = Integer.parseInt(bundle.getString("voltaire.ws.port"));
			vuri = bundle.getString("voltaire.ws.path");
			
			BRANDSERVICE_ID = Long.parseLong(bundle.getString("feefactor.service.brandserviceid"));
			BRANDID = Long.parseLong(bundle.getString("feefactor.service.brandid"));
			PAYMENT_GATEWAY_ID = Long.parseLong(bundle.getString("feefactor.service.paymentgatewaygc"));
			PAYMENT_GATEWAY_ID_CC = Long.parseLong(bundle.getString("feefactor.service.paymentgatewaycc"));
			CURRENCY_ID = Long.parseLong(bundle.getString("feefactor.service.currencyid"));
			DOMAIN = bundle.getString("feefactor.service.domain");
		}
	}

	public Account getAccount() {
		return account;
	}

	public void setAccount(Account account) {
		this.account = account;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public Brand getBrand() {
		return brand;
	}

	public void setBrand(Brand brand) {
		this.brand = brand;
	}

	public Accounts getAccounts() {
		return accounts;
	}

	public void setAccounts(Accounts accountService) {
		this.accounts = accountService;
	}

	public Brands getBrands() {
		return brands;
	}

	public void setBrands(Brands brandServices) {
		this.brands = brandServices;
	}

	public Users getUsers() {
		return users;
	}

	public void setUsers(Users userService) {
		this.users = userService;
	}
	
	public void setTransactions(Transactions txns) {
		this.transactions = txns;
	}
	
	public Transactions getTransactions() {
		return transactions;
	}
	
	public void setBrandService(BrandService bs) {
		this.mBrandService = bs;
	}
	
	public BrandService getBrandService() {
		return mBrandService;
	}

	public ClientConfig getConfig() {
		return config;
	}

	// public void setConfig(ClientConfig config) {
	// this.config = config;
	// }

	public List<AccountHistory> getAccountHistories() {
		return accountHistories;
	}

	public void setAccountHistories(List<AccountHistory> accountHistories) {
		this.accountHistories = accountHistories;
	}

	public PaymentGateway getPaymentGateway() {
		return paymentGateway;
	}

	public void setPaymentGateway(PaymentGateway paymentGateway) {
		this.paymentGateway = paymentGateway;
	}
	
	public void setBrandServices(BrandServices bsUtil) {
		this.brandServices = bsUtil;
	}
	
	public BrandServices getBrandServices() {
		return brandServices;
	}
	
	public List<RatedBrandProduct> getProducts() {
		BrandServices bsUtil = getBrandServices();
		
		try {
			List<BrandProduct> products = bsUtil.getBrandProducts(BRANDSERVICE_ID, 
					"brandserviceid = " +BRANDSERVICE_ID + " and upper(productcode) like 'PLUG%'", "", 0, 0);
			Log.d(TAG, "result --- "+ products.size());
			if(products == null || products.size() == 0){ 
				return null;
			}
			
			List<RatedBrandProduct> result = new ArrayList<RatedBrandProduct>();
			
			for(BrandProduct bp : products) {
				List<BrandProductPrice> prices = bsUtil.getBrandProductPrices(bp.getProductID(), 
						"", "INDEXNUMBER ASC",0l,0l);
				
				RatedBrandProduct rbp = new RatedBrandProduct(bp, prices);
				result.add(rbp);
			}
			
			Log.d(TAG, "result --- "+ result.size());
			return result;
		} catch (FeefactorCheckedException e) {
			e.printStackTrace();
		}
		
		return null;
	}
	
	/**
	 * 
	 * @return
	 */
	public Server setupServer() {
		Server server = new Server();
		server.setHost(vhost);
		server.setPort(vport);
		server.setPrefix(vuri);
		return server;
	}

	public void logout() {
		Log.i(TAG,"Logging out...");
		clearUtilities();
		this.isLoggedIn = false;
	}
	
	/**
	 * 
	 * @param username
	 * @param password
	 * @throws FeefactorCheckedException
	 */
	public void login(String username, String password)
			throws FeefactorCheckedException {
		clearUtilities();
		// Important - init Config
		ClientConfig config = setupRtbeUserAuthClientConfig(
				QuickstartApplication.BRANDID, username, password);

		setupUtilities(config);

		// authenticate
		Users users = getUsers();

		List<User> theUsers = users.getUsers("A.BRANDID="
				+ QuickstartApplication.BRANDID + " AND USERNAME='" + username
				+ "' AND PASSWORD='" + password + "'", "", 1, 1);
		User user = theUsers.get(0);

		// fill in "context"
		Brand brand = getBrands().getBrand(QuickstartApplication.BRANDID);

		setUser(user);
		setBrand(brand);

		String myAccountWhere = "BRANDID=" + brand.getBrandID() + " AND USERID="
			+ user.getUserID() + " AND ACCOUNTID='"
			+ user.getUsername() + "'";
		Log.d(TAG, myAccountWhere);
		List<Account> myAccounts = getAccounts().getAccounts(
				myAccountWhere, "", 1, 1);

		Account myAccount = myAccounts.get(0);
		setAccount(myAccount);		
		
		// then retrieve the brand product description & price
		/*BrandServices brandServices = new BrandServices(config);
		Log.d(TAG, "Getting BrandProducts...");
		// Double param passing for serviceid?
		List<BrandProduct> brandProducts = brandServices.getBrandProducts(QuickstartApplication.BRANDSERVICE_ID, "BRANDSERVICEID=" + QuickstartApplication.BRANDSERVICE_ID + " AND PRODUCTCODE='"+QuickstartApplication.PRODUCT_CODE+"'", "", 100, 1);			
		BrandProduct bp = brandProducts.get(0);			
		Log.d(TAG, "Getting BrandProductPrices...");
		List<BrandProductPrice> prices = brandServices.getBrandProductPrices( bp.getProductID(), "BRANDPRODUCTID="+bp.getProductID(), "INDEXNUMBER", 100, 1);
		BrandProductPrice brandProductPrice = prices.get(0);
		Log.d(TAG, "Product: " + bp);
		
		setProduct(bp);
		setPrice(brandProductPrice);*/
		
		CardPayments cardPayments = new CardPayments(config);
		List<PaymentGateway> paymentGateways = cardPayments.getBrandPaymentGateways("BRANDID=" + QuickstartApplication.BRANDID + " AND TYPE='GOOGLECHECKOUT'", "", 1, 1);
		
		PaymentGateway paymentGateway = paymentGateways.get(0);
		setPaymentGateway(paymentGateway);
		
		this.isLoggedIn = true;
	}

	/**
	 * 
	 * @param config
	 * @throws FeefactorCheckedException 
	 */
	private void setupUtilities(ClientConfig config) throws FeefactorCheckedException {
		this.config = config;
		Users users = new Users(config);
		setUsers(users);

		Brands brands = new Brands(config);
		setBrands(brands);

		Accounts accounts = new Accounts(config);
		setAccounts(accounts);

		CardPayments cardPayments = new CardPayments(config);
		setCardPayments(cardPayments);
		
		BrandServices bss = new BrandServices(config);
		setBrandServices(bss);
		
		BrandService bs = getBrandServices().getBrandService(BRANDSERVICE_ID);
		setBrandService(bs);
		
		Transactions transactions = new Transactions(config);
		setTransactions(transactions);
		
	}

	private void clearUtilities() {
		setUsers(null);
		setBrands(null);
		setAccounts(null);
		setCardPayments(null);
	}

	/**
	 * Self Signup
	 * 
	 * @param brandId
	 * @param domain
	 * @return
	 */
	public ClientConfig setupBrandAuthClientConfig(long brandId, String domain) {
		BrandAuthDetail authDetail = new BrandAuthDetail();
		authDetail.setBrandID(brandId);
		authDetail.setDomain(domain);
		
		return new ClientConfig(setupServer(), authDetail);
	}

	/**
	 * For Login
	 * 
	 * @param brandId
	 * @param username
	 * @param password
	 * @return
	 */
	public ClientConfig setupRtbeUserAuthClientConfig(long brandId,
			String username, String password) {
		RtbeUserAuthDetail authDetail = new RtbeUserAuthDetail();
		authDetail.setBrandID(brandId);
		authDetail.setUsername(username);
		authDetail.setPassword(password);

		return new ClientConfig(setupServer(), authDetail);
	}

	public CardPayments getCardPayments() {
		return cardPayments;
	}

	public void setCardPayments(CardPayments cardPayments) {
		this.cardPayments = cardPayments;
	}

	public List<Currency> getCurrencies() {
		return currencies;
	}

	public void setCurrencies(List<Currency> currencies) {
		this.currencies = currencies;
	}

	public boolean isLoggedIn() {
		return isLoggedIn;
	}

	public BrandProduct getProduct() {
		return product;
	}

	public void setProduct(BrandProduct product) {
		this.product = product;
	}

	public BrandProductPrice getPrice() {
		return price;
	}

	public void setPrice(BrandProductPrice price) {
		this.price = price;
	}
}
