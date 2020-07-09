package com.asm.bigmart;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;

import com.asm.bigmart.adapters.SO_OrderDetails;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class shopownerorderdetails extends AppCompatActivity {

    private String orderID;
    private Integer position;
    private List<Product> products;
    private List<Product> databaseProducts;
    ListView productList;
    Orders orderDetail;
    Button butComplete, butConfirm, butCancel,butPrint;
    User user;
    FirebaseDatabase database;
    private String searchItem="";

    public void updateStoreQuantity(List<Product> products){

        for (Product product : products) {
            for (Product databaseProduct : databaseProducts) {
                if (product.ID.equals(databaseProduct.ID))
                {
                    DatabaseReference databaseReference = database.getReference("Products/" + product.ID);
                    Product updatedProduct = new Product();
                    updatedProduct = databaseProduct;
                    updatedProduct.setQty(databaseProduct.Qty - product.QtyNos);
                    databaseReference.setValue(updatedProduct) ;
                }
            }
        }
    }

   /* @Override
    public void onBackPressed() {
        Intent intent=new Intent();
        intent.putExtra("searchItem",searchItem);
        setResult(Activity.RESULT_OK, intent);
        super.onBackPressed();
    }*/

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_orderdetaildisplay);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitleTextColor(getColor(R.color.colorPrimaryDark));

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_back);

        database = FirebaseDatabase.getInstance("https://bigmart-sinprl.firebaseio.com/");

        Bundle b = getIntent().getExtras();
        orderID = b.getString("orderID","12");
        position = b.getInt("position",0);
        searchItem = b.getString("searchItem", "");


        productList = findViewById(R.id.listOrderDetail);
        products = new ArrayList<Product>();
        databaseProducts = new ArrayList<Product>();

        orderDetail = new Orders();
        user = new User();


        butPrint = findViewById(R.id.but_orderdetail_print);

        butComplete = findViewById(R.id.but_orderdetail_complete);
        butComplete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AlertDialog.Builder logoutAlertBuilder = new AlertDialog.Builder(shopownerorderdetails.this);
                logoutAlertBuilder.setMessage("Do you want to Complete Order ?");
                logoutAlertBuilder.setCancelable(false);
                logoutAlertBuilder.setPositiveButton(
                        "YES", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                DatabaseReference orderReference = database.getReference("Orders/").child(""+orderID);
                                Map<String, Object> statusUpdate = new HashMap<>();
                                statusUpdate.put("status", "Complete");
                                orderReference.updateChildren(statusUpdate);
                                Intent intent=new Intent();
                                intent.putExtra("position",position);
                                intent.putExtra("searchItem",searchItem);
                                setResult(Activity.RESULT_OK, intent);
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

                alertLogout.show();


            }
        });

        butConfirm = findViewById(R.id.but_orderdetail_confirm);
        butConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean oosFlag = false;

                for (Product product : products) {
                    for (Product databaseProduct : databaseProducts) {
                        if (databaseProduct.ID.equals(product.ID)) {
                            if (databaseProduct.Qty < databaseProduct.MinStock) {
                                oosFlag = true;
                                break;
                            }
                        }
                    }
                }

                if (oosFlag){
                    AlertDialog.Builder builder1 = new AlertDialog.Builder(shopownerorderdetails.this);
                    builder1.setMessage("Order has some Out Of Stock Products. Please remove.");
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
                            alert11.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.BLACK);
                        }
                    });
                    alert11.show();

                }else {
                    AlertDialog.Builder logoutAlertBuilder = new AlertDialog.Builder(shopownerorderdetails.this);
                    logoutAlertBuilder.setMessage("Do you want to Confirm Order ?");
                    logoutAlertBuilder.setCancelable(false);
                    logoutAlertBuilder.setPositiveButton(
                            "YES", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    DatabaseReference orderReference = database.getReference("Orders/").child(""+orderID);
                                    Map<String, Object> statusUpdate = new HashMap<>();
                                    statusUpdate.put("status", "InProgress");
                                    orderReference.updateChildren(statusUpdate);
                                    butComplete.setVisibility(View.VISIBLE);
                                    butPrint.setVisibility(View.VISIBLE);
                                    butConfirm.setVisibility(View.GONE);
                                    butCancel.setVisibility(View.GONE);
                                    updateStoreQuantity(products);
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

                    alertLogout.show();
                }

            }
        });

        butCancel = findViewById(R.id.but_orderdetail_cancel);
        butCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder logoutAlertBuilder = new AlertDialog.Builder(shopownerorderdetails.this);
                logoutAlertBuilder.setMessage("Do you want to Cancel Order ?");
                logoutAlertBuilder.setCancelable(false);
                logoutAlertBuilder.setPositiveButton(
                        "YES", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                DatabaseReference orderReference = database.getReference("Orders/").child(""+orderID);
                                Map<String, Object> statusUpdate = new HashMap<>();
                                statusUpdate.put("status", "Cancelled");
                                orderReference.updateChildren(statusUpdate);
                                Intent intent=new Intent();
                                intent.putExtra("position",position);
                                intent.putExtra("searchItem",searchItem);
                                setResult(Activity.RESULT_OK, intent);
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

                alertLogout.show();

            }
        });


        productList.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
                if(orderDetail.status.equals("Created")) {
                    AlertDialog.Builder logoutAlertBuilder = new AlertDialog.Builder(shopownerorderdetails.this);
                    logoutAlertBuilder.setMessage("Do you want to delete Product ?");
                    logoutAlertBuilder.setCancelable(false);
                    logoutAlertBuilder.setPositiveButton(
                            "YES", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                    if (products.size() == 1){
                                        butCancel.callOnClick();
                                    }else {
                                        Product product = products.get(position);

                                        DatabaseReference databaseReference = database.getReference("Orders/" + orderID + "/Products/" + products.get(position).ID);
                                        databaseReference.removeValue();

                                        DatabaseReference orderReference = database.getReference("Orders/" + orderID);
                                        orderReference.child("amount").setValue(orderDetail.amount - (product.QtyNos * (product.MRP - product.Discount)));

                                        products.remove(position);

                                        if (products.size() > 0) {
                                            //adapterShopownerOrderDetails productAdaper = new adapterShopownerOrderDetails(shopownerorderdetails.this, R.layout.itemorderdetails, products, databaseProducts, orderDetail.status);
                                            SO_OrderDetails productAdaper = new SO_OrderDetails(shopownerorderdetails.this, R.layout.itemorderdetails, products, databaseProducts, orderDetail.status);
                                            productList.setAdapter(productAdaper);
                                        } else {
                                            orderReference.removeValue();
                                        }
                                    }

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

                    alertLogout.show();
                }
                return false;
            }
        });

        Query queryOrder = database.getReference("/Orders/"+orderID);
        queryOrder.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                orderDetail = dataSnapshot.getValue(Orders.class);
                TextView orderDate = findViewById(R.id.txt_orderdetails_orderDate);
                orderDate.setText(""+orderDetail.date);
                switch (orderDetail.status)
                {
                    case "Complete":
                        butComplete.setVisibility(View.INVISIBLE);
                        butPrint.setVisibility(View.VISIBLE);
                        butConfirm.setVisibility(View.GONE);
                        butCancel.setVisibility(View.GONE);
                        butComplete.setEnabled(false);
                        butPrint.setEnabled(true);
                        break;
                    case "InProgress":
                        butComplete.setVisibility(View.VISIBLE);
                        butPrint.setVisibility(View.VISIBLE);
                        butConfirm.setVisibility(View.GONE);
                        butCancel.setVisibility(View.GONE);
                        butComplete.setEnabled(true);
                        butPrint.setEnabled(true);
                        break;
                    case "Created":
                        butComplete.setVisibility(View.GONE);
                        butPrint.setVisibility(View.GONE);
                        butConfirm.setVisibility(View.VISIBLE);
                        butCancel.setVisibility(View.VISIBLE);
                        butCancel.setEnabled(true);
                        butConfirm.setEnabled(true);
                        break;
                    case "Cancelled":
                        butComplete.setVisibility(View.GONE);
                        butPrint.setVisibility(View.GONE);
                        butConfirm.setVisibility(View.GONE);
                        butCancel.setVisibility(View.GONE);
                        butCancel.setEnabled(false);
                        butConfirm.setEnabled(false);
                        break;

                }
                /*if(orderDetail.status.equals("Complete"))
                    butComplete.setEnabled(false);
                else
                    butComplete.setEnabled(true);*/
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });



        Query databasequery = database.getReference("Products/");
        databasequery.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot postSnapshot: dataSnapshot.getChildren()) {
                    Product product = postSnapshot.getValue(Product.class);
                    product.setID(postSnapshot.getKey());
                    databaseProducts.add(product);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {            }
        });

        Query query = database.getReference("Orders/"+orderID+"/Products/");
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                products.clear();

                for (DataSnapshot postSnapshot: dataSnapshot.getChildren()) {
                    Product product = postSnapshot.getValue(Product.class);
                    //product.setID(product);
                    products.add(product);
                }
                //adapterShopownerOrderDetails productAdaper = new adapterShopownerOrderDetails(shopownerorderdetails.this, R.layout.itemorderdetails, products,databaseProducts, orderDetail.status);
                SO_OrderDetails productAdaper = new SO_OrderDetails(shopownerorderdetails.this, R.layout.itemorderdetails, products,databaseProducts, orderDetail.status);
                productList.setAdapter(productAdaper);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });


        TextView txtorderID = findViewById(R.id.txt_orderdetails_orderID);
        //txtorderID.setText(""+orderID.substring(orderID.length() - 5).toUpperCase());
        txtorderID.setText(""+orderID);





        final TextView name = findViewById(R.id.txt_orderdetails_customerName);
        final TextView mobile = findViewById(R.id.txt_orderdetails_customerMobile);
        final TextView address = findViewById(R.id.txt_orderdetails_customeraddress);

        Query queryUser = database.getReference("/Users" );
        queryUser.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot postSnapshot: dataSnapshot.getChildren()) {
                    User u = postSnapshot.getValue(User.class);
                    if(u.Mobile.equals( orderDetail.userID))
                    {
                        try {
                            name.setText("" + CryptUtil.decrypt(u.Name));
                            mobile.setText("" + u.Mobile);
                            address.setText("" + CryptUtil.decrypt(u.Address1) + " , " + CryptUtil.decrypt(u.Address2));
                            break;
                        }catch (Exception e) {}
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {            }
        });



    }

    @Override
    public void onBackPressed() {
        Intent intent=new Intent();
        intent.putExtra("position",position);
        intent.putExtra("searchItem",searchItem);
        setResult(Activity.RESULT_OK, intent);
        finish();
        super.onBackPressed();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }

}