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
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class customerprofile extends AppCompatActivity {

    private long userID;
    User user;
    EditText name, mobile, address1, address2, email;
    FirebaseDatabase database;
    Integer password = 0;

    public void goToHome(){
        Intent homeIntent = new Intent(customerprofile.this, home.class);
        Bundle extras = new Bundle();
        extras.putLong("userID", userID);
        homeIntent.putExtras(extras);
        startActivity(homeIntent);
        finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customerprofile);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Bundle b = getIntent().getExtras();
        userID = b.getLong("userID");
        user = new User();

        name = findViewById(R.id.edt_profile_name);
        mobile = findViewById(R.id.edt_profile_mobile);
        address1 = findViewById(R.id.edt_profile_address1);
        address2 = findViewById(R.id.edt_profile_address2);
        email = findViewById(R.id.edt_profile_email);

        database = FirebaseDatabase.getInstance("https://bigmart-sinprl.firebaseio.com/");
        DatabaseReference databaseReference = database.getReference("/Users/"+userID);
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                //User user = new User();
                user = dataSnapshot.getValue(User.class);
                /*for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    user = postSnapshot.getValue(User.class);
                }*/
                name.setText("" + user.Name);
                mobile.setText("" + user.Mobile);
                address1.setText("" + user.Address1);
                address2.setText("" + user.Address2);
                email.setText("" + user.Email);
                password = user.Password;
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {            }
        });

        Button save = findViewById(R.id.but_profile_save);
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatabaseReference databaseReference = database.getReference("Users/"+userID);
                User user = new User();
                user.setName(""+name.getText().toString());
                user.setMobile(Long.parseLong(mobile.getText().toString()));
                user.setAddress1(""+address1.getText().toString());
                user.setAddress2(""+address2.getText().toString());
                user.setEmail(""+email.getText().toString());
                user.setPassword(password);
                databaseReference.setValue(user);
                Toast error = Toast.makeText(customerprofile.this, "Changes Saved",Toast.LENGTH_SHORT);
                error.setGravity(Gravity.CENTER, 0, 0);
                error.show();
                goToHome();
                finish();

            }
        });
    }

}
