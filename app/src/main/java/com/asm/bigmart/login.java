package com.asm.bigmart;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;

import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
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
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class login extends AppCompatActivity {


    private Integer showpassword = 0;
    public List<User> usersDB;
    EditText edtMobile,edtPassword;
    Button butLogin , butRegister;
    private FirebaseAuth mAuth;
    private User user;
    TextView txtforgotPassword;


    public void showErrorMessage(String message){
        Toast error = Toast.makeText(login.this, message,Toast.LENGTH_SHORT);
        error.setGravity(Gravity.TOP, 0, 0);
        error.show();
    }

    public Boolean isMobileValid(String mobileNumber){
        boolean flag = false;

        if (mobileNumber.length() == 0)
        {
            edtMobile.setError("Mobile Number Should not be empty");
            edtMobile.requestFocus();
        }
        else
        {
            if (mobileNumber.length() < 10)
            {
                edtMobile.setError("Enter 10 Digit Mobile Number");
                edtMobile.requestFocus();
            }
            else
            {
                if (Double.parseDouble(mobileNumber) < 7000000000.00)
                {
                    edtMobile.setError("Invalid Mobile Number");
                    edtMobile.requestFocus();
                }
                else
                    flag = true;
            }
        }
        return flag;
    }

    public Boolean isPasswordValid(String password){
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

    public Boolean isUserExist(String mobileNumber){
        boolean flag = false;

        for (User u : usersDB) {
            if (u.Mobile.equals(Long.parseLong(mobileNumber)))
            {
                flag = true;
                user = u;
                break;
            }
        }
        return flag;
    }

    public Integer getPassword(String mobileNumber)
    {
        Integer pin = 0;

        for (User u : usersDB) {
            if (u.Mobile.equals(Long.parseLong(mobileNumber)))
            {pin = u.Password;break;}
        }
        return pin;
    }


    public void performLogin() {
        if(isMobileValid(edtMobile.getText().toString()) && isPasswordValid(edtPassword.getText().toString()))
        {
            if (isUserExist(edtMobile.getText().toString()))
            {
                Integer pin = getPassword(edtMobile.getText().toString());
                long userID = Long.parseLong(edtMobile.getText().toString());
                if (pin == Integer.parseInt(edtPassword.getText().toString()))
                {
                    //if (userID == 9999999999.0) {
                    if (user.Access.equals("Admin")) {
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
                        edtPassword.setError("InCorrect Pin.");
                        edtPassword.setText("");
                    }
                }
            }else {
                showErrorMessage("User does not exist. Please Register first.");
                //edtPassword.setText("");
                Intent registerIntent = new Intent(login.this, registration.class);
                Bundle extras = new Bundle();
                extras.putLong("userID", Long.parseLong(edtMobile.getText().toString()));
                registerIntent.putExtras(extras);
                startActivity(registerIntent);
                finish();
            }
        }
    }


    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //String versionName = BuildConfig.VERSION_NAME;
        TextView versionName = findViewById(R.id.versionNumber);
        versionName.setText(getString( R.string.developerName) + "\nv"+BuildConfig.VERSION_NAME);

        toolbar.setTitleTextColor(getColor(R.color.colorPrimaryDark));

        usersDB = new ArrayList<>();

        FirebaseApp.initializeApp(login.this);

        mAuth = FirebaseAuth.getInstance();

        FirebaseDatabase database = FirebaseDatabase.getInstance("https://bigmart-sinprl.firebaseio.com/");

        Query query = database.getReference("/Users");
        query.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                User user = dataSnapshot.getValue(User.class);
                if (user != null) {
                    user.setID(dataSnapshot.getKey());
                    usersDB.add(user);
                }
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
        edtMobile.requestFocus();
        edtPassword = findViewById(R.id.edt_login_password);
        edtMobile.setText("");

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

        edtPassword.setText("");
        edtPassword.setLongClickable(false);
        edtPassword.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_GO) {
                    performLogin();
                }
                return false;
            }
        });
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

        //Adding one line of comment


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

        txtforgotPassword = findViewById(R.id.txt_forgotpassword);
        txtforgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                if(isMobileValid(edtMobile.getText().toString())){
                    if (isUserExist(edtMobile.getText().toString()))
                    {
                        Intent registerIntent = new Intent(login.this, forgotpassword.class);
                        Bundle extras = new Bundle();
                        if (edtMobile.getText().toString().length() > 0)
                            extras.putLong("userID", Long.parseLong(edtMobile.getText().toString()));
                        else
                            extras.putLong("userID", Long.parseLong("0"));
                        registerIntent.putExtras(extras);
                        startActivity(registerIntent);
                        //finish();
                    }else{
                        edtMobile.setError("User does not exists!");
                        edtMobile.requestFocus();
                    }
                }


            }
        });

        butRegister = findViewById(R.id.but_login_register);
        butRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                long userMobile;

                if (edtMobile.getText().toString().length() == 0)
                    userMobile = 0L;
                else
                    userMobile = Long.parseLong(edtMobile.getText().toString());

                boolean exitingUser = false;
                for (User user : usersDB) {
                    if (user.Mobile.equals(userMobile))
                    {
                        exitingUser = true; break;
                    }
                }

                if (exitingUser)
                {
                    //showErrorMessage("User already exists.");
                    edtPassword.setError("User already exists.");
                    edtPassword.setText("");
                    edtPassword.requestFocus();
                }else{
                    Intent registerIntent = new Intent(login.this, registration.class);
                    Bundle extras = new Bundle();
                    extras.putLong("userID", userMobile);
                    registerIntent.putExtras(extras);
                    startActivity(registerIntent);
                    finish();
                }

            }
        });

        if  (android.os.Build.VERSION.SDK_INT < 24);
        {

            LinearLayout.LayoutParams lparams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,0,20);
            lparams.setMargins(3,3,3,3);
            edtPassword.setLayoutParams(lparams);
            edtMobile.setLayoutParams(lparams);
            butLogin.setLayoutParams(lparams);
            butRegister.setLayoutParams(lparams);

        }






    }

    @Override
    protected void onResume() {
        edtPassword.setText("");
        edtMobile.setText("");
        edtMobile.requestFocus();
        super.onResume();
    }


}
