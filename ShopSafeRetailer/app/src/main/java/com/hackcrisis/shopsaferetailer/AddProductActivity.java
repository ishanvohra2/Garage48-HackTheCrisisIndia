package com.hackcrisis.shopsaferetailer;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.hackcrisis.shopsaferetailer.Adapters.AddNewProductAdapter;
import com.hackcrisis.shopsaferetailer.Data.ProductDetails;

import java.util.ArrayList;

public class AddProductActivity extends AppCompatActivity {

    private DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
    private ArrayList<ProductDetails> products = new ArrayList<>();
    private AddNewProductAdapter addNewProductAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_product);

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        RecyclerView recyclerView = findViewById(R.id.feed_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        addNewProductAdapter = new AddNewProductAdapter(this, new ArrayList<ProductDetails>());
        recyclerView.setAdapter(addNewProductAdapter);

        SearchView searchView = findViewById(R.id.search_view);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                Query q = FirebaseDatabase.getInstance().getReference("productDetails").orderByChild("productName")
                        .startAt(query)
                        .endAt(query + "\uf8ff");
                q.addValueEventListener(valueEventListener);

                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                Query q = FirebaseDatabase.getInstance().getReference("productDetails").orderByChild("productName")
                        .startAt(newText)
                        .endAt(newText + "\uf8ff");
                q.addValueEventListener(valueEventListener);

                return false;
            }
        });

        databaseReference.child("productDetails").addValueEventListener(valueEventListener);
    }

    ValueEventListener valueEventListener = new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
            products = new ArrayList<>();
            for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                final ProductDetails product = snapshot.getValue(ProductDetails.class);
                databaseReference.child("inventory").child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                        .child(product.getProductId()).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if(!dataSnapshot.exists()){
                            products.add(product);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }
            addNewProductAdapter.setProducts(products);
            addNewProductAdapter.notifyDataSetChanged();
        }

        @Override
        public void onCancelled(@NonNull DatabaseError databaseError) {

        }
    };
}
