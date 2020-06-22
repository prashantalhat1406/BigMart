package com.example.bigmart;

import android.content.Intent;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.Gravity;
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

    public void showErrorMessage(String message){
        Toast error = Toast.makeText(forgotpassword.this, message,Toast.LENGTH_SHORT);
        error.setGravity(Gravity.TOP, 0, 0);
        error.show();
    }


    public boolean isSecurityAnswerCorrect(String secAnswer){
        boolean flag = false;

        if (user.Answer.toUpperCase().equals(secAnswer.toUpperCase()))
            flag = true;

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



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgotpassword);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        toolbar.setTitleTextColor(getColor(R.color.colorPrimaryDark));
        setTitle("Reset Password");

        Bundle b = getIntent().getExtras();
        userID = b.getLong("userID",0);
        usersDB = new ArrayList<>();

        database = FirebaseDatabase.getInstance("https://bigmart-sinprl.firebaseio.com/");

        spnSecurityQ = findViewById(R.id.spn_forgot_security);
        edtSecurityAnswer = findViewById(R.id.edt_forgot_secAnswer);
        edtPassword = findViewById(R.id.edt_forgot_pin);


        edtPassword.setVisibility(View.GONE);

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
}