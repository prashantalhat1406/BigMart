package com.example.bigmart;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
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
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class customercartdisplay extends AppCompatActivity {
    public Integer listPosition = 0;
    private List<Product> products;
    ListView productList;
    private long userID;
    private int count = 0;
    private String orderID = "test";
    private Boolean flag = false;
    private Double TotalPrice = 0.0;
    FirebaseDatabase database;

    public void goToHome(){
        Intent homeIntent = new Intent(customercartdisplay.this, home.class);
        Bundle extras = new Bundle();
        extras.putLong("userID", userID);
        homeIntent.putExtras(extras);
        startActivity(homeIntent);
        finish();
    }

    @Override
    public void onBackPressed() {
        goToHome();
        //super.onBackPressed();
    }

    public BroadcastReceiver positionMes = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            listPosition = intent.getIntExtra("position",0);
            //Toast.makeText(customercartdisplay.this, "" + productList.getLastVisiblePosition(),Toast.LENGTH_SHORT).show();
        }
    };



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cartdisplay);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        LocalBroadcastManager.getInstance(this).registerReceiver(positionMes,new IntentFilter("message_subject_intent"));


                Bundle b = getIntent().getExtras();
        userID = b.getLong("userID");

        products = new ArrayList<Product>();
        productList = findViewById(R.id.listCart);
        database = FirebaseDatabase.getInstance("https://bigmart-sinprl.firebaseio.com/");

        final Button butConfirm = findViewById(R.id.but_cart_confirm);
        butConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                flag = true;
                Intent intent = new Intent(customercartdisplay.this,customerdeliverypayment.class);
                startActivityForResult(intent, 100);
            }
        });

        //Button butContinueShopping = findViewById(R.id.but_cart_continueshopping);
        TextView butContinueShopping = findViewById(R.id.txt_cart_continue);
        butContinueShopping.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToHome();
            }
        });

        //final Button butRemoveAll = findViewById(R.id.but_cart_removeall);
        final TextView butRemoveAll = findViewById(R.id.txt_cart_removeall);
        butRemoveAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AlertDialog.Builder removeAllAlertBuilder = new AlertDialog.Builder(customercartdisplay.this);
                removeAllAlertBuilder.setMessage("Are you sure to Remove All items ?");
                removeAllAlertBuilder.setCancelable(false);
                removeAllAlertBuilder.setPositiveButton(
                        "YES", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                DatabaseReference databaseReference = database.getReference("Users/"+userID+"/TempOrder");
                                databaseReference.removeValue();
                                goToHome();
                            }
                        });
                removeAllAlertBuilder.setNegativeButton(
                        "NO", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        });
                AlertDialog alertRemoveall = removeAllAlertBuilder.create();
                alertRemoveall.show();



            }
        });

        Query query = database.getReference("Users/"+userID+"/TempOrder");
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                //Toast.makeText(customercartdisplay.this, "" + productList.getLastVisiblePosition(),Toast.LENGTH_SHORT).show();
                //Integer position =  productList.getSelectedItemPosition();
                products.clear();
                TotalPrice = 0.0;
                Double TotalSavings = 0.0;
                int count = 0;
                for (DataSnapshot postSnapshot: dataSnapshot.getChildren()) {
                    Product product = postSnapshot.getValue(Product.class);
                    product.setID(postSnapshot.getKey());
                    products.add(product);
                    TotalPrice = TotalPrice + (product.QtyNos * (product.MRP -  product.Discount.doubleValue()));
                    TotalSavings = TotalSavings + (product.QtyNos * product.Discount.doubleValue() );
                    count = count + product.QtyNos;
                }

                TextView totalPrice = findViewById(R.id.txt_cartdisplay_totalamount);
                DecimalFormat formater = new DecimalFormat("0.00");
                totalPrice.setText("Total : "+ customercartdisplay.this.getResources().getString(R.string.Rupee) + " " +formater.format(TotalPrice));

                if(products.size() == 0)
                {
                    butConfirm.setEnabled(false);
                    butRemoveAll.setEnabled(false);
                    butRemoveAll.setVisibility(View.INVISIBLE);
                }else {
                    butConfirm.setEnabled(true);
                    butRemoveAll.setEnabled(true);
                    butRemoveAll.setVisibility(View.VISIBLE);
                }

                    adapterProduct productAdaper = new adapterProduct(customercartdisplay.this, R.layout.itemproduct, products, userID, 2);
                    productList.setAdapter(productAdaper);
                    productList.setSelection(listPosition);

                    TextView totSave = findViewById(R.id.txt_cartdisplay_totalsavings);
                    totSave.setText("Savings : " + customercartdisplay.this.getResources().getString(R.string.Rupee) +" "+formater.format(TotalSavings));
                    if (count == 0)
                        goToHome();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 100)
        {
            if (data.getStringExtra("type").length() != 0) {
                DatabaseReference productReference = database.getReference("Users/" + userID).child("/TempOrder");
                Query query = productReference.orderByKey();
                query.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (flag) {
                            List<Product> products = new ArrayList<Product>();
                            for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                                products.add(postSnapshot.getValue(Product.class));
                            }
                            DatabaseReference orderReference = database.getReference("Orders/");
                            orderID = orderReference.push().getKey();
                            Orders orders = new Orders();
                            orders.setID(orderID);
                            orders.setUserID(userID);
                            orders.setAmount(TotalPrice);

                            Date c = Calendar.getInstance().getTime();
                            SimpleDateFormat df = new SimpleDateFormat("dd-MMM-yyyy");
                            String formattedDate = df.format(c);

                            orders.setDate(formattedDate);
                            orders.setDeliveryType(data.getStringExtra("type"));
                            orders.setPaymentMode("Card");
                            orders.setStatus("Created");
                            orderReference.child("" + orderID).setValue(orders);

                            for (Product product : products) {
                                DatabaseReference productReference = database.getReference("Orders/" + orderID + "/Products");
                                productReference.push().setValue(product);
                            }

                            DatabaseReference databaseReference = database.getReference("Users/" + userID + "/TempOrder");
                            databaseReference.removeValue();
                            flag = false;
                            Toast error = Toast.makeText(customercartdisplay.this, "Thank you. Order placed", Toast.LENGTH_SHORT);
                            error.setGravity(Gravity.TOP, 0, 0);
                            error.show();
                            goToHome();
                            finish();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                    }
                });
            }
        }
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

    public void showLogoutAlertDialog(){

        AlertDialog.Builder logoutAlertBuilder = new AlertDialog.Builder(customercartdisplay.this);
        logoutAlertBuilder.setMessage("Are you sure to Logout ?");
        logoutAlertBuilder.setCancelable(false);
        logoutAlertBuilder.setPositiveButton(
                "YES", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent logoutIntent = new Intent(customercartdisplay.this, login.class);
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
                        Intent viewCartIntent = new Intent(customercartdisplay.this, customercartdisplay.class);
                        Bundle extras = new Bundle();
                        extras.putLong("userID", userID);
                        viewCartIntent.putExtras(extras);
                        startActivity(viewCartIntent);
                        finish();
                    }
                    else
                    {
                        Toast error = Toast.makeText(customercartdisplay.this, "No Items In Cart !",Toast.LENGTH_SHORT);
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
            Intent viewCartIntent = new Intent(customercartdisplay.this, customerorderhistory.class);
            Bundle extras = new Bundle();
            extras.putLong("userID", userID);
            viewCartIntent.putExtras(extras);
            startActivity(viewCartIntent);
            //finish();
            //return true;
        }

        if (id == R.id.menu_profile) {
            Intent profileIntent = new Intent(customercartdisplay.this, customerprofile.class);
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
}
