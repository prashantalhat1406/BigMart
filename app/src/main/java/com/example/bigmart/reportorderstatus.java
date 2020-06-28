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

import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ListView;
import android.widget.RadioGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class reportorderstatus extends AppCompatActivity {

    private List<Orders> orders;
    private Integer position=0;
    private String searchItem="";
    private List<String> orderIDs;
    ListView ordersList;
    private long userID;
    private int count = 0;
    private String orderID = "test";
    private Boolean flag = false;
    private FirebaseDatabase database;
    AutoCompleteTextView autoCompleteTextView;
    String orderStatus = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reportorderstatus);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        toolbar.setTitleTextColor(getColor(R.color.colorPrimaryDark));

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_back);

        Bundle b = getIntent().getExtras();
        orderStatus = b.getString("orderStatus","12");


        orders = new ArrayList<Orders>();
        orderIDs = new ArrayList<String>();
        ordersList = findViewById(R.id.listReportOrderStatus);
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
                    if (order.status.equals(orderStatus))
                    {
                        orders.add(order);
                        orderIDs.add(order.ID);
                    }
                }

                adapterReportOrderHistory orderAdapter = new adapterReportOrderHistory(reportorderstatus.this,R.layout.itemordershopowner,orders);
                ordersList.setAdapter(orderAdapter);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        autoCompleteTextView = findViewById(R.id.auto_reportorderstatus_orderid);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,R.layout.support_simple_spinner_dropdown_item,orderIDs);
        autoCompleteTextView.setAdapter(adapter);
        autoCompleteTextView.setThreshold(1);
        autoCompleteTextView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {


                Object t = parent.getItemAtPosition(position);
                for (Orders order : orders) {
                    if( order.ID.equals(t.toString())){
                        Intent orderIntent = new Intent(reportorderstatus.this, shopownerorderdetails.class);
                        Bundle extras = new Bundle();
                        extras.putString("orderID", ""+order.ID);
                        extras.putInt("position", position);
                        extras.putString("searchItem", autoCompleteTextView.getText().toString());
                        orderIntent.putExtras(extras);
                        startActivityForResult(orderIntent,100);
                        autoCompleteTextView.setText("");
                        break;
                    }
                }


            }
        });


        ordersList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent orderIntent = new Intent(reportorderstatus.this, shopownerorderdetails.class);
                Bundle extras = new Bundle();
                //extras.putString("orderID", ""+orders.get(position).ID);
                String selected = ((TextView) view.findViewById(R.id.txt_reportorder_ID_DUP)).getText().toString();
                extras.putString("orderID", ""+selected);
                extras.putInt("position", position);
                extras.putString("searchItem", autoCompleteTextView.getText().toString());
                orderIntent.putExtras(extras);
                startActivityForResult(orderIntent,100);
            }
        });

    }
}