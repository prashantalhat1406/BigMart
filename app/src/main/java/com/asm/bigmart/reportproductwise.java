package com.asm.bigmart;

import android.content.Intent;
import android.os.Bundle;

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
    String categoryNames[];
    Integer count_outostock=0,count_nodiscount=0;
    Integer count_cat1=0,count_cat2=0,count_cat3=0,count_cat4=0,count_cat5=0,count_cat6=0;
    private  int count = 0;
    private static final int[] LAYOUT_IDS = {
            R.id.layout_report_product_category1,
            R.id.layout_report_product_category2,
            R.id.layout_report_product_category3,
            R.id.layout_report_product_category4,
            R.id.layout_report_product_category5,
            R.id.layout_report_product_category6
    };

    private static final int[] CATEGORYNAME_IDS = {
            R.id.txt_report_product_category1,
            R.id.txt_report_product_category2,
            R.id.txt_report_product_category3,
            R.id.txt_report_product_category4,
            R.id.txt_report_product_category5,
            R.id.txt_report_product_category6
    };

    private static final int[] CATEGORYNAMECOUNT_IDS = {
            R.id.txt_report_product_category1_no,
            R.id.txt_report_product_category2_no,
            R.id.txt_report_product_category3_no,
            R.id.txt_report_product_category4_no,
            R.id.txt_report_product_category5_no,
            R.id.txt_report_product_category6_no
    };



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

        /*Query categoryQuery = database.getReference("/Categories");
        categoryQuery.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {


                Category category = dataSnapshot.getValue(Category.class);
                categoryNames[count] = category.Name;
                count++;
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {       }
        });*/



        DatabaseReference productReference = database.getReference("Products/");
        Query query = productReference.orderByKey();
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                count_outostock = 0;
                count_cat1=count_cat2=count_cat3=count_cat4=count_cat5=count_cat6=0;
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

                    switch(product.Category){
                        case "Dairy" : count_cat1++; break;
                        case "Grocery" : count_cat2++; break;
                        case "HomeKitchen" : count_cat3++; break;
                        case "SkinCare" : count_cat4++; break;
                        case "PersonalCare" : count_cat5++; break;
                        case "PackagedFoods" : count_cat6++; break;
                    }
                }

                TextView txtDairy = findViewById(R.id.txt_report_product_category1_no);
                txtDairy.setText("" + count_cat1);

                TextView txtGrocery = findViewById(R.id.txt_report_product_category2_no);
                txtGrocery.setText("" + count_cat2);

                TextView txtHomeKitchen = findViewById(R.id.txt_report_product_category3_no);
                txtHomeKitchen.setText("" + count_cat3);

                TextView txtSkinCare = findViewById(R.id.txt_report_product_category4_no);
                txtSkinCare.setText("" + count_cat4);

                TextView txtPersonalCare = findViewById(R.id.txt_report_product_category5_no);
                txtPersonalCare.setText("" + count_cat5);

                TextView txtPackagedFoods = findViewById(R.id.txt_report_product_category6_no);
                txtPackagedFoods.setText("" + count_cat6);

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