package com.asm.bigmart;

import android.content.Intent;
import android.os.Bundle;

import com.asm.bigmart.adapters.SO_Report_OrderDisplay;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ListView;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class reportorderstatus extends AppCompatActivity {

    private List<Orders> orders;
    private Integer position=0;
    private String searchItem="";
    private List<String> orderIDs;
    ListView ordersList;
    private long userID;
    private int period = 0;
    private String baseDate = "test";
    private Boolean flag = false;
    private FirebaseDatabase database;
    AutoCompleteTextView autoCompleteTextView;
    String orderStatus = "";


    public boolean isDateInCurrentWeek(Date date,String baseDate) {

        Calendar currentCalendar = Calendar.getInstance();

        try {
            currentCalendar.setTime(new SimpleDateFormat("dd-MMM-yyyy").parse(baseDate));
        }catch (Exception e){}

        Date min, max;
        min = currentCalendar.getTime();
        currentCalendar.add(Calendar.DAY_OF_MONTH, 6);
        max = currentCalendar.getTime();
        return date.compareTo(min) >= 0 && date.compareTo(max) <= 0;

    }

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
        baseDate = b.getString("baseDate","");
        period = b.getInt("period",-1);

        setTitle(getTitle() + " : " + orderStatus.toUpperCase());


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
                    if (orderStatus.equals("All"))
                    {
                        orders.add(order);
                        orderIDs.add(order.ID);
                    }else
                    {
                        if (order.status.equals(orderStatus))
                        {
                            orders.add(order);
                            orderIDs.add(order.ID);
                        }
                    }

                }

                List<Orders> tempOrders = new ArrayList<>();

                switch (period){
                    case 0:
                        for (Orders order : orders) {
                            if (order.date.equals(baseDate))
                                tempOrders.add(order);
                        }
                        break;
                    case 1:
                        for (Orders order : orders) {
                            try{
                                if (isDateInCurrentWeek(new SimpleDateFormat("dd-MMM-yyyy").parse(order.date),baseDate)) {
                                    tempOrders.add(order);
                                }
                            }catch (Exception e){}
                        }
                        break;
                    case 2:
                        for (Orders order : orders) {
                            if (order.date.split("-")[1].equals(baseDate.split("-")[1]))
                                tempOrders.add(order);
                        }
                        break;
                }

                //adapterReportOrderHistory orderAdapter = new adapterReportOrderHistory(reportorderstatus.this,R.layout.itemordershopowner,tempOrders);
                SO_Report_OrderDisplay orderAdapter = new SO_Report_OrderDisplay(reportorderstatus.this,R.layout.itemordershopowner,tempOrders);
                ordersList.setAdapter(orderAdapter);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        autoCompleteTextView = findViewById(R.id.auto_reportorderstatus_orderid);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, androidx.appcompat.R.layout.support_simple_spinner_dropdown_item,orderIDs);
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

        autoCompleteTextView.clearFocus();


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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();


        if (id == android.R.id.home) {
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }
}