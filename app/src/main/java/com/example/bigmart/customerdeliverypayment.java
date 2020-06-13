package com.example.bigmart;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;


import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.text.Html;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

public class customerdeliverypayment extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customerdeliverypayment);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        TextView pickup = findViewById(R.id.txt_pickup);
        pickup.setText(Html.fromHtml(getString(R.string.pickup)));

        TextView homedelivery = findViewById(R.id.txt_homedelivery);
        homedelivery.setText(Html.fromHtml(getString(R.string.homedelivery)));

        Button butpickup = findViewById(R.id.but_pickup);
        butpickup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LinearLayout pu = findViewById(R.id.layoutPickUP);
                pu.setBackground(getDrawable( R.drawable.roundbutton_white));

                LinearLayout hd = findViewById(R.id.layoutHomeDelivery);
                hd.setBackground(getDrawable(R.drawable.roundbutton_grey));

                Intent intent=new Intent();
                intent.putExtra("type","Pick Up");
                setResult(Activity.RESULT_OK, intent);
                finish();

            }
        });

        Button buthomedelivery = findViewById(R.id.but_homedelivery);
        buthomedelivery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LinearLayout pu = findViewById(R.id.layoutPickUP);
                pu.setBackground(getDrawable( R.drawable.roundbutton_grey));

                LinearLayout hd = findViewById(R.id.layoutHomeDelivery);
                hd.setBackground(getDrawable(R.drawable.roundbutton_white));
                Intent intent=new Intent();
                intent.putExtra("type","Home Delivery");
                setResult(Activity.RESULT_OK, intent);
                finish();

            }
        });


    }

    @Override
    public void onBackPressed() {
        Intent intent=new Intent();
        intent.putExtra("type","");
        setResult(Activity.RESULT_CANCELED, intent);
        finish();
        super.onBackPressed();
    }
}
