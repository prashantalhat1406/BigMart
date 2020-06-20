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

import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class customerprofile extends AppCompatActivity {

    private long userID;
    User user;
    EditText name, mobile, address1, address2, email;
    FirebaseDatabase database;
    Integer password = 0;
    Button save;
    List<Product> products;

    public void goToHome(){
        Intent homeIntent = new Intent(customerprofile.this, home.class);
        Bundle extras = new Bundle();
        extras.putLong("userID", userID);
        homeIntent.putExtras(extras);
        startActivity(homeIntent);
        finish();
    }

    /*public void showErrorMessage(String message){
        Toast error = Toast.makeText(customerprofile.this, message,Toast.LENGTH_SHORT);
        error.setGravity(Gravity.TOP, 0, 0);
        error.show();
    }*/


    public Boolean isAddress2Empty(){
        Boolean flag = false;

        if (address2.getText().toString().trim().length() == 0)
        {
            address2.setError("Address2 should not be empty");
            address2.requestFocus();
        }
        else
            flag = true;

        return flag;
    }

    public Boolean isNameEmpty(){
        Boolean flag = false;

        if (name.getText().toString().trim().length() == 0)
        {
            name.setError("Name should not be empty");
            name.requestFocus();
        }
        else
            flag = true;

        return flag;
    }

    public Boolean isAddress1Empty(){
        Boolean flag = false;

        if (address1.getText().toString().trim().length() == 0)
        {
            address1.setError("Address1 should not be empty");
            address1.requestFocus();
        }
        else
            flag = true;

        return flag;
    }


    private TextWatcher genericTextWater = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
        }

        @Override
        public void afterTextChanged(Editable s) {
            save.setEnabled(true);
            save.setBackground(getDrawable(R.drawable.status_complete));
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customerprofile);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitleTextColor(getColor(R.color.colorPrimaryDark));
        toolbar.setOverflowIcon(getDrawable(R.drawable.ic_menuicon));

        Bundle b = getIntent().getExtras();
        userID = b.getLong("userID");
        user = new User();

        name = findViewById(R.id.edt_profile_name);
        name.addTextChangedListener(genericTextWater);
        mobile = findViewById(R.id.edt_profile_mobile);
        mobile.addTextChangedListener(genericTextWater);
        address1 = findViewById(R.id.edt_profile_address1);
        address1.addTextChangedListener(genericTextWater);
        address2 = findViewById(R.id.edt_profile_address2);
        address2.addTextChangedListener(genericTextWater);
        email = findViewById(R.id.edt_profile_email);

        database = FirebaseDatabase.getInstance("https://bigmart-sinprl.firebaseio.com/");
        DatabaseReference databaseReference = database.getReference("/Users/"+userID);
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                user = dataSnapshot.getValue(User.class);
                name.setText("" + user.Name);
                mobile.setText("" + user.Mobile);
                address1.setText("" + user.Address1);
                address2.setText("" + user.Address2);
                email.setText("" + user.Email);
                password = user.Password;
                save.setEnabled(false);
                save.setBackground(getDrawable(R.drawable.status_complete_disable));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {            }
        });

        products = new ArrayList<Product>();

        /*DatabaseReference productReference = database.getReference("Users/" + userID).child("/TempOrder");
        Query query = productReference.orderByKey();
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                products.clear();
                    for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                        products.add(postSnapshot.getValue(Product.class));
                    }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });*/


        save = findViewById(R.id.but_profile_save);
        save.setEnabled(false);
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isAddress1Empty()  && isAddress2Empty()  && isNameEmpty())
                {
                    DatabaseReference databaseReference = database.getReference("Users/" + userID);
                    User user = new User();
                    user.setName("" + name.getText().toString());
                    user.setMobile(Long.parseLong(mobile.getText().toString()));
                    user.setAddress1("" + address1.getText().toString());
                    user.setAddress2("" + address2.getText().toString());
                    user.setEmail("" + email.getText().toString());
                    user.setPassword(password);
                    databaseReference.child("name").setValue(user.Name);
                    databaseReference.child("address1").setValue(user.Address1);
                    databaseReference.child("address2").setValue(user.Address2);
                    databaseReference.child("email").setValue(user.Email);
                    databaseReference.child("password").setValue(user.Password);
                    databaseReference.child("mobile").setValue(user.Mobile);

                    Toast error = Toast.makeText(customerprofile.this, "Changes Saved", Toast.LENGTH_SHORT);
                    error.setGravity(Gravity.CENTER, 0, 0);
                    error.show();

                    /*database = FirebaseDatabase.getInstance("https://bigmart-sinprl.firebaseio.com/");

                    DatabaseReference userTempOrderReference = database.getReference("Users/"+userID+"/TempOrder");

                    for (Product product : products) {
                        userTempOrderReference.child(""+product.ID).setValue(product);
                    }*/


                    goToHome();
                    //finish();
                }

            }
        });
    }

}
