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

import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

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
    Button back, next;
    String baseDate="";

    public void showErrorMessage(String message){
        Toast error = Toast.makeText(reportorderwise.this, message,Toast.LENGTH_SHORT);
        error.setGravity(Gravity.TOP, 0, 0);
        error.show();
    }

    public void showReportOrderList(String status){
        Intent orderstatusIntent = new Intent(reportorderwise.this, reportorderstatus.class);
        Bundle extras = new Bundle();
        extras.putString("orderStatus", status);
        extras.putString("baseDate", baseDate);

        if (dailyFlag)
            extras.putInt("period", 0);

        if (weeklyFlag)
            extras.putInt("period", 1);

        if (monthlyFlag)
            extras.putInt("period", 2);

        orderstatusIntent.putExtras(extras);
        startActivity(orderstatusIntent);
    }

    public static boolean isDateInCurrentWeek(Date date,String baseDate) {
        Calendar currentCalendar = Calendar.getInstance();
        try {
            currentCalendar.setTime(new SimpleDateFormat("dd-MMM-yyyy").parse(baseDate));
        }catch (Exception e){}

        int week = currentCalendar.get(Calendar.WEEK_OF_YEAR);
        int year = currentCalendar.get(Calendar.YEAR);
        Calendar targetCalendar = Calendar.getInstance();
        targetCalendar.setTime(date);
        int targetWeek = targetCalendar.get(Calendar.WEEK_OF_YEAR);
        int targetYear = targetCalendar.get(Calendar.YEAR);
        return week == targetWeek && year == targetYear;
    }

    public boolean isDateInCurrentWeekN(Date date,String baseDate) {

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

    public String getCurrentWeek(String baseDate){
        String currWeek = "";
        Calendar currentCalendar = Calendar.getInstance();

        try {
            currentCalendar.setTime(new SimpleDateFormat("dd-MMM-yyyy").parse(baseDate));
        }catch (Exception e){}

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

        baseDate = new SimpleDateFormat("dd-MMM-yyyy").format(Calendar.getInstance().getTime());
        dailyFlag = true;
        weeklyFlag = false;
        monthlyFlag = false;
        txtCurrentSelection.setText(baseDate);


        final TextView txtDaily = findViewById(R.id.txt_report_orderwise_daily);
        final TextView txtMonthly = findViewById(R.id.txt_report_orderwise_monthly);
        final TextView txtWeekly = findViewById(R.id.txt_report_orderwise_weekly);

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
                    /*switch (order.status){
                        case "Complete": count_completed = count_completed + 1;
                            break;
                        case "Created": count_created = count_created + 1;
                            break;
                        case "InProgress": count_inprogress = count_inprogress + 1;
                            break;
                        case "Cancelled": count_cancelled = count_cancelled + 1;
                            break;
                    }*/
                }
                txtDaily.callOnClick();
                /*txtCancelled.setText("" + count_cancelled);
                txtCompleted.setText("" + count_completed);
                txtCreated.setText("" + count_created);
                txtInProgress.setText("" + count_inprogress);
                txtTotal.setText("" + (count_completed+count_created+count_inprogress+count_cancelled));*/

            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });




        txtMonthly.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                txtMonthly.setBackground(getDrawable(R.drawable.button_bluefull));
                txtMonthly.setTextColor(getColor(R.color.colorWhite));
                txtWeekly.setBackground(getDrawable(R.drawable.button_blueborder));
                txtWeekly.setTextColor(getColor(R.color.blacklabels));
                txtDaily.setBackground(getDrawable(R.drawable.button_blueborder));
                txtDaily.setTextColor(getColor(R.color.blacklabels));

                dailyFlag = false;
                weeklyFlag = false;
                monthlyFlag = true;
                String currentMonth = baseDate.split("-")[1];
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


        txtWeekly.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                txtWeekly.setBackground(getDrawable(R.drawable.button_bluefull));
                txtWeekly.setTextColor(getColor(R.color.colorWhite));
                txtMonthly.setBackground(getDrawable(R.drawable.button_blueborder));
                txtMonthly.setTextColor(getColor(R.color.blacklabels));
                txtDaily.setBackground(getDrawable(R.drawable.button_blueborder));
                txtDaily.setTextColor(getColor(R.color.blacklabels));

                dailyFlag = false;
                weeklyFlag = true;
                monthlyFlag = false;

                count_cancelled=0;
                count_completed =0;
                count_created =0 ;
                count_inprogress =0;


                txtCurrentSelection.setText(getCurrentWeek(baseDate));

                try{
                for (Orders order : orders) {
                        if (isDateInCurrentWeekN(new SimpleDateFormat("dd-MMM-yyyy").parse(order.date),baseDate)) {
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


        txtDaily.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {

                txtDaily.setBackground(getDrawable(R.drawable.button_bluefull));
                txtDaily.setTextColor(getColor(R.color.colorWhite));
                txtWeekly.setBackground(getDrawable(R.drawable.button_blueborder));
                txtWeekly.setTextColor(getColor(R.color.blacklabels));
                txtMonthly.setBackground(getDrawable(R.drawable.button_blueborder));
                txtMonthly.setTextColor(getColor(R.color.blacklabels));

                dailyFlag = true;
                weeklyFlag = false;
                monthlyFlag = false;
                //String currentDate = new SimpleDateFormat("dd-MMM-yyyy").format(Calendar.getInstance().getTime());
                count_cancelled=0;
                count_completed =0;
                count_created =0 ;
                count_inprogress =0;

                txtCurrentSelection.setText("" + baseDate);
                ///baseDate = currentDate;

                for (Orders order : orders) {
                    if (order.date.equals(baseDate)){
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

        back = findViewById(R.id.but_reportorderwise_back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar currentCalendar = Calendar.getInstance();

                try {
                    currentCalendar.setTime(new SimpleDateFormat("dd-MMM-yyyy").parse(baseDate));
                }catch (Exception e){}

                if (dailyFlag)
                {
                    currentCalendar.add(Calendar.DAY_OF_MONTH,-1);
                    baseDate = new SimpleDateFormat("dd-MMM-yyyy").format(currentCalendar.getTime());
                    txtDaily.callOnClick();

                }
                if (weeklyFlag)
                {
                    currentCalendar.add(Calendar.DAY_OF_MONTH,-7);
                    baseDate = new SimpleDateFormat("dd-MMM-yyyy").format(currentCalendar.getTime());
                    txtWeekly.callOnClick();
                }
                if (monthlyFlag)
                {
                    currentCalendar.add(Calendar.MONTH,-1);
                    baseDate = new SimpleDateFormat("dd-MMM-yyyy").format(currentCalendar.getTime());
                    txtMonthly.callOnClick();
                }
            }
        });

        next = findViewById(R.id.but_reportorderwise_next);
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar currentCalendar = Calendar.getInstance();

                try {
                    currentCalendar.setTime(new SimpleDateFormat("dd-MMM-yyyy").parse(baseDate));
                }catch (Exception e){}

                if (dailyFlag)
                {
                    currentCalendar.add(Calendar.DAY_OF_MONTH,1);
                    baseDate = new SimpleDateFormat("dd-MMM-yyyy").format(currentCalendar.getTime());
                    txtDaily.callOnClick();

                }
                if (weeklyFlag)
                {
                    currentCalendar.add(Calendar.DAY_OF_MONTH,7);
                    baseDate = new SimpleDateFormat("dd-MMM-yyyy").format(currentCalendar.getTime());
                    txtWeekly.callOnClick();
                }
                if (monthlyFlag)
                {
                    currentCalendar.add(Calendar.MONTH,1);
                    baseDate = new SimpleDateFormat("dd-MMM-yyyy").format(currentCalendar.getTime());
                    txtMonthly.callOnClick();
                }
            }
        });

        LinearLayout llcreated = findViewById(R.id.layout_report_created);
        llcreated.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Integer.parseInt( txtCreated.getText().toString()) > 0)
                    showReportOrderList("Created");
                else
                    showErrorMessage("No Orders to show !");
            }
        });

        LinearLayout llcompleted = findViewById(R.id.layout_report_completed);
        llcompleted.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (Integer.parseInt( txtCompleted.getText().toString()) > 0)
                    showReportOrderList("Complete");
                else
                    showErrorMessage("No Orders to show !");
            }
        });

        LinearLayout llcancelled = findViewById(R.id.layout_report_cancelled);
        llcancelled.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (Integer.parseInt( txtCancelled.getText().toString()) > 0)
                    showReportOrderList("Cancelled");
                else
                    showErrorMessage("No Orders to show !");
            }
        });

        LinearLayout llinprogress = findViewById(R.id.layout_report_inprogress);
        llinprogress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (Integer.parseInt( txtInProgress.getText().toString()) > 0)
                    showReportOrderList("InProgress");
                else
                    showErrorMessage("No Orders to show !");
            }
        });

        LinearLayout lltotal = findViewById(R.id.layout_report_total);
        lltotal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (Integer.parseInt( txtTotal.getText().toString()) > 0)
                    showReportOrderList("All");
                else
                    showErrorMessage("No Orders to show !");
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