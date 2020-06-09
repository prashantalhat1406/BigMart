package com.example.bigmart;

import android.content.Intent;
import android.os.Bundle;

import com.google.firebase.FirebaseApp;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class login extends AppCompatActivity {

    private long userID;


    public List<User> usersDB;
    EditText edtMobile,edtPassword;
    Button butLogin ;
    TextView error;

    public void showErrorMessage(String message){
        Toast error = Toast.makeText(login.this, message,Toast.LENGTH_SHORT);
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

    public Boolean isPasswordValid(String password){
        Boolean flag = false;

        if (password.length() == 0)
            showErrorMessage("Password should not be empty");
        else
        {
            if (password.length() < 4)
                showErrorMessage("Incorrect Pin: Enter 4 Digit pin");
            else
                flag = true;
        }
        return flag;
    }

    public Boolean isUserExist(String mobileNumber)
    {
        Boolean flag = false;

        for (User u : usersDB) {
            if (u.Mobile == Long.parseLong(edtMobile.getText().toString()))
                flag = true;
        }
        return flag;
    }

    public Integer getPassword(String mobileNumber)
    {
        Integer pin = 0;

        for (User u : usersDB) {
            if (u.Mobile == Long.parseLong(edtMobile.getText().toString()))
                pin = u.Password;
        }
        return pin;
    }


    public void performLogin() {
        if(isMobileValid(edtMobile.getText().toString()) && isPasswordValid(edtPassword.getText().toString()))
        {
            if (isUserExist(edtMobile.getText().toString()))
            {
                Integer pin = getPassword(edtMobile.getText().toString());
                userID = Long.parseLong(edtMobile.getText().toString());
                if (pin == Integer.parseInt(edtPassword.getText().toString()))
                {
                    if (userID == 9999999999.0) {
                        Intent shopIntent = new Intent(login.this, shopownerhome.class);
                        startActivity(shopIntent);

                    } else {
                        Intent homeIntent = new Intent(login.this, home.class);
                        Bundle extras = new Bundle();
                        extras.putLong("userID", userID);
                        homeIntent.putExtras(extras);
                        startActivity(homeIntent);
                        finish();
                    }
                }else {
                    {
                        showErrorMessage("InCorrect Pin.");
                        edtPassword.setText("");
                    }
                }
            }else {
                showErrorMessage("User does not exist. Please Register first.");
                edtPassword.setText("");
            }
        }
    }
    /*public void performLogin() {

        if(usersDB.size() == 0){
            Toast t = Toast.makeText(login.this, "No Internet Connectivity", Toast.LENGTH_SHORT);
            t.setGravity(Gravity.TOP, 0, 0);
            t.show();

        }else {
            Boolean flag = true;
            Boolean validUserPassword = false;
            Boolean register = true;


            if(edtMobile.getText().length() == 0)
                {
                    showErrorMessage("Mobile should not be empty"); flag = false;
                }else
                    if (edtMobile.getText().length() < 10)
                    {
                        showErrorMessage("Enter 10 Digit Mobile Number"); flag = false;
                    }else
                        if (edtPassword.length() < 4 && edtPassword.length() > 0)
                        {
                            showErrorMessage("Incorrect Pin: Enter 4 Digit pin"); flag = false;
                        }else
                            if (edtPassword.getText().length() == 0)
                            {
                                Intent registerIntent = new Intent(login.this, registration.class);
                                Bundle extras = new Bundle();
                                extras.putLong("userID", Long.parseLong(edtMobile.getText().toString()));
                                extras.putString("password", edtPassword.getText().toString());
                                registerIntent.putExtras(extras);
                                startActivity(registerIntent);
                                finish();
                            }else
                            {
                                Boolean userNotFound = true;
                                for (User u : usersDB) {
                                    if (u.Mobile == Long.parseLong(edtMobile.getText().toString()))
                                        if (u.Password == Integer.parseInt((edtPassword.getText().toString()))) {
                                            validUserPassword = true;
                                            userNotFound = false;
                                            userID = u.Mobile;
                                            break;
                                        } else {
                                            showErrorMessage("InCorrect Password");
                                            edtPassword.setText("");
                                        }
                                }
                                if (validUserPassword) {
                                    if (userID == 1212121212) {
                                        Intent shopIntent = new Intent(login.this, shopownerhome.class);
                                        startActivity(shopIntent);

                                    } else {
                                        Intent homeIntent = new Intent(login.this, home.class);
                                        Bundle extras = new Bundle();
                                        extras.putLong("userID", userID);
                                        homeIntent.putExtras(extras);
                                        startActivity(homeIntent);
                                        finish();
                                    }
                                }
                                if (userNotFound)
                                {
                                    Intent registerIntent = new Intent(login.this, registration.class);
                                    Bundle extras = new Bundle();
                                    extras.putLong("userID", Long.parseLong(edtMobile.getText().toString()));
                                    extras.putString("password", edtPassword.getText().toString());
                                    registerIntent.putExtras(extras);
                                    startActivity(registerIntent);
                                    finish();
                                }
                            }
        }
    }*/

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        usersDB = new ArrayList<User>();

        FirebaseApp.initializeApp(login.this);
        FirebaseDatabase database = FirebaseDatabase.getInstance("https://bigmart-sinprl.firebaseio.com/");

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


        edtMobile = findViewById(R.id.edt_login_mobile);
        edtPassword = findViewById(R.id.edt_login_password);
        //error = findViewById(R.id.txt_login_error);
        edtMobile.setText("");
        edtPassword.setText("");

        edtPassword.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_GO) {
                    performLogin();
                }
                return false;
            }
        });


        butLogin = findViewById(R.id.but_login_login);
        butLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(usersDB.size() == 0) {
                    showErrorMessage("No Internet Connectivity.");
                }else
                    performLogin();
            }
        });

        Button butRegister = findViewById(R.id.but_login_register);
        butRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent registerIntent = new Intent(login.this, registration.class);
                Bundle extras = new Bundle();
                if (edtMobile.getText().toString().length() == 0)
                    extras.putLong("userID", 0);
                else
                    extras.putLong("userID", Long.parseLong(edtMobile.getText().toString()));

                //extras.putString("password", edtPassword.getText().toString());
                registerIntent.putExtras(extras);
                startActivity(registerIntent);
                finish();
            }
        });






    }

    @Override
    protected void onResume() {
        edtPassword.setText("");
        edtMobile.setText("");
        super.onResume();
    }


}
