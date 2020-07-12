package com.asm.bigmart;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

public class shopownerhome extends AppCompatActivity {

    public void showLogoutAlertDialog(){

        /*AlertDialog.Builder logoutAlertBuilder = new AlertDialog.Builder(shopownerhome.this);
        logoutAlertBuilder.setMessage("Are you sure to Logout ?");
        logoutAlertBuilder.setCancelable(false);
        logoutAlertBuilder.setPositiveButton(
                "YES", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent logoutIntent = new Intent(shopownerhome.this, login.class);
                        logoutIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK |Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(logoutIntent);
                        finish();
                    }
                });
        logoutAlertBuilder.setNegativeButton(
                "NO", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
        final AlertDialog alertLogout = logoutAlertBuilder.create();

        alertLogout.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialog) {
                alertLogout.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(Color.RED);
                alertLogout.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(getResources().getColor(R.color.darkgreenColorButton));
            }
        });

        alertLogout.setTitle("LOGOUT");
        alertLogout.show();*/

        final Dialog dialog = new Dialog(shopownerhome.this);
        dialog.setContentView(R.layout.logoutdialog);
        dialog.setCancelable(false);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        Button yes = dialog.findViewById(R.id.dialog_btn_red);
        yes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent logoutIntent = new Intent(shopownerhome.this, login.class);
                logoutIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK |Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(logoutIntent);
                finish();
            }
        });
        Button no = dialog.findViewById(R.id.dialog_btn_green);
        no.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shopownerhome);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitleTextColor(getColor(R.color.colorPrimaryDark));

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_back);

        Button order = findViewById(R.id.but_shop_order);
        order.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent shopIntent = new Intent(shopownerhome.this, shopownerordershistory.class);
                startActivity(shopIntent);
            }
        });

        Button products = findViewById(R.id.but_shop_products);
        products.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent shopIntent = new Intent(shopownerhome.this, shopownerproductdisplay.class);
                startActivity(shopIntent);
            }
        });

        Button reports = findViewById(R.id.but_shop_report);
        reports.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent shopIntent = new Intent(shopownerhome.this, reporthome.class);
                startActivity(shopIntent);
            }
        });

        Button users = findViewById(R.id.but_shop_user);
        users.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent userIntent = new Intent(shopownerhome.this, shopowneruserdisplay.class);
                startActivity(userIntent);
            }
        });


    }
    @Override
    public void onBackPressed() {
        showLogoutAlertDialog();

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }
}
