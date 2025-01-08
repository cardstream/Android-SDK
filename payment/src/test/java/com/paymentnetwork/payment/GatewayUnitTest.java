package com.paymentnetwork.payment;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;

import java.util.HashMap;
import java.util.Map;

/**
 * Test cases for Gateway class.
 */
@Config(sdk = 32, manifest=Config.NONE)
@RunWith(RobolectricTestRunner.class)
public class GatewayUnitTest {
	String GATEWAY_DIRECT_URL = RuntimeEnvironment.getApplication().getString(R.string.direct_url);
	String GATEWAY_HOSTED_URL = RuntimeEnvironment.getApplication().getString(R.string.hosted_url);
	String MERCHANT_ID = RuntimeEnvironment.getApplication().getString(R.string.merchant_id);
	String MERCHANT_SECRET = RuntimeEnvironment.getApplication().getString(R.string.merchant_secret);

	private final Gateway gatewayDirect = new Gateway(GATEWAY_DIRECT_URL, MERCHANT_ID, MERCHANT_SECRET);
	private final Gateway gatewayHosted = new Gateway(GATEWAY_HOSTED_URL, MERCHANT_ID, MERCHANT_SECRET);

	@Test
	public void direct_request() throws Exception {

		final Map<String, String> request = new HashMap<>();

		request.put("action", "SALE");
		request.put("amount", "2099");
		request.put("cardCVV", "356");
		request.put("cardExpiryMonth", "12");
		request.put("cardExpiryYear", "23");
		request.put("cardNumber", "4929421234600821");
		request.put("countryCode", "826"); // GB
		request.put("currencyCode", "826"); // GBP
		request.put("customerAddress", "Flat 6 Primrose Rise 347 Lavender Road Northampton");
		request.put("customerName", "Tester");
		request.put("customerEmail", "user@example.com");
		request.put("customerPostCode", "NN17 8YG");
		request.put("orderRef", "T001");
		request.put("type", "1"); // E-commerce

		final Map<String, String> response = this.gatewayDirect.directRequest(request);
		System.out.println(response.toString());
		assertEquals(Integer.parseInt(response.get("responseCode")), Gateway.RC_SUCCESS);
		assertEquals(response.get("amountReceived"), request.get("amount"));
		assertEquals(response.get("state"), "captured");

	}

	@Test
	public void hosted_request() throws Exception {

		final Map<String, String> request = new HashMap<>();

		request.put("action", "SALE");
		request.put("amount", "2399");
		request.put("cardExpiryDate", "1223");
		request.put("cardNumber", "4929 4212 3460 0821");
		request.put("countryCode", "826"); // GB
		request.put("currencyCode", "826"); // GBP
		request.put("orderRef", "T004");
		request.put("transactionUnique", "55f025addd3c2");
		request.put("type", "1"); // E-commerce

		final Map<String, String> options = new HashMap<>();

		options.put("submitText", "Confirm & Pay");

		final String html = this.gatewayHosted.hostedRequest(request, options);
		System.out.println(html);
		assertEquals(html, "<form method=\"post\"  action=\""+GATEWAY_HOSTED_URL+"\">\n"
				+ "<input type=\"hidden\" name=\"action\" value=\"SALE\" />\n"
				+ "<input type=\"hidden\" name=\"amount\" value=\"2399\" />\n"
				+ "<input type=\"hidden\" name=\"cardExpiryDate\" value=\"1223\" />\n"
				+ "<input type=\"hidden\" name=\"cardNumber\" value=\"4929 4212 3460 0821\" />\n"
				+ "<input type=\"hidden\" name=\"countryCode\" value=\"826\" />\n"
				+ "<input type=\"hidden\" name=\"currencyCode\" value=\"826\" />\n"
				+ "<input type=\"hidden\" name=\"merchantID\" value=\"merchant_id_here\" />\n"
				+ "<input type=\"hidden\" name=\"orderRef\" value=\"T004\" />\n"
				+ "<input type=\"hidden\" name=\"signature\" value=\"f21eb93274320dc084b3431365b40e9e69a7dc5fc90ee44b527146a9e16a4e7c520ea2d40452e248afd20ebd388bc878279e45d8ad09ade9acf2b9e977a10403|action,amount,cardExpiryDate,cardNumber,countryCode,currencyCode,merchantID,orderRef,transactionUnique,type\" />\n"
				+ "<input type=\"hidden\" name=\"transactionUnique\" value=\"55f025addd3c2\" />\n"
				+ "<input type=\"hidden\" name=\"type\" value=\"1\" />\n"
				+ "<input  type=\"submit\" value=\"Confirm &amp; Pay\">\n"
				+ "</form>\n");

	}

}