package com.asm.bigmart;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

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

public class forgotpassword extends AppCompatActivity {

    Spinner spnSecurityQ;
    EditText edtSecurityAnswer, edtPassword, edtmobile;
    ArrayAdapter<String> SQadapter;
    public List<User> usersDB;
    Button butValidate;
    FirebaseDatabase database;
    private long userID;
    User user;
    private Integer showpassword = 0;

    public void showErrorMessage(String message){
        Toast error = Toast.makeText(forgotpassword.this, message,Toast.LENGTH_SHORT);
        error.setGravity(Gravity.TOP, 0, 0);
        error.show();
    }


    public boolean isSecurityAnswerCorrect(String secAnswer){
        boolean flag = false;

        try {
            if (CryptUtil.decrypt(user.Answer).toUpperCase().equals(secAnswer.toUpperCase()))
                flag = true;

        }catch (Exception e) {}

        return flag;
    }
    public  boolean isValidPassword(String password){
        boolean flag = false;

        if (password.length() == 0)
        {
            edtPassword.setError("Password should not be empty");
            edtPassword.requestFocus();
        }
        else
        {
            if (password.length() < 4)
            {
                edtPassword.setError("Incorrect Password: Enter 4 Digit pin");
                edtPassword.requestFocus();
            }
            else
                flag = true;
        }

        return flag;
    }

    public void showLogoutAlertDialog(){

        /*AlertDialog.Builder logoutAlertBuilder = new AlertDialog.Builder(forgotpassword.this);
        logoutAlertBuilder.setMessage("Are you sure to Logout ?");
        logoutAlertBuilder.setCancelable(false);
        logoutAlertBuilder.setPositiveButton(
                "YES", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent logoutIntent = new Intent(forgotpassword.this, login.class);
                        logoutIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK |Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(logoutIntent);
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

        alertLogout.show();*/

        final Dialog dialog = new Dialog(forgotpassword.this);
        dialog.setContentView(R.layout.logoutdialog);
        dialog.setCancelable(false);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        Button yes = dialog.findViewById(R.id.dialog_btn_yes);
        yes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent logoutIntent = new Intent(forgotpassword.this, login.class);
                logoutIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK |Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(logoutIntent);
                finish();
            }
        });
        Button no = dialog.findViewById(R.id.dialog_btn_no);
        no.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgotpassword);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        toolbar.setTitleTextColor(getColor(R.color.colorPrimaryDark));
        setTitle("Reset Password");

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_back);

        Bundle b = getIntent().getExtras();
        userID = b.getLong("userID",0);
        usersDB = new ArrayList<>();

        database = FirebaseDatabase.getInstance("https://bigmart-sinprl.firebaseio.com/");

        spnSecurityQ = findViewById(R.id.spn_forgot_security);
        edtSecurityAnswer = findViewById(R.id.edt_forgot_secAnswer);
        edtPassword = findViewById(R.id.edt_forgot_pin);


        edtPassword.setVisibility(View.GONE);
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


        String[] securityQArray = getResources().getStringArray(R.array.securityquestions);
        SQadapter =  new ArrayAdapter<String>(this, R.layout.support_simple_spinner_dropdown_item, securityQArray);
        spnSecurityQ.setAdapter(SQadapter);

        butValidate = findViewById(R.id.but_forgot_validate);
        butValidate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (butValidate.getText().toString().equals("Save Pin")) {
                    if (isValidPassword(edtPassword.getText().toString())  )
                    {

                            DatabaseReference databaseReference = database.getReference("Users/" + userID);
                            databaseReference.child("password").setValue(Integer.parseInt(edtPassword.getText().toString()));

                            showErrorMessage("Password/Pin updated successfully");

                            Intent logoutIntent = new Intent(forgotpassword.this, login.class);
                            logoutIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(logoutIntent);
                            finish();

                    }
                }else
                {
                    if (isSecurityAnswerCorrect(edtSecurityAnswer.getText().toString())) {
                        edtPassword.setVisibility(View.VISIBLE);
                        edtPassword.requestFocus();
                        butValidate.setText("Save Pin");
                        butValidate.setBackground(getDrawable(R.drawable.roundbutton_green));
                    } else {
                        edtSecurityAnswer.setError("InCorrect Answer, Please enter correct answer.");
                        edtSecurityAnswer.setText("");
                        edtSecurityAnswer.requestFocus();
                    }

                }
            }
        });



        Query userData = database.getReference("/Users/"+userID);
        userData.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                user = dataSnapshot.getValue(User.class);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    @Override
    public void onBackPressed() {
        //showLogoutAlertDialog();
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