package com.example.bigmart;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class registration extends AppCompatActivity {
    private Integer showpassword = 0;
    private  Long userID;
    public List<User> usersDB;
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
                {
                    //flag = true;
                    Boolean exitingUser = false;
                    for (User user : usersDB) {
                        if (user.Mobile == Long.parseLong(mobileNumber))
                            {exitingUser = true; break;}
                    }

                    if (exitingUser)
                    {
                        showErrorMessage("User already exists. Please enter new mobile.");
                        edtPassword.setText("");
                        edtMobile.setText("");
                        edtMobile.requestFocus();
                    }
                    else
                        flag = true;
                }
            }
        }
        return flag;
    }

    public Boolean isPinValid(String pin){
        Boolean flag = false;

        if (pin.length() == 0)
            showErrorMessage("Pin should not be empty");
        else
        {
            if (pin.length() < 4)
                showErrorMessage("Incorrect Pin: Enter 4 Digit pin");
            else
                flag = true;
        }
        return flag;
    }

    public Boolean isFieldEmpty(EditText field){
        Boolean flag = false;

        if (field.getText().length() == 0)
        {
            showErrorMessage(field.getTag().toString() + " should not be empty");
            field.requestFocus();
        }
        else
            flag = true;

        return flag;
    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Bundle b = getIntent().getExtras();
        userID = b.getLong("userID");
        usersDB = new ArrayList<User>();

        database = FirebaseDatabase.getInstance("https://bigmart-sinprl.firebaseio.com/");

        edtMobile = findViewById(R.id.edt_register_mobile);
        if (userID != 0)
        {
            edtMobile.setEnabled(false);
            edtMobile.setText("" + userID);
        }

        database = FirebaseDatabase.getInstance("https://bigmart-sinprl.firebaseio.com/");

        Query query = database.getReference("/Users");
        query.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                User user = dataSnapshot.getValue(User.class);
                user.setID(dataSnapshot.getKey());
                usersDB.add(user);
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {           }
            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {            }
            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {           }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {            }
        });


        edtName = findViewById(R.id.edt_register_Name);
        edtPassword = findViewById(R.id.edt_register_pin);
        edtPassword.setLongClickable(false);
        edtPassword.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                if(event.getAction() == MotionEvent.ACTION_UP) {
                    if(event.getRawX() >= (edtPassword.getRight() - edtPassword.getCompoundDrawables()[2].getBounds().width())) {
                        if (showpassword == 0)
                        {
                            edtPassword.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                            showpassword = 1;
                            edtPassword.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_eye, 0);
                        }else
                        {
                            edtPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
                            showpassword = 0;
                            edtPassword.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_eye_off, 0);

                        }

                        return true;
                    }
                }
                return false;
            }
        });

        edtAddress1 = findViewById(R.id.edt_register_Address1);
        edtAddress2 = findViewById(R.id.edt_register_Address2);

        Button butRegister = findViewById(R.id.but_register_save);
        butRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isMobileValid(edtMobile.getText().toString()) && isFieldEmpty(edtName)
                        && isPinValid(edtPassword.getText().toString())
                        && isFieldEmpty(edtAddress1)
                        && isFieldEmpty(edtAddress2)
                )
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
            }
        });


    }

    public void showLogoutAlertDialog(){

        AlertDialog.Builder logoutAlertBuilder = new AlertDialog.Builder(registration.this);
        logoutAlertBuilder.setMessage("Are you sure you want to exit ?");
        logoutAlertBuilder.setCancelable(false);
        logoutAlertBuilder.setPositiveButton(
                "YES", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                });
        logoutAlertBuilder.setNegativeButton(
                "NO", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
        AlertDialog alertLogout = logoutAlertBuilder.create();
        alertLogout.show();
    }

    @Override
    public void onBackPressed() {
        showLogoutAlertDialog();
        //super.onBackPressed();
    }
}
