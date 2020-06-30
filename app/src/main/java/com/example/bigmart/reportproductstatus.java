package com.example.bigmart;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
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

public class reportproductstatus extends AppCompatActivity {

    String productStatus;
    private FirebaseDatabase database;
    private List<Product> products;
    ListView productList;
    EditText edtsearch;

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();


        if (id == android.R.id.home) {
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reportproductstatus);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        toolbar.setTitleTextColor(getColor(R.color.colorPrimaryDark));


        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_back);

        Bundle b = getIntent().getExtras();
        productStatus = b.getString("productStatus","o");

        if (productStatus.equals("OOS"))
            setTitle("Out Of Stock Products");
        else
            setTitle("All Products");

        products = new ArrayList<>();
        productList = findViewById(R.id.listReportProductStatus);

        database = FirebaseDatabase.getInstance("https://bigmart-sinprl.firebaseio.com/");
        DatabaseReference productReference = database.getReference("Products/");
        Query query = productReference.orderByKey();
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot postSnapshot: dataSnapshot.getChildren())
                {
                    Product product = postSnapshot.getValue(Product.class);
                    if (productStatus.equals("ALL"))
                        products.add(product);
                    else
                        if (product.Qty < product.MinStock)
                            products.add(product);

                }

                adapterShopOwnerProduct productAdaper = new adapterShopOwnerProduct(reportproductstatus.this, R.layout.itemshopownerproduct, products);
                productList.setAdapter(productAdaper);

            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {   }
        });

        edtsearch = findViewById(R.id.edt_report_product_search);
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

                adapterShopOwnerProduct productAdaper = new adapterShopOwnerProduct(reportproductstatus.this, R.layout.itemshopownerproduct, tempList);
                productList.setAdapter(productAdaper);
            }
            @Override
            public void afterTextChanged(Editable s) {           }
        });


        productList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id)
            {
                Intent editProductIntent = new Intent(reportproductstatus.this, shopownerproductdetails.class);
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
}