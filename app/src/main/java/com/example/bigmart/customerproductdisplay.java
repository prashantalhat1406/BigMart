package com.example.bigmart;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
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

import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class customerproductdisplay extends AppCompatActivity {

    private List<Product> products,cartProducts;
    ListView productList;
    private long userID;
    String subcategoryName;
    String searchItem;
    FirebaseDatabase database;
    Integer productQty;

    public Integer isProductInCart(final String productID)
    {
        productQty = 0;

        for (Product cartProduct : cartProducts) {
            if(productID.equals(cartProduct.ID))
                productQty = cartProduct.QtyNos;
        }

        /*DatabaseReference databaseReference = database.getReference("Users/"+userID+"/TempOrder/");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot postSnapshot: dataSnapshot.getChildren()) {
                    Product product = postSnapshot.getValue(Product.class);
                    product.setID(postSnapshot.getKey());
                    if (productID.equals(product.ID))
                        productQty=product.QtyNos;
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });*/
        //databaseReference.child(""+product.ID).setValue(product);

        /*final Query cartquery = database.getReference("Users/"+userID+"/TempOrder");
        cartquery.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                //cartProducts.clear();
                for (DataSnapshot postSnapshot: dataSnapshot.getChildren()) {
                    Product product = postSnapshot.getValue(Product.class);
                    product.setID(postSnapshot.getKey());
                    if (productID.equals(product.ID))
                        productQty=product.QtyNos;
                    //cartProducts.add(product);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });*/

        return productQty;
    }
/*
    @Override
    protected void onResume() {
        final FirebaseDatabase database = FirebaseDatabase.getInstance("https://bigmart-sinprl.firebaseio.com/");
        Query query = database.getReference("/Products");
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                products.clear();

                for (DataSnapshot postSnapshot: dataSnapshot.getChildren())
                {
                    Product product = postSnapshot.getValue(Product.class);
                    product.setID(postSnapshot.getKey());

                    product.setQtyNos(isProductInCart(product.ID));

                    if(searchItem.length() > 0)
                    {
                        if (product.Name.toLowerCase().contains(searchItem.toLowerCase()))
                            products.add(product);
                    }
                    else {
                        if (subcategoryName.equals(product.SubCategory))
                            products.add(product);
                    }
                    adapterProduct productAdaper = new adapterProduct(productdisplay.this,R.layout.itemproduct,products, userID,1);
                    productList.setAdapter(productAdaper);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        super.onResume();
    }*/

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_productdisplay);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Bundle b = getIntent().getExtras();
        userID = b.getLong("userID");
        subcategoryName = b.getString("subCategoryName","");
        searchItem = b.getString("searchItem","");

        products = new ArrayList<Product>();
        cartProducts = new ArrayList<Product>();
        cartProducts.clear();
        productList = findViewById(R.id.listProduct);

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

                for (DataSnapshot postSnapshot: dataSnapshot.getChildren())
                {
                    Product product = postSnapshot.getValue(Product.class);
                    product.setID(postSnapshot.getKey());
                    product.setQtyNos(isProductInCart(product.ID));

                    if(searchItem.length() > 0)
                    {
                        if (product.Name.toLowerCase().contains(searchItem.toLowerCase()))
                            products.add(product);
                    }
                    else {
                        if (subcategoryName.equals(product.SubCategory))
                            products.add(product);
                    }
                    adapterProduct productAdaper = new adapterProduct(customerproductdisplay.this,R.layout.itemproduct,products, userID,1);
                    productList.setAdapter(productAdaper);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });

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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.menu_viewcart) {
            Intent viewCartIntent = new Intent(customerproductdisplay.this, customercartdisplay.class);
            Bundle extras = new Bundle();
            extras.putLong("userID", userID);
            viewCartIntent.putExtras(extras);
            startActivity(viewCartIntent);
            finish();
            //return true;
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
            Intent logoutIntent = new Intent(customerproductdisplay.this, login.class);
            logoutIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK |Intent.FLAG_ACTIVITY_CLEAR_TOP);
            Toast error = Toast.makeText(customerproductdisplay.this, "Logout Successful",Toast.LENGTH_SHORT);
            error.setGravity(Gravity.TOP, 0, 0);
            error.show();
            startActivity(logoutIntent);
            finish();
        }

        return super.onOptionsItemSelected(item);
    }
}
