package com.example.bigmart;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

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
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
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
    private String searchItem="";
    private Integer showpassword = 0;

    @Override
    protected void onResume() {
        //edtsearch.setText("");
        super.onResume();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shopownerproductdisplay);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        toolbar.setTitleTextColor(getColor(R.color.colorPrimaryDark));

        database = FirebaseDatabase.getInstance("https://bigmart-sinprl.firebaseio.com/");


        productList = findViewById(R.id.listShopOwnerProduct);
        products = new ArrayList<Product>();
        edtsearch = findViewById(R.id.edt_showowner_product_search);
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
                    //getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
                    //return true;
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
                List<Product> tempList = new ArrayList<Product>();
                if (edtsearch.getText().length() == 0)
                    tempList = products;
                else {
                    tempList.clear();
                    for (Product product : products) {
                        if (product.Name.toUpperCase().contains(edtsearch.getText().toString().trim().toUpperCase()))
                            tempList.add(product);
                    }
                }

                adapterShopOwnerProduct productAdaper = new adapterShopOwnerProduct(shopownerproductdisplay.this, R.layout.itemshopownerproduct, tempList);
                productList.setAdapter(productAdaper);
            }
            @Override
            public void afterTextChanged(Editable s) {           }
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
                extras.putInt("position", position);
                extras.putString("searchItem", edtsearch.getText().toString());
                editProductIntent.putExtras(extras);
                startActivityForResult(editProductIntent,100);
            }
        });


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 100)
        {
            searchItem = data.getStringExtra("searchItem");
            edtsearch.setText(""+searchItem);
            if(searchItem.length() == 0)
                edtsearch.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
            Integer pos = data.getIntExtra("position",0);
            productList.setSelection(pos);
        }
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
