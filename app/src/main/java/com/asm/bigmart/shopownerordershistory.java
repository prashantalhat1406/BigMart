package com.asm.bigmart;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;

import com.asm.bigmart.adapters.SO_OrderDisplay;
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

import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioGroup;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class shopownerordershistory extends AppCompatActivity implements DatePickerDialog.OnDateSetListener {

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
    Button back, next;
    AutoCompleteTextView autoCompleteTextView;
    String baseDate="";
    RadioGroup statusRadioGroup;
    TextView txtCurrentSelection;
    LinearLayout empty,orderslist;

    public void displayFilteredList(int radioButton, String  baseDate)
    {

        List<Orders> orders_statuswise;
        orders_statuswise = new ArrayList<Orders>();
        switch (radioButton){
            case R.id.rdbAll:
                orders_statuswise =orders;

                break;
            case R.id.rdbInProgress:
                for (Orders order : orders) {
                    if (order.status.equals("InProgress"))
                        orders_statuswise.add(order);
                }
                break;
            case R.id.rdbComplete:
                for (Orders order : orders) {
                    if (order.status.equals("Complete"))
                        orders_statuswise.add(order);
                }
                break;
            case R.id.rdbCreated:
                for (Orders order : orders) {
                    if (order.status.equals("Created"))
                        orders_statuswise.add(order);
                }
                break;
        }

        List<Orders> orders_datewise;
        orders_datewise = new ArrayList<Orders>();
        for (Orders order : orders_statuswise) {
            if (order.getDate().equals(baseDate))
                orders_datewise.add(order);
        }


        if (orders_datewise.size() > 0)
        {
            orderslist.setVisibility(View.VISIBLE);
            empty.setVisibility(View.GONE);
        }else {
            orderslist.setVisibility(View.GONE);
            empty.setVisibility(View.VISIBLE);
        }

        SO_OrderDisplay orderAdapter = new SO_OrderDisplay(shopownerordershistory.this,R.layout.itemordershopowner,orders_datewise, userID,2);
        ordersList.setAdapter(orderAdapter);
        ordersList.setSelection(position);
    }

    @Override
    protected void onResume() {
        RadioGroup rg = findViewById(R.id.rdbGroup);
        displayFilteredList(rg.getCheckedRadioButtonId(),baseDate);
        super.onResume();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shopownerordershistory);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        toolbar.setTitleTextColor(getColor(R.color.colorPrimaryDark));

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_back);



        orders = new ArrayList<Orders>();
        orderIDs = new ArrayList<String>();
        ordersList = findViewById(R.id.listShopOwnerOrder);

        empty = findViewById(R.id.layout_so_orderhistory_empty);
        orderslist = findViewById(R.id.layout_so_orderhistory_list);

        database = FirebaseDatabase.getInstance("https://bigmart-sinprl.firebaseio.com/");
        DatabaseReference productReference = database.getReference("Orders/");
        Query query = productReference.orderByKey();
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                orders = new ArrayList<Orders>();
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    Orders order = postSnapshot.getValue(Orders.class);
                    orders.add(order);
                    orderIDs.add(order.ID);
                }



                if (orders.size() > 0)
                {
                    orderslist.setVisibility(View.VISIBLE);
                    empty.setVisibility(View.GONE);
                }else {
                    orderslist.setVisibility(View.GONE);
                    empty.setVisibility(View.VISIBLE);
                }

                SO_OrderDisplay orderAdapter = new SO_OrderDisplay(shopownerordershistory.this,R.layout.itemordershopowner,orders, userID,2);
                ordersList.setAdapter(orderAdapter);
                RadioGroup rg = findViewById(R.id.rdbGroup);
                rg.findViewById(R.id.rdbAll).setSelected(true);
                displayFilteredList(statusRadioGroup.getCheckedRadioButtonId(), baseDate);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        autoCompleteTextView = findViewById(R.id.auto_shopownerorderhistory_orderid);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,R.layout.support_simple_spinner_dropdown_item,orderIDs);
        autoCompleteTextView.setAdapter(adapter);
        autoCompleteTextView.setThreshold(1);

        autoCompleteTextView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {


                Object t = parent.getItemAtPosition(position);

                for (Orders order : orders) {
                    if( order.ID.equals(t.toString())){
                        Intent orderIntent = new Intent(shopownerordershistory.this, shopownerorderdetails.class);
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
                Intent orderIntent = new Intent(shopownerordershistory.this, shopownerorderdetails.class);
                Bundle extras = new Bundle();
                String selected = ((TextView) view.findViewById(R.id.txt_orderN_ID_DUP)).getText().toString();
                extras.putString("orderID", ""+selected);
                extras.putInt("position", position);
                extras.putString("searchItem", autoCompleteTextView.getText().toString());
                orderIntent.putExtras(extras);
                startActivityForResult(orderIntent,100);
            }
        });

        statusRadioGroup = findViewById(R.id.rdbGroup);

        statusRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                displayFilteredList(checkedId, baseDate);
            }
        });

        final Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);
        final DatePickerDialog datePickerDialog = new DatePickerDialog(
                shopownerordershistory.this, shopownerordershistory.this, year, month, day);


        baseDate = new SimpleDateFormat("dd-MMM-yyyy").format(Calendar.getInstance().getTime());
        txtCurrentSelection = findViewById(R.id.txt_soorderhistory_currentselection);
        txtCurrentSelection.setText(baseDate);

        txtCurrentSelection.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                datePickerDialog.show();
            }
        });

        back = findViewById(R.id.but_soorderhistory_back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar currentCalendar = Calendar.getInstance();

                try {
                    currentCalendar.setTime(new SimpleDateFormat("dd-MMM-yyyy").parse(baseDate));
                    currentCalendar.add(Calendar.DAY_OF_MONTH,-1);
                    baseDate = new SimpleDateFormat("dd-MMM-yyyy").format(currentCalendar.getTime());
                    txtCurrentSelection.setText(baseDate);
                    displayFilteredList(statusRadioGroup.getCheckedRadioButtonId(),baseDate);
                }catch (Exception e){}

            }
        });

        next = findViewById(R.id.but_soorderhistory_next);
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar currentCalendar = Calendar.getInstance();

                try {
                    currentCalendar.setTime(new SimpleDateFormat("dd-MMM-yyyy").parse(baseDate));
                    currentCalendar.add(Calendar.DAY_OF_MONTH,1);
                    baseDate = new SimpleDateFormat("dd-MMM-yyyy").format(currentCalendar.getTime());
                    txtCurrentSelection.setText(baseDate);

                    displayFilteredList(statusRadioGroup.getCheckedRadioButtonId(),baseDate);
                }catch (Exception e){}

            }
        });


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 100)
        {
            position = data.getIntExtra("position",0);
            searchItem = data.getStringExtra("searchItem");
            autoCompleteTextView.setText("");
            ordersList.setSelection( position);

        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }



    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(year,month,dayOfMonth);
        txtCurrentSelection.setText(new SimpleDateFormat("dd-MMM-yyyy").format(calendar.getTime()));
        baseDate = new SimpleDateFormat("dd-MMM-yyyy").format(calendar.getTime());

        displayFilteredList(statusRadioGroup.getCheckedRadioButtonId(),baseDate);
    }
}
