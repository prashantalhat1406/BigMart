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

import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class reportorderwise extends AppCompatActivity {

    private List<Orders> orders;
    private FirebaseDatabase database;
    Integer count_created=0,count_cancelled=0,count_inprogress=0,count_completed=0;
    TextView txtCreated, txtCancelled, txtCompleted,txtInProgress, txtTotal, txtCurrentSelection;
    boolean dailyFlag = false, weeklyFlag = false, monthlyFlag = false;

    public static boolean isDateInCurrentWeek(Date date) {
        Calendar currentCalendar = Calendar.getInstance();
        int week = currentCalendar.get(Calendar.WEEK_OF_YEAR);
        int year = currentCalendar.get(Calendar.YEAR);
        Calendar targetCalendar = Calendar.getInstance();
        targetCalendar.setTime(date);
        int targetWeek = targetCalendar.get(Calendar.WEEK_OF_YEAR);
        int targetYear = targetCalendar.get(Calendar.YEAR);
        return week == targetWeek && year == targetYear;
    }

    public String getCurrentWeek(){
        String currWeek = "";
        Calendar currentCalendar = Calendar.getInstance();
        currWeek = "" + new SimpleDateFormat("dd-MMM").format(currentCalendar.getTime());
        currentCalendar.add(Calendar.DAY_OF_MONTH, 7);
        currWeek = currWeek +" to "+ new SimpleDateFormat("dd-MMM").format(currentCalendar.getTime());

        return  currWeek;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reportorderwise);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        toolbar.setTitleTextColor(getColor(R.color.colorPrimaryDark));

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_back);


        orders = new ArrayList<Orders>();
        txtCancelled = findViewById(R.id.txt_report_orderwise_cancelled);
        txtCompleted = findViewById(R.id.txt_report_orderwise_completed);
        txtInProgress = findViewById(R.id.txt_report_orderwise_inprogress);
        txtCreated = findViewById(R.id.txt_report_orderwise_created);
        txtTotal = findViewById(R.id.txt_report_orderwise_total);
        txtCurrentSelection = findViewById(R.id.txt_report_currentselection);

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
                    switch (order.status){
                        case "Complete": count_completed = count_completed + 1;
                            break;
                        case "Created": count_created = count_created + 1;
                            break;
                        case "InProgress": count_inprogress = count_inprogress + 1;
                            break;
                        case "Cancelled": count_cancelled = count_cancelled + 1;
                            break;
                    }
                }
                txtCancelled.setText("" + count_cancelled);
                txtCompleted.setText("" + count_completed);
                txtCreated.setText("" + count_created);
                txtInProgress.setText("" + count_inprogress);
                txtTotal.setText("" + (count_completed+count_created+count_inprogress+count_cancelled));

            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        TextView txtMonthly = findViewById(R.id.txt_report_orderwise_monthly);
        txtMonthly.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dailyFlag = false;
                weeklyFlag = false;
                monthlyFlag = true;
                String currentMonth = new SimpleDateFormat("MMM").format(Calendar.getInstance().getTime());
                count_cancelled=0;
                count_completed =0;
                count_created =0 ;
                count_inprogress =0;

                txtCurrentSelection.setText("" + currentMonth);


                for (Orders order : orders) {
                    if (order.date.split("-")[1].equals(currentMonth)) {
                        switch (order.status) {
                            case "Complete":
                                count_completed = count_completed + 1;
                                break;
                            case "Created":
                                count_created = count_created + 1;
                                break;
                            case "InProgress":
                                count_inprogress = count_inprogress + 1;
                                break;
                            case "Cancelled":
                                count_cancelled = count_cancelled + 1;
                                break;
                        }
                    }
                }


                txtCancelled.setText("" + count_cancelled);
                txtCompleted.setText("" + count_completed);
                txtCreated.setText("" + count_created);
                txtInProgress.setText("" + count_inprogress);
                txtTotal.setText("" + (count_completed+count_created+count_inprogress+count_cancelled));
            }
        });

        TextView txtWeekly = findViewById(R.id.txt_report_orderwise_weekly);
        txtWeekly.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dailyFlag = false;
                weeklyFlag = true;
                monthlyFlag = false;

                count_cancelled=0;
                count_completed =0;
                count_created =0 ;
                count_inprogress =0;


                txtCurrentSelection.setText(getCurrentWeek());

                try{
                for (Orders order : orders) {
                        if (isDateInCurrentWeek(new SimpleDateFormat("dd-MMM-yyyy").parse(order.date))) {
                            switch (order.status) {
                                case "Complete":
                                    count_completed = count_completed + 1;
                                    break;
                                case "Created":
                                    count_created = count_created + 1;
                                    break;
                                case "InProgress":
                                    count_inprogress = count_inprogress + 1;
                                    break;
                                case "Cancelled":
                                    count_cancelled = count_cancelled + 1;
                                    break;
                            }
                        }
                }}catch (Exception e){}


                txtCancelled.setText("" + count_cancelled);
                txtCompleted.setText("" + count_completed);
                txtCreated.setText("" + count_created);
                txtInProgress.setText("" + count_inprogress);
                txtTotal.setText("" + (count_completed+count_created+count_inprogress+count_cancelled));
            }
        });

        TextView txtDaily = findViewById(R.id.txt_report_orderwise_daily);
        txtDaily.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                dailyFlag = true;
                weeklyFlag = false;
                monthlyFlag = false;
                String currentDate = new SimpleDateFormat("dd-MMM-yyyy").format(Calendar.getInstance().getTime());
                count_cancelled=0;
                count_completed =0;
                count_created =0 ;
                count_inprogress =0;

                txtCurrentSelection.setText("" + currentDate);

                for (Orders order : orders) {
                    if (order.date.equals(currentDate)){
                        switch (order.status){
                            case "Complete": count_completed = count_completed + 1;
                                break;
                            case "Created": count_created = count_created + 1;
                                break;
                            case "InProgress": count_inprogress = count_inprogress + 1;
                                break;
                            case "Cancelled": count_cancelled = count_cancelled + 1;
                                break;
                        }
                    }
                }

                txtCancelled.setText("" + count_cancelled);
                txtCompleted.setText("" + count_completed);
                txtCreated.setText("" + count_created);
                txtInProgress.setText("" + count_inprogress);
                txtTotal.setText("" + (count_completed+count_created+count_inprogress+count_cancelled));

            }
        });

        LinearLayout llcreated = findViewById(R.id.layout_report_created);
        llcreated.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent orderstatusIntent = new Intent(reportorderwise.this, reportorderstatus.class);
                Bundle extras = new Bundle();
                extras.putString("orderStatus", "Created");
                orderstatusIntent.putExtras(extras);
                startActivity(orderstatusIntent);
            }
        });

        LinearLayout llcompleted = findViewById(R.id.layout_report_completed);
        llcompleted.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent orderstatusIntent = new Intent(reportorderwise.this, reportorderstatus.class);
                Bundle extras = new Bundle();
                extras.putString("orderStatus", "Complete");
                orderstatusIntent.putExtras(extras);
                startActivity(orderstatusIntent);
            }
        });

        LinearLayout llcancelled = findViewById(R.id.layout_report_cancelled);
        llcancelled.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent orderstatusIntent = new Intent(reportorderwise.this, reportorderstatus.class);
                Bundle extras = new Bundle();
                extras.putString("orderStatus", "Cancelled");
                orderstatusIntent.putExtras(extras);
                startActivity(orderstatusIntent);
            }
        });

        LinearLayout llinprogress = findViewById(R.id.layout_report_inprogress);
        llinprogress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent orderstatusIntent = new Intent(reportorderwise.this, reportorderstatus.class);
                Bundle extras = new Bundle();
                extras.putString("orderStatus", "InProgress");
                orderstatusIntent.putExtras(extras);
                startActivity(orderstatusIntent);
            }
        });

        LinearLayout lltotal = findViewById(R.id.layout_report_total);
        lltotal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent orderstatusIntent = new Intent(reportorderwise.this, reportorderstatus.class);
                Bundle extras = new Bundle();
                extras.putString("orderStatus", "All");
                orderstatusIntent.putExtras(extras);
                startActivity(orderstatusIntent);
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