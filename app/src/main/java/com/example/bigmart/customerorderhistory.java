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
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class customerorderhistory extends AppCompatActivity {

    private List<Orders> orders;
    ListView ordersList;
    private long userID;
    private int count = 0;
    private String orderID = "test";
    private Boolean flag = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_orderdisplay);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Bundle b = getIntent().getExtras();
        userID = b.getLong("userID");

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
                if(orders.size() != 0){
                    adapterOrder orderAdapter = new adapterOrder(customerorderhistory.this, R.layout.itemorder, orders, userID, 2);
                    ordersList.setAdapter(orderAdapter);
                }else
                {
                    Toast error = Toast.makeText(customerorderhistory.this, "No Orders to Show",Toast.LENGTH_SHORT);
                    error.setGravity(Gravity.TOP, 0, 0);
                    error.show();
                    finish();
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {            }
        });

        //ordersList = findViewById(R.id.listShopOwnerOrder);

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

}
