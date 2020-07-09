package com.asm.bigmart;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.asm.bigmart.adapters.SO_Report_User;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class shopowneruserdisplay extends AppCompatActivity {

    FirebaseDatabase database;
    private List<User> users;
    ListView userList;
    EditText edtsearch;
    private String searchItem="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shopowneruserdisplay);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_back);

        toolbar.setTitleTextColor(getColor(R.color.colorPrimaryDark));

        database = FirebaseDatabase.getInstance("https://bigmart-sinprl.firebaseio.com/");

        edtsearch = findViewById(R.id.edt_showowner_user_search);
        userList = findViewById(R.id.listShopOwnerUser);
        users = new ArrayList<>();

        edtsearch.setText("");
        edtsearch.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                if (edtsearch.getText().length() > 0){
                    if(event.getAction() == MotionEvent.ACTION_UP) {
                        if(event.getRawX() >= (edtsearch.getRight() - edtsearch.getCompoundDrawables()[2].getBounds().width())) {
                            edtsearch.setText("");
                            //edtsearch.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_clear, 0);
                            edtsearch.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);

                            return true;
                        }
                    }}
                return false;
            }


        });

        edtsearch.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    InputMethodManager imm = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
                    imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
                    edtsearch.clearFocus();
                    return true;
                }
                return false;
            }
        });

        edtsearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                edtsearch.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_clear, 0);
                List<User> tempList = new ArrayList<>();
                if (edtsearch.getText().length() == 0)
                    tempList = users;
                else {
                    tempList.clear();
                    for (User user : users) {
                        if (user.Name.toUpperCase().contains(edtsearch.getText().toString().trim().toUpperCase()))
                            tempList.add(user);
                    }
                }

                //adapterShopOwnerUser userAdaper = new adapterShopOwnerUser(shopowneruserdisplay.this, R.layout.itemshopowneruser, tempList);
                SO_Report_User userAdaper = new SO_Report_User(shopowneruserdisplay.this, R.layout.itemshopowneruser, tempList);
                userList.setAdapter(userAdaper);
            }
            @Override
            public void afterTextChanged(Editable s) {           }
        });

        Query query = database.getReference("Users/");
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                users.clear();

                for (DataSnapshot postSnapshot: dataSnapshot.getChildren()) {
                    User user = postSnapshot.getValue(User.class);
                    user.setID(postSnapshot.getKey());
                    users.add(user);
                }
                //adapterShopOwnerUser userAdaper = new adapterShopOwnerUser(shopowneruserdisplay.this, R.layout.itemshopowneruser, users);
                SO_Report_User userAdaper = new SO_Report_User(shopowneruserdisplay.this, R.layout.itemshopowneruser, users);
                userList.setAdapter(userAdaper);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });

        userList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id)
            {
                Intent userIntent = new Intent(shopowneruserdisplay.this, shopowneruserdetails.class);
                Bundle extras = new Bundle();
                String selected = ((TextView) view.findViewById(R.id.txt_shopowner_userID)).getText().toString();
                extras.putLong("userID", Long.parseLong(selected));
                //extras.putString("userID", ""+selected);
                userIntent.putExtras(extras);
                //startActivityForResult(userIntent,100);
                startActivity(userIntent);
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