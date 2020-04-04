package com.hackcrisis.shopsaferetailer;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.hackcrisis.shopsaferetailer.Adapters.OrderListAdapter;
import com.hackcrisis.shopsaferetailer.Data.CartItem;
import com.hackcrisis.shopsaferetailer.Data.OrderDetails;

import java.util.ArrayList;

public class OrderViewActivity extends AppCompatActivity {

    private DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
    private ArrayList<CartItem> cart = new ArrayList<>();
    OrderDetails orderDetails;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_view);

        final String orderId = getIntent().getStringExtra("orderId");

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        final TextView addressTv = findViewById(R.id.address_tv);
        final TextView totalAmountTv = findViewById(R.id.total_amount_tv);
        final Button confirmBtn = findViewById(R.id.confirm_btn);

        RecyclerView recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        final OrderListAdapter adapter = new OrderListAdapter(this, new ArrayList<CartItem>());
        recyclerView.setAdapter(adapter);

        databaseReference.child("orderDetails").child(orderId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                orderDetails = dataSnapshot.getValue(OrderDetails.class);
                totalAmountTv.setText("Total Amount Paid : Rs." + orderDetails.getTotalAmount());
                adapter.setItems(orderDetails.getCart());
                adapter.notifyDataSetChanged();

                if(orderDetails.getStatus().equals("Placed")){
                    confirmBtn.setText("Confirm Order");
                }
                else if(orderDetails.getStatus().equals("Order Confirmed")){
                    confirmBtn.setText("Order Ready");
                }
                else if(orderDetails.getStatus().equals("Order Ready")){
                    confirmBtn.setText("Delivered");
                }
                else {
                    confirmBtn.setVisibility(View.GONE);
                }

                databaseReference.child("users").child(orderDetails.getUserId()).child("address").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        addressTv.setText(dataSnapshot.getValue(String.class));
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        confirmBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(orderDetails.getStatus().equals("Placed")){
                    databaseReference.child("orderDetails").child(orderId).child("status").setValue("Order Confirmed");
                }
                else if(orderDetails.getStatus().equals("Order Confirmed")){
                    databaseReference.child("orderDetails").child(orderId).child("status").setValue("Order Ready");
                }
                else if(orderDetails.getStatus().equals("Order Ready")){
                    databaseReference.child("orderDetails").child(orderId).child("status").setValue("Delivered");
                }
            }
        });
    }
}
