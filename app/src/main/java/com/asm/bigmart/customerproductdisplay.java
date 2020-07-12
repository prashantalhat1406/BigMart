package com.asm.bigmart;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.os.Bundle;

import com.asm.bigmart.adapters.CU_ProductDisplay;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class customerproductdisplay extends AppCompatActivity {

    private List<Product> products,cartProducts;
    ListView productList;
    private long userID;
    String subcategoryName,categoryName;
    String searchItem;
    FirebaseDatabase database;
    Integer productQty;
    Double cartAmount;
    LinearLayout empty, normal;

    public Integer isProductInCart(final String productID)
    {
        productQty = 0;

        for (Product cartProduct : cartProducts) {
            if(productID.equals(cartProduct.ID))
                productQty = cartProduct.QtyNos;
        }

        return productQty;
    }

    public void goToHome(){
        if (subcategoryName.length() == 0) {
            Intent homeIntent = new Intent(customerproductdisplay.this, home.class);
            Bundle extras = new Bundle();
            extras.putLong("userID", userID);
            homeIntent.putExtras(extras);
            startActivity(homeIntent);
            finish();
        }else{
            Intent homeIntent = new Intent(customerproductdisplay.this, subcategorydisplay.class);
            Bundle extras = new Bundle();
            extras.putLong("userID", userID);
            extras.putString("categoryName", categoryName);
            homeIntent.putExtras(extras);
            startActivity(homeIntent);
            finish();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_productdisplay);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitleTextColor(getColor(R.color.colorPrimaryDark));
        toolbar.setOverflowIcon(getDrawable(R.drawable.ic_menuicon));

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_back);

        ViewGroup.LayoutParams params = (ViewGroup.LayoutParams)toolbar.getLayoutParams();
        params.height = 160;
        toolbar.setLayoutParams(params);

        Bundle b = getIntent().getExtras();
        userID = b.getLong("userID");
        subcategoryName = b.getString("subCategoryName","");
        categoryName = b.getString("categoryName","");
        searchItem = b.getString("searchItem","");

        setTitle(subcategoryName);

        switch (subcategoryName.toLowerCase()){
            case "detergentsfabric": setTitle("Detergents & Fabric"); break;
            case "homeutility": setTitle("Home Utility"); break;
            case "utensilcleaners": setTitle("Utensil Cleaners"); break;
            case "cleaners": setTitle("Cleaners"); break;
            case "readytocook": setTitle("Ready To Cook"); break;
            case "oralcare": setTitle("Oral Care"); break;
            case "personalhygiene": setTitle("Personal Hygiene"); break;
            case "sanitarynapkins": setTitle("Sanitary Napkins"); break;
            case "cookingoil": setTitle("Cooking Oil"); break;
            case "facecare": setTitle("Face Care"); break;
            case "showergel": setTitle("Shower Gel"); break;
            case "sunscreen": setTitle("Sun-Screen"); break;
            case "talcompowder": setTitle("Talcom Powder"); break;
            case "dryfruits": setTitle("Dry Fruits"); break;
            case "deos": setTitle("Deos & Perfumes"); break;
            case "soaps": setTitle("Soaps"); break;
            case "health": setTitle("Health & Wellness"); break;
            case "shaving": setTitle("Shaving Needs"); break;
            case "jams": setTitle("Jams & Spreads"); break;
            case "pasta": setTitle("Pasta & Noodles"); break;
            case "ketchup": setTitle("Ketchup & Sauce"); break;
            case "freshner": setTitle("Freshener & Repellents"); break;
            case "masala": setTitle("Masala & Spices"); break;
            case "salt": setTitle("Salt/Sugar/Jaggery"); break;
            case "flours": setTitle("Flours & Grains"); break;
            case "ghee": setTitle("Ghee & Vanaspati"); break;
            case "rice": setTitle("Rice & Rice Products"); break;
            case "milk": setTitle("Dairy Products"); break;
            case "biscuits": setTitle("Biscuits & Cookies"); break;
            case "bakery": setTitle("Bakery"); break;
            case "pickles": setTitle("Pickles"); break;
            case "dals": setTitle("Dals"); break;
            case "pulses": setTitle("Pulses"); break;
            default : setTitle("Products");
        }

        products = new ArrayList<Product>();
        cartProducts = new ArrayList<Product>();
        cartProducts.clear();
        productList = findViewById(R.id.listProduct);

        empty = findViewById(R.id.layout_productdisplay_empty);
        normal = findViewById(R.id.layout_productdisplay_list);

        database = FirebaseDatabase.getInstance("https://bigmart-sinprl.firebaseio.com/");

        DatabaseReference databaseReference = database.getReference("Users/"+userID+"/TempOrder/");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot postSnapshot: dataSnapshot.getChildren()) {
                    Product product = postSnapshot.getValue(Product.class);
                    product.setID(postSnapshot.getKey());
                    cartProducts.add(product);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        Query query = database.getReference("/Products");
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                List<Product> tempproducts;
                tempproducts = new ArrayList<Product>();

                for (DataSnapshot postSnapshot: dataSnapshot.getChildren()) {
                    Product product = postSnapshot.getValue(Product.class);
                    product.setID(postSnapshot.getKey());
                    product.setQtyNos(isProductInCart(product.ID));
                    tempproducts.add(product);

                    if (searchItem.length() > 0) {
                        if (product.Name.toLowerCase().contains(searchItem.toLowerCase()))
                            products.add(product);
                    } else {
                        if (subcategoryName.equals(product.SubCategory))
                            products.add(product);
                    }
                }
                //adapterProduct productAdaper;
                CU_ProductDisplay productAdaper;
                if (products.size() == 0)
                {
                    /*Toast error = Toast.makeText(customerproductdisplay.this, "No Match found. Showing all Products.",Toast.LENGTH_SHORT);
                    error.setGravity(Gravity.TOP, 0, 0);

                    error.show();
                    productAdaper = new adapterProduct(customerproductdisplay.this,R.layout.itemproduct,tempproducts, userID,1);*/
                    empty.setVisibility(View.VISIBLE);
                    normal.setVisibility(View.GONE);
                }
                else {
                    empty.setVisibility(View.GONE);
                    normal.setVisibility(View.VISIBLE);
                    productAdaper = new CU_ProductDisplay(customerproductdisplay.this, R.layout.itemproduct, products, userID, 1);
                    productList.setAdapter(productAdaper);
                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {      }
        });

        Button continueShopping = findViewById(R.id.but_productdisplay_continueShopping);
        continueShopping.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToHome();
            }
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

        badge.setCount(count, amount);
        icon.mutate();
        icon.setDrawableByLayerId(R.id.ic_badge, badge);
    }

    public void showLogoutAlertDialog(){

        /*AlertDialog.Builder logoutAlertBuilder = new AlertDialog.Builder(customerproductdisplay.this);
        logoutAlertBuilder.setMessage("Are you sure to Logout ?");
        logoutAlertBuilder.setCancelable(false);
        logoutAlertBuilder.setPositiveButton(
                "YES", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent logoutIntent = new Intent(customerproductdisplay.this, login.class);
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

        final Dialog dialog = new Dialog(customerproductdisplay.this);
        dialog.setContentView(R.layout.logoutdialog);
        dialog.setCancelable(false);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        Button yes = dialog.findViewById(R.id.dialog_btn_yes);
        yes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent logoutIntent = new Intent(customerproductdisplay.this, login.class);
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
                        Intent viewCartIntent = new Intent(customerproductdisplay.this, customercartdisplay.class);
                        Bundle extras = new Bundle();
                        extras.putLong("userID", userID);
                        viewCartIntent.putExtras(extras);
                        startActivity(viewCartIntent);
                        finish();
                    }
                    else
                    {
                        Toast error = Toast.makeText(customerproductdisplay.this, "No Items In Cart !",Toast.LENGTH_SHORT);
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
            Intent viewCartIntent = new Intent(customerproductdisplay.this, customerorderhistory.class);
            Bundle extras = new Bundle();
            extras.putLong("userID", userID);
            viewCartIntent.putExtras(extras);
            startActivity(viewCartIntent);
            //finish();
            //return true;
        }
        if (id == R.id.menu_profile) {
            Intent profileIntent = new Intent(customerproductdisplay.this, customerprofile.class);
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
