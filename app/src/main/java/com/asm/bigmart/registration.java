package com.asm.bigmart;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
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

import android.text.Editable;
import android.text.TextWatcher;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class registration extends AppCompatActivity {
    private Integer showpassword = 0;
    private  Long userID;
    public List<User> usersDB;
    private  String password;
    EditText edtName, edtMobile,edtPassword,edtAddress1, edtAddress2, edtSecurityAns;
    FirebaseDatabase database;
    Spinner spnSecurityQ;
    ArrayAdapter<String> SQadapter;

    public void showErrorMessage(String message){
        Toast error = Toast.makeText(registration.this, message,Toast.LENGTH_SHORT);
        error.setGravity(Gravity.TOP, 0, 0);
        error.show();
    }

    public Boolean isMobileValid(String mobileNumber){
        Boolean flag = false;

        if (mobileNumber.length() == 0)
            edtMobile.setError("Mobile should not be empty");
        else
        {
            if (mobileNumber.length() < 10)
                edtMobile.setError("Enter 10 Digit Mobile Number");
            else
            {
                if (Double.parseDouble(mobileNumber) < 7000000000.00)
                    edtMobile.setError("Invalid Mobile Number");
                else
                {
                    //flag = true;
                    Boolean exitingUser = false;
                    for (User user : usersDB) {
                        if (user.Mobile.equals(Long.parseLong(mobileNumber)))
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
            edtPassword.setError("Password should not be empty");
        else
        {
            if (pin.length() < 4)
                edtPassword.setError("Incorrect Password: Enter 4 Digit pin");
            else
                flag = true;
        }
        return flag;
    }

    public Boolean isFieldEmpty(EditText field){
        Boolean flag = false;

        if (field.getText().toString().trim().length() == 0)
        {
            field.setError(field.getTag().toString() + " should not be empty");
            field.requestFocus();
        }
        else
            flag = true;

        return flag;
    }

    public boolean isSecurityQuestionSelected(){
        boolean flag = false;

        if (spnSecurityQ.getSelectedItemPosition() > 0)
            flag = true;
        else
            showErrorMessage("Please select security Question");

        return  flag;
    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitleTextColor(getColor(R.color.colorPrimaryDark));

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_back);

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
        edtName.requestFocus();
        edtPassword = findViewById(R.id.edt_register_pin);
        edtPassword.setLongClickable(false);
        edtPassword.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                edtPassword.requestFocus();
                //getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
                if(event.getAction() == MotionEvent.ACTION_UP) {
                    if(event.getRawX() >= (500+edtPassword.getRight() - edtPassword.getCompoundDrawables()[2].getBounds().width())) {
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

        edtPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {            }

            @Override
            public void afterTextChanged(Editable s) {
                if (edtPassword.getText().length() == 4)
                    edtAddress1.requestFocus();
            }
        });

        edtMobile.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {            }

            @Override
            public void afterTextChanged(Editable s) {
                if (edtMobile.getText().length() == 10)
                    edtPassword.requestFocus();
            }
        });

        edtAddress1 = findViewById(R.id.edt_register_Address1);
        edtAddress2 = findViewById(R.id.edt_register_Address2);

        edtSecurityAns = findViewById(R.id.edt_register_secAnswer);
        spnSecurityQ = findViewById(R.id.spn_register_security);

        String[] securityQArray = getResources().getStringArray(R.array.securityquestions);
        SQadapter =  new ArrayAdapter<String>(this, R.layout.support_simple_spinner_dropdown_item, securityQArray);
        spnSecurityQ.setAdapter(SQadapter);

        Button butRegister = findViewById(R.id.but_register_save);
        butRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isMobileValid(edtMobile.getText().toString()) && isFieldEmpty(edtName)
                        && isPinValid(edtPassword.getText().toString())
                        && isFieldEmpty(edtAddress1)
                        && isFieldEmpty(edtAddress2)
                        && isFieldEmpty(edtSecurityAns)
                        && isSecurityQuestionSelected()
                )
                {
                    try {
                        User user = new User();
                        user.setName(CryptUtil.encrypt( edtName.getText().toString()));
                        user.setMobile(Long.parseLong(edtMobile.getText().toString()));
                        user.setPassword(Integer.parseInt((edtPassword.getText().toString())));
                        user.setAddress1(CryptUtil.encrypt( edtAddress1.getText().toString()));
                        user.setAddress2(CryptUtil.encrypt(edtAddress2.getText().toString()));
                        user.setEmail("");
                        user.setSecurityQ( spnSecurityQ.getSelectedItemPosition());
                        user.setAnswer(CryptUtil.encrypt(edtSecurityAns.getText().toString()));
                        user.setAccess("Normal");

                        DatabaseReference databaseReference = database.getReference("Users");
                        databaseReference.child("" + user.Mobile).setValue(user);
                        Intent homeIntent = new Intent(registration.this, home.class);
                        Bundle extras = new Bundle();
                        extras.putLong("userID", user.Mobile);
                        homeIntent.putExtras(extras);
                        startActivity(homeIntent);
                        finish();
                    }catch (Exception e){}


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
        final AlertDialog alertLogout = logoutAlertBuilder.create();
        alertLogout.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialog) {
                alertLogout.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(Color.RED);
                alertLogout.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(getResources().getColor(R.color.darkgreenColorButton));
            }
        });
        alertLogout.setTitle("LOGOUT");
        alertLogout.show();
    }

    @Override
    public void onBackPressed() {
        //showLogoutAlertDialog();
        Intent logoutIntent = new Intent(registration.this, login.class);
        logoutIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK |Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(logoutIntent);
        finish();
        super.onBackPressed();
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
