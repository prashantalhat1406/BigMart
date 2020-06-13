package com.example.bigmart;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
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
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class home extends AppCompatActivity {

    public Menu homeMenu;
    private long userID;
    private  int count = 0;
    FirebaseDatabase database;
    EditText search;
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

    public void gotoProductDisplay(){
        Intent productdispalyIntent = new Intent(home.this, customerproductdisplay.class);
        Bundle extras = new Bundle();
        extras.putString("searchItem", search.getText().toString().trim());
        extras.putLong("userID", userID);
        productdispalyIntent.putExtras(extras);
        startActivity(productdispalyIntent);
        finish();
    }

    @Override
    protected void onResume() {
        supportInvalidateOptionsMenu();
        invalidateOptionsMenu();
        EditText search = findViewById(R.id.edt_home_search);
        search.setText("");
        //this.onCreateOptionsMenu(homeMenu);
        super.onResume();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Bundle b = getIntent().getExtras();
        userID = b.getLong("userID");


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

        search = findViewById(R.id.edt_home_search);
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
        });


        ImageButton searchBut = findViewById(R.id.but_home_search);
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
        });

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


    }

    public void displaySubCategory(String categoryName)    {
        Intent subcatIntent = new Intent(home.this, subcategorydisplay.class);
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
        Query query = database.getReference("Users/"+userID+"/TempOrder");
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Integer cnt =0;
                for (DataSnapshot postSnapshot: dataSnapshot.getChildren()) {
                    Product product = postSnapshot.getValue(Product.class);
                    cnt = cnt + product.QtyNos;
                }
                setBadgeCount(getBaseContext(), icon, ""+cnt);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {            }
        });
        return true;
    }

    public static void setBadgeCount(Context context, LayerDrawable icon, String count) {

        BadgeDrawable badge;

        // Reuse drawable if possible
        Drawable reuse = icon.findDrawableByLayerId(R.id.ic_badge);
        if (reuse != null && reuse instanceof BadgeDrawable) {
            badge = (BadgeDrawable) reuse;
        } else {
            badge = new BadgeDrawable(context);
        }

        badge.setCount(count);
        icon.mutate();
        icon.setDrawableByLayerId(R.id.ic_badge, badge);
    }

    public void showLogoutAlertDialog(){

        AlertDialog.Builder logoutAlertBuilder = new AlertDialog.Builder(home.this);
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
        AlertDialog alertLogout = logoutAlertBuilder.create();
        alertLogout.show();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

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
