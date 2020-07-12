package com.asm.bigmart;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
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
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.text.DecimalFormat;
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
    private String searchItem="";
    private Integer position=0;
    Product product;


    public void showErrorMessage(String message){
        Toast error = Toast.makeText(shopownerproductdetails.this, message,Toast.LENGTH_SHORT);
        error.setGravity(Gravity.TOP, 0, 0);
        error.show();
    }

    public boolean isFieldEmpty(EditText field){
        boolean flag = false;

        if (field.getText().toString().trim().length() == 0)
        {
            field.setError(field.getHint() + " should not be empty");
            flag = false;
        }else
            flag = true;

        return flag;
    }

    public boolean fieldValidated(){
        boolean flag = true;

        if (edtName.getText().toString().trim().length() == 0)
        {
            flag = false;
            edtName.setError("Name should not be empty");

        }else
            if (edtMRP.getText().toString().trim().length() == 0)
            {
                flag = false;
                edtMRP.setError("MRP should not be empty");
            }else
                if (edtDiscount.getText().toString().length() == 0)
                {
                    flag = false;
                    edtDiscount.setError("Discount should not be empty");
                }else
                    if (edtGST.getText().toString().length() == 0)
                    {
                        flag = false;
                        edtGST.setError("GST should not be empty");
                    }else
                        if (edtQTY.getText().toString().length() == 0)
                        {
                            flag = false;
                            edtQTY.setError("Quantity should not be empty");
                        }else
                            if (edtHSN.getText().toString().length() == 0)
                            {
                                flag = false;
                                edtHSN.setError("HSN should not be empty");
                            }else
                                if (edtMaxStock.getText().toString().length() == 0)
                                {
                                    flag = false;
                                    edtMaxStock.setError("Max Stock should not be empty");
                                }else
                                    if (edtMinStock.getText().toString().length() == 0)
                                    {
                                        flag = false;
                                        edtMinStock.setError("Minimum Stock should not be empty");
                                    }else
                                        if (Integer.parseInt(edtQTY.getText().toString()) < Integer.parseInt(edtMinStock.getText().toString()) )
                                        {
                                            flag = false;
                                            edtQTY.setError("Quantity should not be less than Minimum Stock");
                                        }else
                                            if (Integer.parseInt(edtDiscount.getText().toString()) > 25 )
                                            {
                                                flag = false;
                                                edtDiscount.setError("Discount can not be greater than 25%");
                                            }else
                                                if (Integer.parseInt(edtGST.getText().toString()) > 30 )
                                                {
                                                    flag = false;
                                                    edtGST.setError("GST can not be greater than 30%");
                                                }else
                                                    if (spncategory.getSelectedItemPosition() == 0 )
                                                    {
                                                        flag = false;
                                                        showErrorMessage("Please select correct Category");
                                                    }

        return flag;
    }


    @Override
    public void onBackPressed() {

        if(butsave.getText().toString().toLowerCase().equals("edit")){
            Intent intent=new Intent();
            intent.putExtra("searchItem",searchItem);
            intent.putExtra("position",position);
            setResult(Activity.RESULT_OK, intent);
            super.onBackPressed();
        }else{
            AlertDialog.Builder builder1 = new AlertDialog.Builder(shopownerproductdetails.this);
            builder1.setMessage("Please Save details.");
            builder1.setCancelable(false);
            builder1.setPositiveButton(
                    "Ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    });
            final AlertDialog alert11 = builder1.create();
            alert11.setOnShowListener(new DialogInterface.OnShowListener() {
                @Override
                public void onShow(DialogInterface dialog) {
                    alert11.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.GREEN);
                }
            });
            alert11.show();
        }


        /**/
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shopownerproductddetails);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_back);

        toolbar.setTitleTextColor(getColor(R.color.colorPrimaryDark));

        Bundle b = getIntent().getExtras();
        productID = b.getString("productID", "");
        action = b.getString("action", "add");
        position = b.getInt("position",0);
        searchItem = b.getString("searchItem", "");




        Categories = new ArrayList<>();
        SubCategories = new ArrayList<>();
        strCategories = new ArrayList<>();
        strSubCategories = new ArrayList<>();
        spncategory = findViewById(R.id.spn_product_category);
        spnsubcategory = findViewById(R.id.spn_product_subcategory);
        spnsubcategory.setEnabled(false);
        butsave = findViewById(R.id.but_productdetails_save);
        //butdelete = findViewById(R.id.but_productdetails_delete);

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

        edtName.setEnabled(false);
        edtName2.setEnabled(false);
        edtDisplayname.setEnabled(false);
        edtDiscount.setEnabled(false);
        edtMaxStock.setEnabled(false);
        edtMinStock.setEnabled(false);
        edtMRP.setEnabled(false);
        edtHSN.setEnabled(false);
        edtGST.setEnabled(false);
        edtQTY.setEnabled(false);
        spnType.setEnabled(false);
        spncategory.setEnabled(false);
        spnsubcategory.setEnabled(false);

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
                strCategories.add(0,"Select Category");
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
                    //strSubCategories.add(0,"Select SubCategory");
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
                    subcategoryAdapter = new ArrayAdapter<>(shopownerproductdetails.this, R.layout.support_simple_spinner_dropdown_item, strSubCategories);
                    spnsubcategory.setAdapter(subcategoryAdapter);
                    if (butsave.getText().toString().equals("Edit"))
                        spnsubcategory.setEnabled(false);
                    else
                        spnsubcategory.setEnabled(true);
                    spnsubcategory.setSelection(subcategoryAdapter.getPosition(productSubCategory));
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {             }
        });

        String[] productTypeArray = getResources().getStringArray(R.array.producttype);
        PTadapter =  new ArrayAdapter<String>(this, R.layout.support_simple_spinner_dropdown_item, productTypeArray);
        spnType.setAdapter(PTadapter);

        butsave.setText("Edit");



        butsave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (butsave.getText().toString().equals("Edit")) {
                    butsave.setText("Save Changes");

                    edtName.setEnabled(true);
                    edtName2.setEnabled(true);
                    edtDisplayname.setEnabled(true);
                    edtDiscount.setEnabled(true);
                    edtMaxStock.setEnabled(true);
                    edtMinStock.setEnabled(true);
                    edtMRP.setEnabled(true);
                    edtHSN.setEnabled(true);
                    edtGST.setEnabled(true);
                    edtQTY.setEnabled(true);
                    spnType.setEnabled(true);
                    spncategory.setEnabled(true);
                    spnsubcategory.setEnabled(true);

                }
                else{

                    final Dialog dialog = new Dialog(shopownerproductdetails.this);
                    dialog.setContentView(R.layout.logoutdialog);
                    dialog.setCancelable(false);
                    dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

                    TextView dialogTitle = dialog.findViewById(R.id.dialog_title);
                    dialogTitle.setText("SAVE PRODUCT");

                    TextView dialogMessage = dialog.findViewById(R.id.dialog_message);
                    dialogMessage.setText("Are you sure to Save Product Details ?");

                    Button yes = dialog.findViewById(R.id.dialog_btn_yes);
                    yes.setText("Save");
                    yes.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (fieldValidated()) {
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
                                product.setType(spnType.getSelectedItem().toString());
                                product.setGST(Integer.parseInt(edtGST.getText().toString()));


                                if (action.equals("edit")) {
                                    DatabaseReference databaseReference = database.getReference("Products/");
                                    databaseReference.child("" + productID).setValue(product);
                                    Toast t = Toast.makeText(shopownerproductdetails.this, "Product Details Edited", Toast.LENGTH_SHORT);
                                    t.setGravity(Gravity.TOP, 0, 0);
                                    t.show();
                                } else {
                                    DatabaseReference databaseReference = database.getReference("Products/");
                                    databaseReference.push().setValue(product);
                                    Toast t = Toast.makeText(shopownerproductdetails.this, "New Product Added", Toast.LENGTH_SHORT);
                                    t.setGravity(Gravity.TOP, 0, 0);
                                    t.show();
                                }


                                Intent intent = new Intent();
                                intent.putExtra("searchItem", searchItem);
                                intent.putExtra("position", position);
                                setResult(Activity.RESULT_OK, intent);
                                finish();
                            }else{
                                dialog.dismiss();
                            }
                        }
                    });

                    Button no = dialog.findViewById(R.id.dialog_btn_no);
                    no.setText("Cancel");
                    no.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            butsave.setText("Edit");
                            dialog.dismiss();
                            onBackPressed();
                        }
                    });
                    dialog.show();



                /*AlertDialog.Builder logoutAlertBuilder = new AlertDialog.Builder(shopownerproductdetails.this);
                logoutAlertBuilder.setMessage("Are you sure to Save Product Details ?");
                logoutAlertBuilder.setCancelable(false);
                logoutAlertBuilder.setPositiveButton(
                        "YES", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                if (fieldValidated()) {
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
                                    product.setType(spnType.getSelectedItem().toString());
                                    product.setGST(Integer.parseInt(edtGST.getText().toString()));


                                    if (action.equals("edit")) {
                                        DatabaseReference databaseReference = database.getReference("Products/");
                                        databaseReference.child("" + productID).setValue(product);
                                        Toast t = Toast.makeText(shopownerproductdetails.this, "Product Details Edited", Toast.LENGTH_SHORT);
                                        t.setGravity(Gravity.TOP, 0, 0);
                                        t.show();
                                    } else {
                                        DatabaseReference databaseReference = database.getReference("Products/");
                                        databaseReference.push().setValue(product);
                                        Toast t = Toast.makeText(shopownerproductdetails.this, "New Product Added", Toast.LENGTH_SHORT);
                                        t.setGravity(Gravity.TOP, 0, 0);
                                        t.show();
                                    }


                                    Intent intent = new Intent();
                                    intent.putExtra("searchItem", searchItem);
                                    intent.putExtra("position", position);
                                    setResult(Activity.RESULT_OK, intent);
                                    finish();
                                }
                            }
                        });
                logoutAlertBuilder.setNegativeButton(
                        "NO", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                //dialog.cancel();
                                butsave.setText("Edit");
                                onBackPressed();
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

                alertLogout.show();*/
            }

                /**/
            }
        });

        if  (action.equals("add")){
            butsave.setText("Save Changes");

            edtName.setEnabled(true);
            edtName2.setEnabled(true);
            edtDisplayname.setEnabled(true);
            edtDiscount.setEnabled(true);
            edtMaxStock.setEnabled(true);
            edtMinStock.setEnabled(true);
            edtMRP.setEnabled(true);
            edtHSN.setEnabled(true);
            edtGST.setEnabled(true);
            edtQTY.setEnabled(true);
            spnType.setEnabled(true);
            spncategory.setEnabled(true);
            spnsubcategory.setEnabled(true);
        }

        if (action.equals("edit"))
        {

            DatabaseReference databaseReference = database.getReference("Products/" + productID);
            databaseReference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    product = dataSnapshot.getValue(Product.class);
                    edtName.setText(product.Name);
                    edtName2.setText(product.Name2);
                    edtDisplayname.setText(product.DisplayName);

                    edtMaxStock.setText("" + product.MaxStock);
                    edtMinStock.setText("" + product.MinStock);

                    DecimalFormat formater = new DecimalFormat("0.00");
                    edtMRP.setText("" + formater.format( product.MRP));
                    //edtDiscount.setText("" + (product.Discount));
                    //edtMRP.setText("" + ( product.MRP));
                    edtDiscount.setText("" + (product.Discount));
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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            //onBackPressed();
            Intent intent=new Intent();
            intent.putExtra("searchItem",searchItem);
            intent.putExtra("position",position);
            setResult(Activity.RESULT_OK, intent);
            super.onBackPressed();
        }

        return super.onOptionsItemSelected(item);
    }
}
