package com.hackcrisis.shopsafeuser.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.hackcrisis.shopsafeuser.ConfirmOrderActivity;
import com.hackcrisis.shopsafeuser.Data.SlotItem;
import com.hackcrisis.shopsafeuser.R;

import java.util.ArrayList;

import static com.hackcrisis.shopsafeuser.ConfirmOrderActivity.shopId;

public class SlotListAdapter extends RecyclerView.Adapter<SlotListAdapter.MyViewHolder> {

    private Context context;
    private ArrayList<SlotItem> dataSet;

    public int setSlots(ArrayList<SlotItem> dataSet){
        this.dataSet = dataSet;
        return dataSet.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{

        private TextView slotTimeTv, slotsLeftTv;

        public MyViewHolder(View itemView){
            super(itemView);
            slotsLeftTv = itemView.findViewById(R.id.spots_tv);
            slotTimeTv = itemView.findViewById(R.id.timeTv);
        }
    }

    public SlotListAdapter(Context context, ArrayList<SlotItem> dataSet){
        this.dataSet = dataSet;
        this.context = context;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.slot_item, parent, false);
        MyViewHolder myViewHolder = new MyViewHolder(view);

        return myViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder holder, final int position) {
        holder.slotTimeTv.setText(dataSet.get(position).getSlotTime());

//        Query query = FirebaseDatabase.getInstance().getReference("orderDetails").orderByChild("shopId").equalTo(shopId);
//        query.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                if(dataSnapshot.exists()){
//                    int count = 0;
//                    for(DataSnapshot snapshot : dataSnapshot.getChildren()){
//                        if(snapshot.child("slotId").getValue(String.class).equals(dataSet.get(position).getSlotId()))
//                            count++;
//                    }
//                    if(count>=5){
//                        holder.itemView.setEnabled(false);
//                        holder.slotTimeTv.setTextColor(context.getResources().getColor(android.R.color.darker_gray));
//                    }
//                }
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//
//            }
//        });

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ConfirmOrderActivity.slotId = dataSet.get(position).getSlotId();
                Toast.makeText(context, ConfirmOrderActivity.slotId , Toast.LENGTH_SHORT).show();
            }
        });

    }

    @Override
    public int getItemCount() {
        return dataSet.size();
    }

}
