package com.paymentnetwork.paymentdemo;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.paymentnetwork.payment.Gateway;

import java.math.BigDecimal;
import java.util.HashMap;

public class HostedPaymentActivity extends AppCompatActivity {

    static final String TAG = HostedPaymentActivity.class.getName();

    String GATEWAY_URL;
    String MERCHANT_ID;
    String MERCHANT_SECRET;
    String REDIRECT_URL;

    Gateway gateway;
    WebView webView;
    ProgressBar progress;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        this.setContentView(R.layout.hosted_payment);
        this.setTitle("Hosted Payment Example");

        GATEWAY_URL = getString(R.string.hosted_url);
        MERCHANT_ID = getString(R.string.merchant_id);
        MERCHANT_SECRET = getString(R.string.merchant_secret);
        REDIRECT_URL = getString(R.string.redirect_url);

        gateway = new Gateway(GATEWAY_URL, MERCHANT_ID, MERCHANT_SECRET);

        webView = (WebView) findViewById(R.id.webview);
        progress = (ProgressBar) findViewById(R.id.progressBar_cyclic);

        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);

        WebViewClient mWebViewClient = new WebViewClient() {
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                progress.setVisibility(View.VISIBLE);
                super.onPageStarted(view, url, favicon);
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                progress.setVisibility(View.GONE);
            }

            @Override
            public void doUpdateVisitedHistory(WebView view, String url, boolean isReload) {
                if(url.contains(REDIRECT_URL) ){
                    Toast.makeText(getApplicationContext(),"Hosted Payment Finished", Toast.LENGTH_LONG).show();
                    finish();
                }
                super.doUpdateVisitedHistory(view, url, isReload);
            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                view.loadUrl(request.getUrl().toString());
                return false;
            }
        };
        webView.setWebViewClient(mWebViewClient);

        this.sendPayment();
    }

    public void sendPayment() {

        final HashMap<String, String> request = new HashMap<>();
        final BigDecimal amount = new BigDecimal("23.99");

        request.put("action", "SALE");
        request.put("amount", amount.multiply(BigDecimal.valueOf(100)).toBigInteger().toString());

        request.put("customerEmail", "info@example.com");
        request.put("customerAddress","Flat 6 Primrose Rise 347 Lavender Road Northampton");
        request.put("customerPostCode","NN17 8YG");
        request.put("countryCode", "826"); // GB
        request.put("currencyCode", "826"); // GBP
        request.put("type", "1"); // E-commerce
        request.put("orderRef", "test003");
        request.put("redirectURL", REDIRECT_URL);
        //request.put("remoteAddress", "0.0.0.0");
        //request.put("threeDSRedirectURL", REDIRECT_URL);

        try {
            String response = gateway.hostedRequest(request);
            String unencodedHtml = "<!DOCTYPE html><html><head><meta name='viewport' content='width=device-width, initial-scale=1.0, maximum-scale=1.0, minimum-scale=1.0, user-scalable=no'><title>Payment Example</title></head><body>"+response+"</body></html>";
            String encodedHtml = Base64.encodeToString(unencodedHtml.getBytes(),
                    Base64.NO_PADDING);
            webView.loadData(encodedHtml, "text/html", "base64");
        } catch (final Exception e) {
            Log.e(TAG, "Gateway submit failed", e);
            Toast.makeText(getApplicationContext(),"Gateway submit failed", Toast.LENGTH_LONG).show();
        }
    }
}
