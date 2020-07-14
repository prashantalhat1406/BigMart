package com.asm.bigmart;

import android.content.Intent;
import android.os.Bundle;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class customerprofile extends AppCompatActivity {

    private long userID;
    User user;
    EditText name, mobile, address1, address2, email;
    FirebaseDatabase database;
    Integer password = 0;
    Button save;


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
            save.setBackground(getDrawable(R.drawable.roundbutton_green));
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

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_back);

        /*ViewGroup.LayoutParams params = (ViewGroup.LayoutParams)toolbar.getLayoutParams();
        params.height = 160;
        toolbar.setLayoutParams(params);*/


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
                try {
                    user = dataSnapshot.getValue(User.class);
                    name.setText("" + CryptUtil.decrypt(user.Name));
                    mobile.setText("" + user.Mobile);
                    address1.setText("" + CryptUtil.decrypt(user.Address1));
                    address2.setText("" + CryptUtil.decrypt(user.Address2));
                    email.setText("" + CryptUtil.decrypt(user.Email));
                    password = user.Password;
                    save.setEnabled(false);
                    save.setBackground(getDrawable(R.drawable.status_complete_disable));
                }catch (Exception e) {}
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {            }
        });




        save = findViewById(R.id.but_profile_save);
        save.setEnabled(false);
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isAddress1Empty()  && isAddress2Empty()  && isNameEmpty())
                {
                    try {
                        DatabaseReference databaseReference = database.getReference("Users/" + userID);
                        User user = new User();
                        user.setName("" + CryptUtil.encrypt(name.getText().toString()));
                        user.setMobile(Long.parseLong(mobile.getText().toString()));
                        user.setAddress1("" + CryptUtil.encrypt(address1.getText().toString()));
                        user.setAddress2("" + CryptUtil.encrypt(address2.getText().toString()));
                        user.setEmail("" + CryptUtil.encrypt(email.getText().toString()));
                        user.setPassword(password);
                        databaseReference.child("name").setValue(user.Name);
                        databaseReference.child("address1").setValue(user.Address1);
                        databaseReference.child("address2").setValue(user.Address2);
                        databaseReference.child("email").setValue(user.Email);
                        databaseReference.child("password").setValue(user.Password);
                        databaseReference.child("mobile").setValue(user.Mobile);
                    }catch (Exception e) {}

                    Toast error = Toast.makeText(customerprofile.this, "Changes Saved", Toast.LENGTH_SHORT);
                    error.setGravity(Gravity.CENTER, 0, 0);
                    error.show();

                    goToHome();
                    //finish();
                }

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
