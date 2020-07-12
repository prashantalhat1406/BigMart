package com.asm.bigmart;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.os.Bundle;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.Gravity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class home extends AppCompatActivity {

    public Menu homeMenu;
    private long userID;
    private  int count = 0;
    FirebaseDatabase database;
    EditText search;
    AutoCompleteTextView txtSearch;
    private List<String> productNames;
    Integer productCount;
    Double cartAmount;

    private static final int[] BUTTON_IDS = {
            R.id.but_category_1,
            R.id.but_category_2,
            R.id.but_category_3,
            R.id.but_category_4,
            R.id.but_category_5,
            R.id.but_category_6,
            R.id.but_category_7,
            R.id.but_category_8
    };

    public void showButton(String name, int id){
        ImageButton b = (ImageButton) findViewById(BUTTON_IDS[id]);
        b.setTag(name);
        b.setVisibility(View.VISIBLE);
        b.setScaleType(ImageView.ScaleType.FIT_XY);
        b.setBackgroundColor(Color.WHITE);
        int id_ = getResources().getIdentifier(name.toLowerCase(), "drawable", getPackageName());
        b.setImageResource(id_);

    }

    /*public void gotoProductDisplay(){
        Intent productdispalyIntent = new Intent(home.this, customerproductdisplay.class);
        Bundle extras = new Bundle();
        extras.putString("searchItem", search.getText().toString().trim());
        extras.putLong("userID", userID);
        productdispalyIntent.putExtras(extras);
        startActivity(productdispalyIntent);
        finish();
    }*/

    public void gotoProductDisplay(String productName){
        Intent productdispalyIntent = new Intent(home.this, customerproductdisplay.class);
        Bundle extras = new Bundle();
        extras.putString("searchItem", productName);
        extras.putLong("userID", userID);
        productdispalyIntent.putExtras(extras);
        startActivity(productdispalyIntent);
        finish();
    }

    @Override
    protected void onResume() {
        //supportInvalidateOptionsMenu();
        invalidateOptionsMenu();

        //EditText search = findViewById(R.id.edt_home_search);
        //search.setText("");
        //this.onCreateOptionsMenu(homeMenu);
        super.onResume();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitleTextColor(getColor(R.color.colorPrimaryDark));
        toolbar.setOverflowIcon(getDrawable(R.drawable.ic_menuicon));

        /*ViewGroup.LayoutParams params = (ViewGroup.LayoutParams)toolbar.getLayoutParams();
        params.height = 160;
        toolbar.setLayoutParams(params);*/



        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_back);

        Bundle b = getIntent().getExtras();

        userID = b.getLong("userID");
        productCount = b.getInt("productCount",0);



        productNames = new ArrayList<String>();


        for(int id : BUTTON_IDS) {
            ImageButton imageButton = (ImageButton) findViewById(id);
            imageButton.setVisibility(View.GONE);
            imageButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    displaySubCategory(((ImageButton) v). getTag().toString());
                }
            });

        }

        /*search = findViewById(R.id.edt_home_search);
        search.clearFocus();
        search.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    gotoProductDisplay();
                    return true;
                }
                return false;
            }
        });*/


        /*ImageButton searchBut = findViewById(R.id.but_home_search);
        searchBut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (search.getText().length() == 0){
                    Toast error = Toast.makeText(home.this, "Error : Please enter text to search",Toast.LENGTH_SHORT);
                    error.setGravity(Gravity.TOP, 0, 0);
                    error.show();
                }else {
                    gotoProductDisplay();
                }
            }
        });*/

        //FirebaseDatabase database = FirebaseDatabase.getInstance("https://bigmart-sinprl.firebaseio.com/");
        database = FirebaseDatabase.getInstance("https://bigmart-sinprl.firebaseio.com/");
        Query query = database.getReference("/Categories");

        query.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                Category category = dataSnapshot.getValue(Category.class);
                showButton(category.Name,count);
                count++;
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {            }
            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {            }
            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {             }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {             }
        });

        Query productsQuery = database.getReference("/Products");
        productsQuery.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot postSnapshot: dataSnapshot.getChildren()) {
                    Product product = postSnapshot.getValue(Product.class);
                    productNames.add(product.Name);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {      }
        });

        txtSearch = findViewById(R.id.txt_auto_search);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,R.layout.support_simple_spinner_dropdown_item,productNames);
        txtSearch.setAdapter(adapter);
        txtSearch.setThreshold(1);
        txtSearch.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Object t = parent.getItemAtPosition(position);
                if (t.toString().length() == 0){
                    /*Toast error = Toast.makeText(home.this, "Error : Please enter text to search",Toast.LENGTH_SHORT);
                    error.setGravity(Gravity.TOP, 0, 0);
                    error.show();*/
                    txtSearch.setError("Enter Product Name");
                }else {
                    gotoProductDisplay(t.toString());
                }
            }
        });

        txtSearch.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                if(event.getAction() == MotionEvent.ACTION_UP) {
                    if(event.getRawX() >= (txtSearch.getRight() - txtSearch.getCompoundDrawables()[2].getBounds().width())) {
                        if (txtSearch.getText().toString().trim().length() == 0){
                            /*Toast error = Toast.makeText(home.this, "Error : Please enter text to search",Toast.LENGTH_SHORT);
                            error.setGravity(Gravity.TOP, 0, 0);
                            error.show();*/
                            txtSearch.setError("Enter Product Name");
                        }else {
                            gotoProductDisplay(txtSearch.getText().toString());
                        }
                    }
                }
                return false;
            }
        });

        txtSearch.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    if (txtSearch.getText().toString().trim().length() == 0)
                        txtSearch.setError("Enter Product Name");
                    else
                    {
                        gotoProductDisplay(txtSearch.getText().toString());
                        return true;
                    }
                }
                return false;
            }
        });


    }

    public void displaySubCategory(String categoryName)    {
        Intent subcatIntent = new Intent(home.this, customersubcategorydisplay.class);
        Bundle extras = new Bundle();
        extras.putString("categoryName", categoryName);
        extras.putLong("userID", userID);
        subcatIntent.putExtras(extras);
        startActivity(subcatIntent);
        finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.asmmenu, menu);
        homeMenu = menu;



        MenuItem itemCart = menu.findItem(R.id.menu_viewcart);
        final LayerDrawable icon = (LayerDrawable) itemCart.getIcon();
        Query query;
        query = database.getReference("Users/"+userID+"/TempOrder");
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                productCount =0;
                cartAmount = 0.0;
                for (DataSnapshot postSnapshot: dataSnapshot.getChildren()) {
                    Product product = postSnapshot.getValue(Product.class);
                    productCount = productCount + product.QtyNos;
                    cartAmount = cartAmount + ((product.MRP - product.Discount) * product.QtyNos);
                }

            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {            }
        });

        setBadgeCount(getBaseContext(), icon, ""+ productCount, "" + cartAmount);
        return true;
    }

    public static void setBadgeCount(Context context, LayerDrawable icon, String count, String amount) {

        BadgeDrawable badge;

        // Reuse drawable if possible
        Drawable reuse = icon.findDrawableByLayerId(R.id.ic_badge);
        if (reuse instanceof BadgeDrawable) {
            badge = (BadgeDrawable) reuse;
        } else {
            badge = new BadgeDrawable(context);
        }

        badge.setCount(count, amount);
        icon.mutate();
        icon.setDrawableByLayerId(R.id.ic_badge, badge);
    }

    public void showLogoutAlertDialog(){

        /*LogoutDialog logoutDialog = new LogoutDialog(home.this);
        logoutDialog.show();*/
        /*AlertDialog.Builder logoutAlertBuilder = new AlertDialog.Builder(home.this);
        logoutAlertBuilder.setMessage("Are you sure to Logout ?");
        logoutAlertBuilder.setCancelable(false);
        logoutAlertBuilder.setPositiveButton(
                "YES", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent logoutIntent = new Intent(home.this, login.class);
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

        alertLogout.setTitle("LOGOUT");

        alertLogout.show();*/

        //final Dialog dialog = new Dialog(mContext);
        //AlertDialog dialog = new AlertDialog.Builder(home.this).create();
        final Dialog dialog;
        dialog = new Dialog(home.this);
        dialog.setContentView(R.layout.logoutdialog);
        dialog.setCancelable(false);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        Button redbutton = dialog.findViewById(R.id.dialog_btn_red);
        redbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        Button greenbutton = dialog.findViewById(R.id.dialog_btn_green);
        greenbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent logoutIntent = new Intent(home.this, login.class);
                logoutIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK |Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(logoutIntent);
                finish();
            }
        });
        dialog.show();

    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        menu.clear();

        getMenuInflater().inflate(R.menu.asmmenu, menu);
        homeMenu = menu;
        MenuItem itemCart = menu.findItem(R.id.menu_viewcart);
        final LayerDrawable icon = (LayerDrawable) itemCart.getIcon();
        Query query = database.getReference("Users/"+userID+"/TempOrder");
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                int cnt =0;
                cartAmount = 0.0;
                for (DataSnapshot postSnapshot: dataSnapshot.getChildren()) {
                    Product product = postSnapshot.getValue(Product.class);
                    cnt = cnt + product.QtyNos;
                    cartAmount = cartAmount + ((product.MRP - product.Discount) * product.QtyNos);
                }
                setBadgeCount(getBaseContext(), icon, ""+cnt, ""+cartAmount);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {            }
        });

        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();


        if (id == android.R.id.home) {
            onBackPressed();
        }

        //noinspection SimplifiableIfStatement
        if (id == R.id.menu_viewcart) {

            Query query = database.getReference("Users/"+userID+"/TempOrder");
            query.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if(dataSnapshot.exists())
                    {
                        Intent viewCartIntent = new Intent(home.this, customercartdisplay.class);
                        Bundle extras = new Bundle();
                        extras.putLong("userID", userID);
                        viewCartIntent.putExtras(extras);
                        startActivity(viewCartIntent);
                        finish();
                    }
                    else
                    {
                        Toast error = Toast.makeText(home.this, "No Items In Cart !",Toast.LENGTH_SHORT);
                        error.setGravity(Gravity.TOP, 0, 0);
                        error.show();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
            //return true;
        }

        if (id == R.id.menu_orderhistory) {
            Intent viewCartIntent = new Intent(home.this, customerorderhistory.class);
            Bundle extras = new Bundle();
            extras.putLong("userID", userID);
            //extras.put
            viewCartIntent.putExtras(extras);
            startActivity(viewCartIntent);
            //return true;
        }

        if (id == R.id.menu_profile) {
            Intent profileIntent = new Intent(home.this, customerprofile.class);
            Bundle extras = new Bundle();
            extras.putLong("userID", userID);
            profileIntent.putExtras(extras);
            startActivity(profileIntent);
            //finish();
            //return true;
        }

        if (id == R.id.menu_logout) {
            showLogoutAlertDialog();
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        showLogoutAlertDialog();

    }
}
