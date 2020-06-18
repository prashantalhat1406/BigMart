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
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ListView;
import android.widget.RadioGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class shopownerordershistory extends AppCompatActivity {

    private List<Orders> orders;
    private Integer position=0;
    private List<String> orderIDs;
    ListView ordersList;
    private long userID;
    private int count = 0;
    private String orderID = "test";
    private Boolean flag = false;
    private FirebaseDatabase database;

    public void displayFilteredList(int radioButton)
    {

        List<Orders> orders_filter;
        orders_filter = new ArrayList<Orders>();
        switch (radioButton){
            case R.id.rdbAll:
                orders_filter =orders;

                break;
            case R.id.rdbInProgress:
                for (Orders order : orders) {
                    if (order.status.equals("InProgress"))
                        orders_filter.add(order);
                }
                break;
            case R.id.rdbComplete:
                for (Orders order : orders) {
                    if (order.status.equals("Complete"))
                        orders_filter.add(order);
                }
                break;
            case R.id.rdbCreated:
                for (Orders order : orders) {
                    if (order.status.equals("Created"))
                        orders_filter.add(order);
                }
                break;
        }


        //Collections.reverse(orders_filter);
        adapterOrder orderAdapter = new adapterOrder(shopownerordershistory.this,R.layout.itemorder,orders_filter, userID,2);
        ordersList.setAdapter(orderAdapter);
        ordersList.setSelection(position);
    }

    @Override
    protected void onResume() {
        RadioGroup rg = findViewById(R.id.rdbGroup);
        displayFilteredList(rg.getCheckedRadioButtonId());
        super.onResume();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shopownerordershistory);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);



        orders = new ArrayList<Orders>();
        orderIDs = new ArrayList<String>();
        //orders_filter = new ArrayList<Orders>();
        ordersList = findViewById(R.id.listShopOwnerOrder);
        database = FirebaseDatabase.getInstance("https://bigmart-sinprl.firebaseio.com/");
        DatabaseReference productReference = database.getReference("Orders/");
        Query query = productReference.orderByKey();
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                orders = new ArrayList<Orders>();
                for (DataSnapshot postSnapshot: dataSnapshot.getChildren())
                {
                    Orders order = postSnapshot.getValue(Orders.class);
                    orders.add(order);
                    orderIDs.add(order.ID.substring(order.ID.length() - 5).toUpperCase());
                    //orders_filter.add(order);
                }

                //Collections.reverse(orders);
                adapterOrder orderAdapter = new adapterOrder(shopownerordershistory.this,R.layout.itemorder,orders, userID,2);
                ordersList.setAdapter(orderAdapter);
                RadioGroup rg = findViewById(R.id.rdbGroup);
                rg.findViewById(R.id.rdbAll).setSelected(true);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        //orders_filter =orders;

        final AutoCompleteTextView autoCompleteTextView = findViewById(R.id.auto_shopownerorderhistory_orderid);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,R.layout.support_simple_spinner_dropdown_item,orderIDs);
        autoCompleteTextView.setAdapter(adapter);
        autoCompleteTextView.setThreshold(1);
        //autoCompleteTextView.setTextColor(Color.RED);

        autoCompleteTextView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {


                Object t = parent.getItemAtPosition(position);
                //Toast.makeText(shopownerordershistory.this,t.toString(),Toast.LENGTH_SHORT).show();
                for (Orders order : orders) {
                    if( order.ID.substring(order.ID.length() - 5).toUpperCase().equals(t.toString())){
                        Intent orderIntent = new Intent(shopownerordershistory.this, shopownerorderdetails.class);
                        Bundle extras = new Bundle();
                        extras.putString("orderID", ""+order.ID);
                        extras.putInt("position", position);
                        orderIntent.putExtras(extras);
                        startActivityForResult(orderIntent,100);
                        autoCompleteTextView.setText("");
                    }
                }
                /*Intent orderIntent = new Intent(shopownerordershistory.this, shopownerorderdetails.class);
                Bundle extras = new Bundle();
                String selected = ((TextView) view.findViewById(R.id.txt_order_ID_DUP)).getText().toString();
                extras.putString("orderID", ""+selected);
                orderIntent.putExtras(extras);
                startActivity(orderIntent);*/

            }
        });


        ordersList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent orderIntent = new Intent(shopownerordershistory.this, shopownerorderdetails.class);
                Bundle extras = new Bundle();
                //extras.putString("orderID", ""+orders.get(position).ID);
                String selected = ((TextView) view.findViewById(R.id.txt_order_ID_DUP)).getText().toString();
                extras.putString("orderID", ""+selected);
                extras.putInt("position", position);
                orderIntent.putExtras(extras);
                startActivityForResult(orderIntent,100);
            }
        });

        RadioGroup rg = findViewById(R.id.rdbGroup);

        rg.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                displayFilteredList(checkedId);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 100)
        {
            position = data.getIntExtra("position",0);
            ordersList.setSelection( position);

        }
    }
}
