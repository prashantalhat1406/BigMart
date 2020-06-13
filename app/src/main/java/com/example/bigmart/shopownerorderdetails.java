package com.example.bigmart;

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

import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class shopownerorderdetails extends AppCompatActivity {

    private String orderID;
    private List<Product> products;
    ListView productList;
    Orders orderDetail;
    Button butComplete, butConfirm, butCancel,butPrint;
    User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_orderdetaildisplay);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        final FirebaseDatabase database = FirebaseDatabase.getInstance("https://bigmart-sinprl.firebaseio.com/");

        Bundle b = getIntent().getExtras();
        orderID = b.getString("orderID","12");

        productList = findViewById(R.id.listOrderDetail);
        products = new ArrayList<Product>();

        orderDetail = new Orders();
        user = new User();


        butPrint = findViewById(R.id.but_orderdetail_print);

        butComplete = findViewById(R.id.but_orderdetail_complete);
        butComplete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatabaseReference orderReference = database.getReference("Orders/").child(""+orderID);
                Map<String, Object> statusUpdate = new HashMap<>();
                statusUpdate.put("status", "Complete");
                orderReference.updateChildren(statusUpdate);
                finish();
            }
        });

        butConfirm = findViewById(R.id.but_orderdetail_confirm);
        butConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatabaseReference orderReference = database.getReference("Orders/").child(""+orderID);
                Map<String, Object> statusUpdate = new HashMap<>();
                statusUpdate.put("status", "InProgress");
                orderReference.updateChildren(statusUpdate);
                butComplete.setVisibility(View.VISIBLE);
                butPrint.setVisibility(View.VISIBLE);
                butConfirm.setVisibility(View.GONE);
                butCancel.setVisibility(View.GONE);
                //finish();
            }
        });

        butCancel = findViewById(R.id.but_orderdetail_cancel);
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

                for (DataSnapshot postSnapshot: dataSnapshot.getChildren()) {
                    Product product = postSnapshot.getValue(Product.class);
                    product.setID(postSnapshot.getKey());
                    products.add(product);
                }
                adapterOrderDetails productAdaper = new adapterOrderDetails(shopownerorderdetails.this, R.layout.itemorderdetails, products);
                productList.setAdapter(productAdaper);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });


        TextView txtorderID = findViewById(R.id.txt_orderdetails_orderID);
        txtorderID.setText(""+orderID.substring(orderID.length() - 5).toUpperCase());



        Query queryOrder = database.getReference("/Orders/"+orderID);
        queryOrder.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                orderDetail = dataSnapshot.getValue(Orders.class);
                TextView orderDate = findViewById(R.id.txt_orderdetails_orderDate);
                orderDate.setText(""+orderDetail.date);
                switch (orderDetail.status)
                {
                    case "Complete":
                        butComplete.setVisibility(View.VISIBLE);
                        butPrint.setVisibility(View.VISIBLE);
                        butConfirm.setVisibility(View.GONE);
                        butCancel.setVisibility(View.GONE);
                        butComplete.setEnabled(false);
                        butPrint.setEnabled(true);
                        break;
                    case "InProgress":
                        butComplete.setVisibility(View.VISIBLE);
                        butPrint.setVisibility(View.VISIBLE);
                        butConfirm.setVisibility(View.GONE);
                        butCancel.setVisibility(View.GONE);
                        butComplete.setEnabled(true);
                        butPrint.setEnabled(true);
                        break;
                    case "Created":
                        butComplete.setVisibility(View.GONE);
                        butPrint.setVisibility(View.GONE);
                        butConfirm.setVisibility(View.VISIBLE);
                        butCancel.setVisibility(View.VISIBLE);
                        butCancel.setEnabled(true);
                        butConfirm.setEnabled(true);
                        break;
                    case "Cancelled":
                        butComplete.setVisibility(View.GONE);
                        butPrint.setVisibility(View.GONE);
                        butConfirm.setVisibility(View.VISIBLE);
                        butCancel.setVisibility(View.VISIBLE);
                        butCancel.setEnabled(false);
                        butConfirm.setEnabled(false);
                        break;

                }
                /*if(orderDetail.status.equals("Complete"))
                    butComplete.setEnabled(false);
                else
                    butComplete.setEnabled(true);*/
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });

        final TextView name = findViewById(R.id.txt_orderdetails_customerName);
        final TextView mobile = findViewById(R.id.txt_orderdetails_customerMobile);
        final TextView address = findViewById(R.id.txt_orderdetails_customeraddress);

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
                        address.setText("" + u.Address1 + " , " + u.Address2);
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {            }
        });



    }

    @Override
    public void onBackPressed() {
        finish();
        super.onBackPressed();
    }
}
