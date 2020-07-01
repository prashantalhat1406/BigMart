package com.example.bigmart;

import android.content.Intent;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class reportproductwise extends AppCompatActivity {

    TextView txtOutOfStock, txtNoDiscount, txtTotal;
    private List<Product> products;
    private FirebaseDatabase database;
    Integer count_outostock=0,count_nodiscount=0;



    public void showErrorMessage(String message){
        Toast error = Toast.makeText(reportproductwise.this, message,Toast.LENGTH_SHORT);
        error.setGravity(Gravity.TOP, 0, 0);
        error.show();
    }

    public void showReportOrderList(String status){
        Intent productstatusIntent = new Intent(reportproductwise.this, reportproductstatus.class);
        Bundle extras = new Bundle();
        extras.putString("productStatus", status);
        productstatusIntent.putExtras(extras);
        startActivity(productstatusIntent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reportproductwise);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        toolbar.setTitleTextColor(getColor(R.color.colorPrimaryDark));

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_back);

        products = new ArrayList<>();
        txtOutOfStock = findViewById(R.id.txt_report_reportwise_outofstock);
        //txtNoDiscount = findViewById(R.id.txt_report_reportwise_nodiscount);
        txtTotal = findViewById(R.id.txt_report_reportwise_total);


        database = FirebaseDatabase.getInstance("https://bigmart-sinprl.firebaseio.com/");
        DatabaseReference productReference = database.getReference("Products/");
        Query query = productReference.orderByKey();
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                count_outostock = 0;
                products.clear();

                for (DataSnapshot postSnapshot: dataSnapshot.getChildren())
                {
                    Product product = postSnapshot.getValue(Product.class);
                    product.setID(postSnapshot.getKey());
                    products.add(product);
                    if (product.Qty < product.MinStock)
                        count_outostock = count_outostock + 1;

                    if (product.Discount == 0)
                        count_nodiscount = count_nodiscount + 1;
                }

                txtOutOfStock.setText(""+count_outostock);
                //txtNoDiscount.setText(""+count_nodiscount);
                txtTotal.setText("" + products.size());
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {   }
        });

        LinearLayout lloutofstock = findViewById(R.id.layout_report_product_outofstock);
        lloutofstock.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (Integer.parseInt( txtOutOfStock.getText().toString()) > 0)
                    showReportOrderList("OOS");
                else
                    showErrorMessage("No Products to show !");
            }
        });

        LinearLayout lltotal = findViewById(R.id.layout_report_product_total);
        lltotal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (Integer.parseInt( txtTotal.getText().toString()) > 0)
                    showReportOrderList("ALL");
                else
                    showErrorMessage("No Products to show !");
            }
        });


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