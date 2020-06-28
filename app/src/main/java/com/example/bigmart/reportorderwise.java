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
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class reportorderwise extends AppCompatActivity {

    private List<Orders> orders;
    private FirebaseDatabase database;
    Integer count_created=0,count_cancelled=0,count_inprogress=0,count_completed=0;
    TextView txtCreated, txtCancelled, txtCompleted,txtInProgress, txtTotal;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reportorderwise);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        orders = new ArrayList<Orders>();
        txtCancelled = findViewById(R.id.txt_report_orderwise_cancelled);
        txtCompleted = findViewById(R.id.txt_report_orderwise_completed);
        txtInProgress = findViewById(R.id.txt_report_orderwise_inprogress);
        txtCreated = findViewById(R.id.txt_report_orderwise_created);
        txtTotal = findViewById(R.id.txt_report_orderwise_total);

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

    }
}