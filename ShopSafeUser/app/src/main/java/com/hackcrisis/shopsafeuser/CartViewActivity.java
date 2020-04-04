package com.hackcrisis.shopsafeuser;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.hackcrisis.shopsafeuser.Adapter.CartViewAdapter;
import com.hackcrisis.shopsafeuser.Data.CartItem;

import java.util.ArrayList;

public class CartViewActivity extends AppCompatActivity {

    private ArrayList<CartItem> cartItems = new ArrayList<>();
    private DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
    private float cartColories = 0;
    private float reqdCalories = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart_view);

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        databaseReference.child("familyDetails").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                reqdCalories = (dataSnapshot.child("adults").getValue(Integer.class)*2400)
                        + (dataSnapshot.child("children").getValue(Integer.class)*1600);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        RecyclerView recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        final CartViewAdapter adapter = new CartViewAdapter(this, new ArrayList<CartItem>());
        recyclerView.setAdapter(adapter);

        databaseReference.child("cartDetails").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    cartItems = new ArrayList<>();
                    cartColories = 0;
                    for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                        CartItem item = snapshot.getValue(CartItem.class);
                        cartItems.add(item);
                        cartColories = cartColories + (item.getQty() * item.getCalories() + item.getWeightPerPkt());
                    }
                    adapter.setCartItems(cartItems);
                    adapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        Button confirmBtn = findViewById(R.id.confirm_button);
        confirmBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(cartColories>reqdCalories){
                    Toast.makeText(CartViewActivity.this, "You have exceed the limit for items in your cart.", Toast.LENGTH_SHORT).show();
                    return;
                }
                startActivity(new Intent(CartViewActivity.this, ConfirmOrderActivity.class));
            }
        });
    }
}
