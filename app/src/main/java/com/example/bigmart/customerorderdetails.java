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
import androidx.core.content.ContextCompat;

import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class customerorderdetails extends AppCompatActivity {

    private String orderID;
    private List<Product> products;
    ListView productList;
    Orders orderDetail;
    Button butComplete, butConfirm, butCancel,butPrint;
    User user;
    Double TotalPrice = 0.0;
    Integer savePrice = 0;
    TextView txtorderStatus;
    LinearLayout linearLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customerorderdetails);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitleTextColor(getColor(R.color.colorPrimaryDark));

        final FirebaseDatabase database = FirebaseDatabase.getInstance("https://bigmart-sinprl.firebaseio.com/");

        Bundle b = getIntent().getExtras();
        orderID = b.getString("orderID","12");

        final DecimalFormat formater = new DecimalFormat("0.00");

        productList = findViewById(R.id.listcustomerOrderDetail);
        products = new ArrayList<Product>();

        orderDetail = new Orders();
        user = new User();

        txtorderStatus = findViewById(R.id.txt_customer_orderdetails_orderstatus);
        linearLayout = findViewById(R.id.layout_custOrderDetails_status);

        butCancel = findViewById(R.id.but_customer_orderdetail_cancel);
        butCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatabaseReference orderReference = database.getReference("Orders/").child(""+orderID);
                Map<String, Object> statusUpdate = new HashMap<>();
                statusUpdate.put("status", "Cancelled");
                orderReference.updateChildren(statusUpdate);
                finish();
            }
        });

        Query query = database.getReference("Orders/"+orderID+"/Products/");
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                products.clear();
                savePrice = 0;

                for (DataSnapshot postSnapshot: dataSnapshot.getChildren()) {
                    Product product = postSnapshot.getValue(Product.class);
                    product.setID(postSnapshot.getKey());
                    products.add(product);

                    TotalPrice = TotalPrice +  (product.QtyNos * (product.MRP -  product.Discount.doubleValue()));
                    savePrice = savePrice + (product.QtyNos * product.Discount);

                    //TotalPrice = (Double) Math.round(TotalPrice);
                }
                //TotalPrice =  Math.round(TotalPrice);
                adapterOrderDetails productAdaper = new adapterOrderDetails(customerorderdetails.this, R.layout.itemorderdetails, products);
                productList.setAdapter(productAdaper);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });

        final TextView name = findViewById(R.id.txt_customer_orderdetails_customerName);
        final TextView mobile = findViewById(R.id.txt_customer_orderdetails_customerMobile);
        TextView txtorderID = findViewById(R.id.txt_customer_orderdetails_orderID);
        //txtorderID.setText(""+orderID.substring(orderID.length() - 5).toUpperCase());
        txtorderID.setText(""+orderID);



        Query queryOrder = database.getReference("/Orders/"+orderID);
        queryOrder.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                orderDetail = dataSnapshot.getValue(Orders.class);
                TextView orderDate = findViewById(R.id.txt_customer_orderdetails_orderDate);
                orderDate.setText(""+orderDetail.date);

                TextView totalAmount = findViewById(R.id.txt_customer_orderdetails_totoalAmount);

                if (orderDetail.deliveryType.equals("Home Delivery"))
                    if (TotalPrice < 2000)
                        totalAmount.setText("Total : " + customerorderdetails.this.getResources().getString(R.string.Rupee) + " "+formater.format( Math.round( TotalPrice)) + " + " + customerorderdetails.this.getResources().getString(R.string.Rupee) + " 50" );
                    else
                        totalAmount.setText("Total : " + customerorderdetails.this.getResources().getString(R.string.Rupee) + " "+formater.format( Math.round( TotalPrice)) + " + " + customerorderdetails.this.getResources().getString(R.string.Rupee) + " "+ formater.format(Math.round( ( TotalPrice *0.02))));
                else
                    totalAmount.setText("Total : " + customerorderdetails.this.getResources().getString(R.string.Rupee) + " "+formater.format( Math.round( TotalPrice)));

                TextView sAmount = findViewById(R.id.txt_customer_orderdetails_saveAmount);
                sAmount.setText("Savings : " + customerorderdetails.this.getResources().getString(R.string.Rupee) + " "+formater.format( savePrice));


                if(orderDetail.status.equals("Created")) {
                    butCancel.setVisibility(View.VISIBLE);
                    txtorderStatus.setVisibility(View.GONE);
                    linearLayout.setVisibility(View.GONE);
                    butCancel.setEnabled(true);
                }else{
                    butCancel.setVisibility(View.GONE);
                    //butCancel.setEnabled(false);
                    linearLayout.setVisibility(View.VISIBLE);
                    txtorderStatus.setVisibility(View.VISIBLE);
                    txtorderStatus.setText(orderDetail.status);
                    txtorderStatus.setTextColor(ContextCompat.getColor(customerorderdetails.this, R.color.completeStatus));
                    switch (orderDetail.status){
                        case "Complete": txtorderStatus.setBackground(customerorderdetails.this.getDrawable(R.drawable.status_complete));
                            break;
                        case "Created": txtorderStatus.setBackground(customerorderdetails.this.getDrawable(R.drawable.status_created));
                            break;
                        case "InProgress": txtorderStatus.setBackground(customerorderdetails.this.getDrawable(R.drawable.status_inprogress));
                            break;
                        case "Cancelled": txtorderStatus.setBackground(customerorderdetails.this.getDrawable(R.drawable.status_cancelled));
                            break;
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {            }
        });

        Query queryUser = database.getReference("/Users" );
        queryUser.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot postSnapshot: dataSnapshot.getChildren()) {
                    User u = postSnapshot.getValue(User.class);
                    if(u.Mobile.equals( orderDetail.userID))
                    {
                        name.setText("" + u.Name);
                        mobile.setText("" + u.Mobile);
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {            }
        });




    }

}
