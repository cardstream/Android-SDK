package com.paymentnetwork.paymentdemo;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    static final String TAG = MainActivity.class.getName();

    @Override
    protected void onCreate(final Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        this.setContentView(R.layout.activity_main);

    }

    public void hostedPayment(final View view){
        Intent intent = new Intent(this, HostedPaymentActivity.class);
        startActivity(intent);
    }

    public void directPayment(final View view){
        Intent intent = new Intent(this, DirectPaymentActivity.class);
        startActivity(intent);
    }

}