package com.hackcrisis.shopsafeuser;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.hackcrisis.shopsafeuser.Adapter.OrderListAdapter;
import com.hackcrisis.shopsafeuser.Data.CartItem;
import com.hackcrisis.shopsafeuser.Data.OrderDetails;

import java.util.ArrayList;

public class OrderViewActivity extends AppCompatActivity {

    private DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
    private ArrayList<CartItem> cart = new ArrayList<>();

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

        RecyclerView recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        final OrderListAdapter adapter = new OrderListAdapter(this, new ArrayList<CartItem>());
        recyclerView.setAdapter(adapter);

        databaseReference.child("orderDetails").child(orderId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                OrderDetails orderDetails = dataSnapshot.getValue(OrderDetails.class);
                totalAmountTv.setText("Total Amount Paid : Rs." + orderDetails.getTotalAmount());
                adapter.setItems(orderDetails.getCart());
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
