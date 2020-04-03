package com.hackcrisis.shopsaferetailer;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.hackcrisis.shopsaferetailer.Adapters.InventoryAdapter;

import java.util.ArrayList;

public class InventoryActivity extends AppCompatActivity {

    private ArrayList<String> productIds = new ArrayList<>();
    private DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inventory);

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        RecyclerView recyclerView = findViewById(R.id.feed_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        final InventoryAdapter adapter = new InventoryAdapter(this, new ArrayList<String>());
        recyclerView.setAdapter(adapter);

        databaseReference.child("inventory").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    productIds = new ArrayList<>();
                    for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                        String id = snapshot.getKey();
                        productIds.add(id);
                    }
                    adapter.setProductIds(productIds);
                    adapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        Button addProductBtn = findViewById(R.id.add_product_btn);
        addProductBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(InventoryActivity.this, AddProductActivity.class));
            }
        });
    }
}
