package com.hackcrisis.shopsaferetailer;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import static android.Manifest.permission.ACCESS_FINE_LOCATION;

public class MainActivity extends AppCompatActivity {

    private static String TAG = "MAIN_ACTIVITY";
    private FusedLocationProviderClient client;

    private DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        requestLocationPermission();
        client = LocationServices.getFusedLocationProviderClient(this);

        Button inventoryBtn = findViewById(R.id.inventory_btn);
        inventoryBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, InventoryActivity.class));
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
                            databaseReference.child("shopDetails").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("latitude").setValue(location.getLatitude());
                            databaseReference.child("shopDetails").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("longitude").setValue(location.getLongitude());
                        }
                    }
                });
            }
        };

        handler.postDelayed(r, 0);
    }

    private void requestLocationPermission(){
        ActivityCompat.requestPermissions(this, new String[]{ACCESS_FINE_LOCATION}, 1);
    }
}
