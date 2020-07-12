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
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import java.util.List;

public class customersubcategorydisplay extends AppCompatActivity {

    private long userID;

    private List<SubCategory> subCategories;
    private  int count = 0;
    FirebaseDatabase database;
    String categoryName;
    Double cartAmount;
    ProgressBar progressBar;
    LinearLayout subcategoriesLayout;
    private static final int[] BUTTON_IDS = {
            R.id.but_subcat_1,
            R.id.but_subcat_2,
            R.id.but_subcat_3,
            R.id.but_subcat_4,
            R.id.but_subcat_5,
            R.id.but_subcat_6,
            R.id.but_subcat_7,
            R.id.but_subcat_8,
            R.id.but_subcat_9
    };

    public void goToHome(){
        Intent homeIntent = new Intent(customersubcategorydisplay.this, home.class);
        Bundle extras = new Bundle();
        extras.putLong("userID", userID);
        homeIntent.putExtras(extras);
        startActivity(homeIntent);
        finish();
    }

    public void showButton(String name, int id){
        ImageButton b = findViewById(BUTTON_IDS[id]);
        b.setTag(name);
        b.setVisibility(View.VISIBLE);
        b.setScaleType(ImageView.ScaleType.FIT_XY);
        b.setBackgroundColor(Color.WHITE);
        int id_ = getResources().getIdentifier(name.toLowerCase(), "drawable", getPackageName());
        b.setImageResource(id_);
    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_subcat);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitleTextColor(getColor(R.color.colorPrimaryDark));
        toolbar.setOverflowIcon(getDrawable(R.drawable.ic_menuicon));

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_back);

        /*ViewGroup.LayoutParams params = (ViewGroup.LayoutParams)toolbar.getLayoutParams();
        params.height = 160;
        toolbar.setLayoutParams(params);*/

        /*ActionBar ab = getActionBar();
        ab.setDisplayHomeAsUpEnabled(true);*/


        Bundle b = getIntent().getExtras();
        categoryName = b.getString("categoryName");
        userID = b.getLong("userID");

        progressBar = findViewById(R.id.progressbarSubCategories);
        subcategoriesLayout = findViewById(R.id.layout_subcategories);
        progressBar.setIndeterminate(true);
        progressBar.setVisibility(View.VISIBLE);

        switch(categoryName){
            case "Dairy" : setTitle("Dairy & Beverages"); break;
            case "Grocery" : setTitle("Grocery"); break;
            case "HomeKitchen" : setTitle("Home & Kitchen"); break;
            case "SkinCare" : setTitle("Skin Care"); break;
            case "PersonalCare" : setTitle("Personal Care"); break;
            case "PackagedFoods" : setTitle("Packaged Foods"); break;
        }
        //setTitle(categoryName);

        for(int id : BUTTON_IDS) {
            final ImageButton button = (ImageButton) findViewById(id);
            button.setVisibility(View.INVISIBLE);
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent productIntent = new Intent(customersubcategorydisplay.this, customerproductdisplay.class);
                    Bundle extras = new Bundle();
                    extras.putLong("userID", userID);
                    extras.putString("subCategoryName", button.getTag().toString());
                    extras.putString("categoryName", categoryName);
                    productIntent.putExtras(extras);
                    startActivity(productIntent);
                }
            });
        }

        database = FirebaseDatabase.getInstance("https://bigmart-sinprl.firebaseio.com/");
        Query query = database.getReference("/SubCategories");

        query.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                SubCategory subCategory = dataSnapshot.getValue(SubCategory.class);
                if(categoryName.equals(subCategory.Category))
                {
                    showButton(subCategory.Name,count);
                    count++;
                }
                progressBar.setVisibility(View.GONE);
                subcategoriesLayout.setVisibility(View.VISIBLE);
            }
            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) { }
            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {       }
            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {       }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) { }
        });
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        menu.clear();

        getMenuInflater().inflate(R.menu.asmmenu, menu);
        //homeMenu = menu;
        MenuItem itemCart = menu.findItem(R.id.menu_viewcart);
        final LayerDrawable icon = (LayerDrawable) itemCart.getIcon();
        Query query = database.getReference("Users/"+userID+"/TempOrder");
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Integer cnt =0;
                cartAmount = 0.0;
                for (DataSnapshot postSnapshot: dataSnapshot.getChildren()) {
                    Product product = postSnapshot.getValue(Product.class);
                    cnt = cnt + product.QtyNos;
                    cartAmount = cartAmount + ((product.MRP - product.Discount) * product.QtyNos);
                }
                setBadgeCount(getBaseContext(), icon, ""+cnt,""+cartAmount);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {            }
        });

        return super.onPrepareOptionsMenu(menu);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.asmmenu, menu);

        MenuItem itemCart = menu.findItem(R.id.menu_viewcart);
        final LayerDrawable icon = (LayerDrawable) itemCart.getIcon();
        Query query = database.getReference("Users/"+userID+"/TempOrder");
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Integer cnt =0;
                cartAmount = 0.0;
                for (DataSnapshot postSnapshot: dataSnapshot.getChildren()) {
                    Product product = postSnapshot.getValue(Product.class);
                    cnt = cnt + product.QtyNos;
                    cartAmount = cartAmount + ((product.MRP - product.Discount) * product.QtyNos);
                }
                setBadgeCount(getBaseContext(), icon, ""+cnt,""+cartAmount);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {            }
        });

        return true;
    }

    public static void setBadgeCount(Context context, LayerDrawable icon, String count, String amount) {

        BadgeDrawable badge;

        // Reuse drawable if possible
        Drawable reuse = icon.findDrawableByLayerId(R.id.ic_badge);
        if (reuse != null && reuse instanceof BadgeDrawable) {
            badge = (BadgeDrawable) reuse;
        } else {
            badge = new BadgeDrawable(context);
        }

        badge.setCount(count,amount);
        icon.mutate();
        icon.setDrawableByLayerId(R.id.ic_badge, badge);
    }

    public void showLogoutAlertDialog(){

        /*AlertDialog.Builder logoutAlertBuilder = new AlertDialog.Builder(subcategorydisplay.this);
        logoutAlertBuilder.setMessage("Are you sure to Logout ?");
        logoutAlertBuilder.setCancelable(false);
        logoutAlertBuilder.setPositiveButton(
                "YES", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent logoutIntent = new Intent(subcategorydisplay.this, login.class);
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

        final Dialog dialog = new Dialog(customersubcategorydisplay.this);
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
                Intent logoutIntent = new Intent(customersubcategorydisplay.this, login.class);
                logoutIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK |Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(logoutIntent);
                finish();

            }
        });
        dialog.show();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == android.R.id.home) {
            goToHome();
        }

        //noinspection SimplifiableIfStatement
        if (id == R.id.menu_viewcart) {
            Query query = database.getReference("Users/"+userID+"/TempOrder");
            query.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if(dataSnapshot.exists())
                    {
                        Intent viewCartIntent = new Intent(customersubcategorydisplay.this, customercartdisplay.class);
                        Bundle extras = new Bundle();
                        extras.putLong("userID", userID);
                        viewCartIntent.putExtras(extras);
                        startActivity(viewCartIntent);
                        finish();
                    }
                    else
                    {
                        Toast error = Toast.makeText(customersubcategorydisplay.this, "No Items In Cart !",Toast.LENGTH_SHORT);
                        error.setGravity(Gravity.TOP, 0, 0);
                        error.show();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }

        if (id == R.id.menu_orderhistory) {
            Intent viewCartIntent = new Intent(customersubcategorydisplay.this, customerorderhistory.class);
            Bundle extras = new Bundle();
            extras.putLong("userID", userID);
            viewCartIntent.putExtras(extras);
            startActivity(viewCartIntent);
            //return true;
        }

        if (id == R.id.menu_profile) {
            Intent profileIntent = new Intent(customersubcategorydisplay.this, customerprofile.class);
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
        goToHome();
        super.onBackPressed();
    }


}
