package com.paymentnetwork.paymentdemo;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.paymentnetwork.payment.Gateway;

import java.lang.ref.WeakReference;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

public class DirectPaymentActivity extends AppCompatActivity {

    static final String TAG = DirectPaymentActivity.class.getName();

    String GATEWAY_URL;
    String MERCHANT_ID;
    String MERCHANT_SECRET;

    Gateway gateway;

    protected EditText amount;
    protected EditText cardNumber;
    protected EditText cardExpiryDate;
    protected EditText cardCVV;
    protected EditText customerAddress;
    protected EditText customerPostCode;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        this.setContentView(R.layout.direct_payment);
        this.setTitle("Direct Payment Example");

        GATEWAY_URL = getString(R.string.hosted_url);
        MERCHANT_ID = getString(R.string.merchant_id);
        MERCHANT_SECRET = getString(R.string.merchant_secret);

        gateway = new Gateway(GATEWAY_URL, MERCHANT_ID, MERCHANT_SECRET);

        this.amount = (EditText) this.findViewById(R.id.amount);
        this.cardNumber = (EditText) this.findViewById(R.id.cardNumber);
        this.cardExpiryDate = (EditText) this.findViewById(R.id.cardExpiryDate);
        this.cardCVV = (EditText) this.findViewById(R.id.cardCVV);
        this.customerAddress = (EditText) this.findViewById(R.id.customerAddress);
        this.customerPostCode = (EditText) this.findViewById(R.id.customerPostCode);

    }

    public void sendPayment(final View view) {

        final HashMap<String, String> request = new HashMap<>();
        final BigDecimal amount = new BigDecimal(this.amount.getText().toString());

        request.put("action", "SALE");
        request.put("amount", amount.multiply(BigDecimal.valueOf(100)).toBigInteger().toString());
        request.put("cardNumber", this.cardNumber.getText().toString());
        request.put("cardExpiryDate", this.cardExpiryDate.getText().toString());
        request.put("cardCVV", this.cardCVV.getText().toString());

        if (this.customerAddress.getText().length() > 0) {
            request.put("customerAddress", this.customerAddress.getText().toString());
        }

        if (this.customerPostCode.getText().length() > 0) {
            request.put("customerPostCode", this.customerPostCode.getText().toString());
        }
        request.put("customerEmail", "info@example.com");
        request.put("countryCode", "826"); // GB
        request.put("currencyCode", "826"); // GBP
        request.put("type", "1"); // E-commerce
        request.put("orderRef", "test001");

        new MyTask(this, request).execute();

    }

    private class MyTask extends AsyncTask<Void, Void, Map<String, String>> {
        WeakReference<DirectPaymentActivity> activityReference;
        HashMap<String, String> request;

        ProgressDialog progress;
        AlertDialog.Builder alert;

        // only retain a weak reference to the activity
        MyTask(DirectPaymentActivity context, HashMap<String, String> request1) {
            activityReference = new WeakReference<>(context);
            alert = new AlertDialog.Builder(context);
            request = request1;
            progress = new ProgressDialog(context);
        }

        @Override
        protected void onPreExecute() {
            progress.setMessage("Please wait");
            progress.show();
        }

        @Override
        protected Map<String, String> doInBackground(final Void... params) {
            try {
                Log.i(TAG, request.toString());
                final Map<String, String> response = gateway.directRequest(request);

                for (final String field : response.keySet()) {
                    Log.i(TAG, field + " = " + response.get(field));
                }

                return response;

            } catch (final Exception e) {
                Log.e(TAG, "Gateway submit failed", e);

                final Map<String, String> error = new HashMap<String, String>();

                error.put("responseMessage", e.getMessage());
                error.put("state", e.getClass().getName());

                return error;
            }
        }

        @Override
        protected void onPostExecute(final Map<String, String> response) {

            if (progress.isShowing()) {
                progress.hide();
            }

            if (response.containsKey("responseMessage")) {
                alert.setMessage(response.get("responseMessage"));
            }

            if (response.containsKey("state")) {
                alert.setTitle(response.get("state"));
            } else {
                alert.setTitle("???");
            }

            alert.setCancelable(false);
            alert.setPositiveButton("OK", null);
            alert.create().show();
        }

    }

}
