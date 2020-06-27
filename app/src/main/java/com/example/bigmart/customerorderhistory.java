package com.example.bigmart;

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
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class customerorderhistory extends AppCompatActivity {

    private List<Orders> orders;
    ListView ordersList;
    private long userID;
    private int count = 0;
    private String orderID = "test";
    private Boolean flag = false;
    LinearLayout emptyPage, orderListPage;

    public void goToHome(){
        Intent homeIntent = new Intent(customerorderhistory.this, home.class);
        Bundle extras = new Bundle();
        extras.putLong("userID", userID);
        homeIntent.putExtras(extras);
        startActivity(homeIntent);
        finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_orderdisplay);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitleTextColor(getColor(R.color.colorPrimaryDark));

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_back);

        Bundle b = getIntent().getExtras();
        userID = b.getLong("userID");

        emptyPage = findViewById(R.id.layout_orderhistory_empty);
        orderListPage = findViewById(R.id.layout_orderhistory_list);

        orders = new ArrayList<Orders>();
        ordersList = findViewById(R.id.listcustomerOrderHistory);
        final FirebaseDatabase database = FirebaseDatabase.getInstance("https://bigmart-sinprl.firebaseio.com/");
        DatabaseReference productReference = database.getReference("Orders/");
        Query query = productReference.orderByKey();
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                //if (dataSnapshot.exists()) {
                List<Orders> orders = new ArrayList<Orders>();
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    Orders order = postSnapshot.getValue(Orders.class);
                    if (order.userID == userID)
                        orders.add(order);
                }
                Collections.reverse(orders);
                if(orders.size() != 0){
                    orderListPage.setVisibility(View.VISIBLE);
                    emptyPage.setVisibility(View.GONE);
                    adapterOrder orderAdapter = new adapterOrder(customerorderhistory.this, R.layout.itemorder, orders, userID, 2);
                    ordersList.setAdapter(orderAdapter);
                }else
                {
                    emptyPage.setVisibility(View.VISIBLE);
                    orderListPage.setVisibility(View.GONE);
                    /*Toast error = Toast.makeText(customerorderhistory.this, "No Orders to Show",Toast.LENGTH_SHORT);
                    error.setGravity(Gravity.TOP, 0, 0);
                    error.show();
                    finish();*/
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {            }
        });

        //ordersList = findViewById(R.id.listShopOwnerOrder);

        Button continueShopping = findViewById(R.id.but_orderhistory_continueShopping);
        continueShopping.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToHome();
            }
        });

        ordersList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent orderIntent = new Intent(customerorderhistory.this, customerorderdetails.class);
                Bundle extras = new Bundle();
                //extras.putString("orderID", ""+orders.get(position).ID);
                String selected = ((TextView) view.findViewById(R.id.txt_order_ID_DUP)).getText().toString();
                extras.putString("orderID", ""+selected);
                orderIntent.putExtras(extras);
                startActivity(orderIntent);
            }
        });



    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();
        if (id == android.R.id.home) {
            goToHome();
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        goToHome();
        super.onBackPressed();
    }
}
