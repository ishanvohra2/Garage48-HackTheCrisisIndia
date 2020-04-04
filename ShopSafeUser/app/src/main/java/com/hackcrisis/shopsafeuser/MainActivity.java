package com.hackcrisis.shopsafeuser;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Dialog;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.TextInputEditText;
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

    public static String pinCode = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        requestLocationPermission();
        client = LocationServices.getFusedLocationProviderClient(this);

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.inflateMenu(R.menu.toolbar_menu);
        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                if(item.getItemId() == R.id.menu_main_recent_order){
                    startActivity(new Intent(MainActivity.this, RecentOrdersActivity.class));
                }
                return false;
            }
        });

        RecyclerView recyclerView = findViewById(R.id.feed_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        final CategoryAdapter adapter = new CategoryAdapter(this, new ArrayList<String>());
        recyclerView.setAdapter(adapter);

        databaseReference.child("categories").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                categories = new ArrayList<>();
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

        databaseReference.child("familyDetails").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(!dataSnapshot.exists()){
                    Dialog dialog = onCreateDialog();
                    dialog.show();
                    DisplayMetrics metrics = getResources().getDisplayMetrics();
                    int width = metrics.widthPixels;
                    int height = metrics.heightPixels;
                    dialog.getWindow().setLayout(width, height);
                    dialog.getWindow().setBackgroundDrawable(getResources().getDrawable(android.R.color.transparent));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        databaseReference.child("users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                pinCode = dataSnapshot.child("pinCode").getValue(String.class);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        final Button cartBtn = findViewById(R.id.cart_btn);
        cartBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, CartViewActivity.class));
            }
        });

        databaseReference.child("cartDetails").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    cartBtn.setVisibility(View.VISIBLE);
                }
                else{
                    cartBtn.setVisibility(View.GONE);
                    Toast.makeText(MainActivity.this, "Your cart is empty", Toast.LENGTH_SHORT).show();
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void requestLocationPermission(){
        ActivityCompat.requestPermissions(this, new String[]{ACCESS_FINE_LOCATION}, 1);
    }

    private Dialog onCreateDialog(){
        final Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.slot_fixing_dialog);

        final TextInputEditText adultsEt = dialog.findViewById(R.id.adults_et);
        final TextInputEditText childrenEt = dialog.findViewById(R.id.children_et);

        Button submitBtn = dialog.findViewById(R.id.save_btn);
        submitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                databaseReference.child("familyDetails").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("adults")
                        .setValue(Integer.parseInt(adultsEt.getText().toString()));
                databaseReference.child("familyDetails").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("children")
                        .setValue(Integer.parseInt(childrenEt.getText().toString()));


                dialog.dismiss();
            }
        });

        return dialog;
    }
}
