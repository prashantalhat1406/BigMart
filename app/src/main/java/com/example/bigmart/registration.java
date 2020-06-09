package com.example.bigmart;

import android.content.Intent;
import android.os.Bundle;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class registration extends AppCompatActivity {
    private  Long userID;
    private  String password;
    EditText edtName, edtMobile,edtPassword,edtAddress1, edtAddress2;
    FirebaseDatabase database;

    public void showErrorMessage(String message){
        Toast error = Toast.makeText(registration.this, message,Toast.LENGTH_SHORT);
        error.setGravity(Gravity.TOP, 0, 0);
        error.show();
    }

    public Boolean isMobileValid(String mobileNumber){
        Boolean flag = false;

        if (mobileNumber.length() == 0)
            showErrorMessage("Mobile should not be empty");
        else
        {
            if (mobileNumber.length() < 10)
                showErrorMessage("Enter 10 Digit Mobile Number");
            else
            {
                if (Double.parseDouble(mobileNumber) < 7000000000.00)
                    showErrorMessage("Invalid Mobile Number");
                else
                    flag = true;
            }
        }
        return flag;
    }

    public Boolean isPinValid(String pin){
        Boolean flag = false;

        if (pin.length() == 0)
            showErrorMessage("Password should not be empty");
        else
        {
            if (pin.length() < 4)
                showErrorMessage("Incorrect Pin: Enter 4 Digit pin");
            else
                flag = true;
        }
        return flag;
    }

    public Boolean isNameValid(String name){
        Boolean flag = false;

        if (name.length() == 0)
            showErrorMessage("Name should not be empty");
        else
        {
            flag = true;
        }
        return flag;
    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_register);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Bundle b = getIntent().getExtras();
        userID = b.getLong("userID");

        database = FirebaseDatabase.getInstance("https://bigmart-sinprl.firebaseio.com/");

        edtMobile = findViewById(R.id.edt_register_mobile);
        if (userID != 0)
        {
            edtMobile.setEnabled(false);
            edtMobile.setText("" + userID);
        }


        edtName = findViewById(R.id.edt_register_Name);
        edtPassword = findViewById(R.id.edt_register_pin);
        edtAddress1 = findViewById(R.id.edt_register_Address1);
        edtAddress2 = findViewById(R.id.edt_register_Address2);




        Button butRegister = findViewById(R.id.but_register_save);
        butRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isMobileValid(edtMobile.getText().toString()) && isNameValid(edtName.getText().toString()) && isPinValid(edtPassword.getText().toString()))
                {
                    User user = new User();
                    user.setName(edtName.getText().toString());
                    user.setMobile(Long.parseLong(edtMobile.getText().toString()));
                    user.setPassword(Integer.parseInt((edtPassword.getText().toString())));
                    user.setAddress1(edtAddress1.getText().toString());
                    user.setAddress2(edtAddress2.getText().toString());
                    user.setEmail("");

                    DatabaseReference databaseReference = database.getReference("Users");
                    databaseReference.child("" + user.Mobile).setValue(user);
                    Intent homeIntent = new Intent(registration.this, home.class);
                    Bundle extras = new Bundle();
                    extras.putLong("userID", user.Mobile);
                    homeIntent.putExtras(extras);
                    startActivity(homeIntent);
                    finish();
                }
                /*Boolean flag = true;


                String errorMessages[] = {
                        "Enter 10 Digit Mobile Number",
                        "Incorrect Pin: Enter 4 Digit pin",
                        "Name should not be empty",
                        "Mobile should not be empty",
                        "Pin should not be empty"
                };
                if (edtMobile.length() == 0){
                    flag = false;
                    Toast error = Toast.makeText(registration.this, errorMessages[3],Toast.LENGTH_SHORT);
                    error.setGravity(Gravity.TOP, 0, 0);
                    error.show();
                }
                if (edtMobile.length() < 10){
                    flag = false;
                    Toast error = Toast.makeText(registration.this, errorMessages[0],Toast.LENGTH_SHORT);
                    error.setGravity(Gravity.TOP, 0, 0);
                    error.show();
                }
                if (edtPassword.length() == 0){
                    flag = false;
                    Toast error = Toast.makeText(registration.this, errorMessages[4],Toast.LENGTH_SHORT);
                    error.setGravity(Gravity.TOP, 0, 0);
                    error.show();
                }
                if (edtPassword.length() < 4){
                    flag = false;
                    Toast error = Toast.makeText(registration.this, errorMessages[1],Toast.LENGTH_SHORT);
                    error.setGravity(Gravity.TOP, 0, 0);
                    error.show();
                }
                if (edtName.length() == 0){
                    flag = false;
                    Toast error = Toast.makeText(registration.this, errorMessages[2],Toast.LENGTH_SHORT);
                    error.setGravity(Gravity.TOP, 0, 0);
                    error.show();
                }

                if (flag)
                {
                    User user = new User();
                    user.setName(edtName.getText().toString());
                    user.setMobile(Long.parseLong(edtMobile.getText().toString()));
                    user.setPassword(Integer.parseInt((edtPassword.getText().toString())));
                    user.setAddress1("");
                    user.setAddress2("");
                    user.setEmail("");

                    DatabaseReference databaseReference = database.getReference("Users");
                    databaseReference.child("" + user.Mobile).setValue(user);
                    Intent homeIntent = new Intent(registration.this, home.class);
                    Bundle extras = new Bundle();
                    extras.putLong("userID", user.Mobile);
                    homeIntent.putExtras(extras);
                    startActivity(homeIntent);
                    finish();
                }else {

                }*/
            }
        });


    }

}
