package com.example.bigmart;

import android.content.Intent;
import android.os.Bundle;

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
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class shopownerproductdisplay extends AppCompatActivity {

    FirebaseDatabase database;
    private List<Product> products;
    ListView productList;
    EditText edtsearch;

    @Override
    protected void onResume() {
        edtsearch.setText("");
        super.onResume();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shopownerproductdisplay);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        database = FirebaseDatabase.getInstance("https://bigmart-sinprl.firebaseio.com/");


        productList = findViewById(R.id.listShopOwnerProduct);
        products = new ArrayList<Product>();
        edtsearch = findViewById(R.id.edt_showowner_product_search);
        edtsearch.setText("");
        edtsearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                List<Product> tempList = new ArrayList<Product>();
                if (edtsearch.getText().length() == 0)
                    tempList = products;
                else {
                    tempList.clear();
                    for (Product product : products) {
                        if (product.Name.toUpperCase().contains(edtsearch.getText().toString().toUpperCase()))
                            tempList.add(product);
                    }
                }

                adapterShopOwnerProduct productAdaper = new adapterShopOwnerProduct(shopownerproductdisplay.this, R.layout.itemshopownerproduct, tempList);
                productList.setAdapter(productAdaper);
            }
            @Override
            public void afterTextChanged(Editable s) {            }
        });

        Query query = database.getReference("Products");
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                products.clear();

                for (DataSnapshot postSnapshot: dataSnapshot.getChildren()) {
                    Product product = postSnapshot.getValue(Product.class);
                    product.setID(postSnapshot.getKey());
                    products.add(product);
                }
                adapterShopOwnerProduct productAdaper = new adapterShopOwnerProduct(shopownerproductdisplay.this, R.layout.itemshopownerproduct, products);
                productList.setAdapter(productAdaper);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });

        productList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id)
            {
                Intent editProductIntent = new Intent(shopownerproductdisplay.this, shopownerproductdetails.class);
                Bundle extras = new Bundle();
                String selected = ((TextView) view.findViewById(R.id.txt_shopowner_productID)).getText().toString();
                extras.putString("productID", ""+selected);
                extras.putString("action", "edit");
                editProductIntent.putExtras(extras);
                startActivity(editProductIntent);
            }
        });


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.shopownermenu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.menu_addProduct) {
            Intent emptyProductIntent = new Intent(shopownerproductdisplay.this, shopownerproductdetails.class);
            Bundle extras = new Bundle();
            //String selected = ((TextView) view.findViewById(R.id.txt_shopowner_productID)).getText().toString();
            extras.putString("productID", "");
            extras.putString("action", "add");
            emptyProductIntent.putExtras(extras);
            startActivity(emptyProductIntent);

        }
        return super.onOptionsItemSelected(item);
    }

}
