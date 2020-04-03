package com.hackcrisis.shopsafeuser;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.view.View;
import android.widget.Button;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.hackcrisis.shopsafeuser.Adapter.CategoryAdapter;

import java.util.ArrayList;

import static android.Manifest.permission.ACCESS_FINE_LOCATION;

public class MainActivity extends AppCompatActivity {

    private ArrayList<String> categories = new ArrayList<>();
    private DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();

    private static String TAG = "MAIN_ACTIVITY";
    private Location selectedLocation;
    private FusedLocationProviderClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        requestLocationPermission();
        client = LocationServices.getFusedLocationProviderClient(this);

        RecyclerView recyclerView = findViewById(R.id.feed_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        final CategoryAdapter adapter = new CategoryAdapter(this, new ArrayList<String>());
        recyclerView.setAdapter(adapter);

        databaseReference.child("categories").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                    String c = snapshot.getKey();
                    categories.add(c);
                }
                adapter.setCategories(categories);
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        final Handler handler = new Handler();
        final Runnable r = new Runnable() {
            @Override
            public void run() {
                handler.postDelayed(this, 2000);
                client.getLastLocation().addOnSuccessListener(new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        if(location!=null){
                            databaseReference.child("users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("lat").setValue(location.getLatitude());
                            databaseReference.child("users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("lng").setValue(location.getLongitude());
                        }
                    }
                });
            }
        };

        handler.postDelayed(r, 0);

        Button cartBtn = findViewById(R.id.cart_btn);
        cartBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, CartViewActivity.class));
            }
        });
    }

    private void requestLocationPermission(){
        ActivityCompat.requestPermissions(this, new String[]{ACCESS_FINE_LOCATION}, 1);
    }
}
