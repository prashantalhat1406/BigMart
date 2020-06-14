package com.example.bigmart;

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
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class shopownerproductdetails extends AppCompatActivity {

    FirebaseDatabase database;
    private List<Category> Categories;
    private List<SubCategory> SubCategories;
    private List<String> strCategories, strSubCategories;
    Spinner spncategory, spnsubcategory,spnType;
    Button butsave, butdelete;
    EditText edtName, edtName2, edtDisplayname, edtMRP, edtDiscount, edtGST, edtQTY, edtHSN, edtMaxStock, edtMinStock;
    String productID, action, productSubCategory;
    ArrayAdapter<String> categoryAdapter, subcategoryAdapter;
    ArrayAdapter<String> PTadapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shopownerproductddetails);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Bundle b = getIntent().getExtras();
        productID = b.getString("productID", "");
        action = b.getString("action", "add");

        Categories = new ArrayList<Category>();
        SubCategories = new ArrayList<SubCategory>();
        strCategories = new ArrayList<String>();
        strSubCategories = new ArrayList<String>();
        spncategory = findViewById(R.id.spn_product_category);
        spnsubcategory = findViewById(R.id.spn_product_subcategory);
        spnsubcategory.setEnabled(false);
        butsave = findViewById(R.id.but_productdetails_save);
        butdelete = findViewById(R.id.but_productdetails_delete);

        edtDiscount = findViewById(R.id.edt_productdetails_Discount);
        edtDisplayname = findViewById(R.id.edt_productdetails_DisplayName);
        edtGST = findViewById(R.id.edt_productdetails_GST);
        edtHSN = findViewById(R.id.edt_productdetails_HSN);
        edtMaxStock = findViewById(R.id.edt_productdetails_MaxStock);
        edtMinStock = findViewById(R.id.edt_productdetails_MinStock);
        edtMRP = findViewById(R.id.edt_productdetails_MRP);
        edtName = findViewById(R.id.edt_productdetails_Name);
        edtName2 = findViewById(R.id.edt_productdetails_Name2);
        spnType = findViewById(R.id.spn_productdetails_type);
        edtQTY = findViewById(R.id.edt_productdetails_QTY);

        database = FirebaseDatabase.getInstance("https://bigmart-sinprl.firebaseio.com/");

        Query categoryQuery = database.getReference("/Categories");
        categoryQuery.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot postSnapshot: dataSnapshot.getChildren()) {
                    Category category = postSnapshot.getValue(Category.class);
                    Categories.add(category);
                    strCategories.add(category.Name);
                }
                categoryAdapter = new ArrayAdapter<String>(shopownerproductdetails.this,R.layout.support_simple_spinner_dropdown_item,strCategories);
                spncategory.setAdapter(categoryAdapter);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {     }
        });

        final Query subcategoryQuery = database.getReference("/SubCategories");
        subcategoryQuery.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                    for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                        SubCategory subCategory = postSnapshot.getValue(SubCategory.class);
                        SubCategories.add(subCategory);
                        strSubCategories.add(subCategory.Name);
                    }
                    subcategoryAdapter = new ArrayAdapter<String>(shopownerproductdetails.this, R.layout.support_simple_spinner_dropdown_item, strSubCategories);
                    spnsubcategory.setAdapter(subcategoryAdapter);

            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {     }
        });

        spncategory.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    strSubCategories.clear();
                    for (SubCategory subCategory : SubCategories) {
                        if (subCategory.Category.toUpperCase().equals(strCategories.get(position).toString().toUpperCase())) {
                            strSubCategories.add(subCategory.Name);
                        }
                    }
                    subcategoryAdapter = new ArrayAdapter<String>(shopownerproductdetails.this, R.layout.support_simple_spinner_dropdown_item, strSubCategories);
                    spnsubcategory.setAdapter(subcategoryAdapter);
                    spnsubcategory.setEnabled(true);
                    spnsubcategory.setSelection(subcategoryAdapter.getPosition(productSubCategory));
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {             }
        });

        String[] productTypeArray = getResources().getStringArray(R.array.producttype);
        PTadapter =  new ArrayAdapter<String>(this, R.layout.support_simple_spinner_dropdown_item, productTypeArray);
        spnType.setAdapter(PTadapter);

        butsave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {

                Product product = new Product();
                product.setName("" + edtName.getText().toString());
                product.setName2("" + edtName2.getText().toString());
                product.setQtyNos(0);
                product.setQty(Integer.parseInt(edtQTY.getText().toString()));
                product.setMinStock(Integer.parseInt(edtMinStock.getText().toString()));
                product.setMaxStock(Integer.parseInt(edtMaxStock.getText().toString()));
                product.setMRP(Double.parseDouble(edtMRP.getText().toString()));
                product.setDisplayName("" + edtDisplayname.getText().toString());
                product.setDiscount(Integer.parseInt(edtDiscount.getText().toString()));
                product.setCategory(spncategory.getSelectedItem().toString());
                product.setSubCategory(spnsubcategory.getSelectedItem().toString());
                product.setHSN(Integer.parseInt(edtHSN.getText().toString()));
                //product.setType(edtType.getText().toString());
                product.setType(spnType.getSelectedItem().toString());
                product.setGST(Integer.parseInt(edtGST.getText().toString()));


                if (action.equals("edit"))
                {
                    DatabaseReference databaseReference = database.getReference("Products/");
                    databaseReference.child(""+productID).setValue(product);
                    Toast t = Toast.makeText(shopownerproductdetails.this, "Product Details Edited", Toast.LENGTH_SHORT);
                    t.setGravity(Gravity.TOP, 0, 0);
                    t.show();
                }else {
                    DatabaseReference databaseReference = database.getReference("Products/");
                    databaseReference.push().setValue(product);
                    Toast t = Toast.makeText(shopownerproductdetails.this, "New Product Added", Toast.LENGTH_SHORT);
                    t.setGravity(Gravity.TOP, 0, 0);
                    t.show();
                }


                finish();
            }
        });



        if (action.equals("edit"))
        {

            DatabaseReference databaseReference = database.getReference("Products/" + productID);
            databaseReference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    Product product = dataSnapshot.getValue(Product.class);
                    edtName.setText(product.Name);
                    edtName2.setText(product.Name2);
                    edtDisplayname.setText(product.DisplayName);
                    edtDiscount.setText("" + product.Discount);
                    edtMaxStock.setText("" + product.MaxStock);
                    edtMinStock.setText("" + product.MinStock);
                    edtMRP.setText("" + product.MRP);
                    edtHSN.setText("" + product.HSN);
                    edtGST.setText("" + product.GST);
                    edtQTY.setText("" + product.Qty);
                    spnType.setSelection(PTadapter.getPosition(product.Type));


                    productSubCategory = product.SubCategory;
                    categoryAdapter = new ArrayAdapter<String>(shopownerproductdetails.this,R.layout.support_simple_spinner_dropdown_item,strCategories);
                    spncategory.setSelection(categoryAdapter.getPosition(product.Category));
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {                }
            });

        }

    }

}
