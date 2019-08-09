package com.ensontech.travelmantics;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.ensontech.travelmantics.Activities.AdminActivity;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.ConcurrentModificationException;
import java.util.List;

public class DealAdapter extends RecyclerView.Adapter<DealAdapter.DealViewHolder> {
    private List<TravelDeal> mDeals;
    private Context mContext;
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mReference;
    private ChildEventListener mChildEventListener;
    @NonNull
    @Override
    public DealViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        Context context=viewGroup.getContext();
        View view= LayoutInflater.from(context).inflate(R.layout.item_layout,viewGroup,false);
        
        return new DealViewHolder(view);
    }
    public DealAdapter(){
       
       
        mFirebaseDatabase = FirebaseUtil.mfireBaseDatabase;
        mReference = FirebaseUtil.mDatabaseRefrences;
        mDeals=FirebaseUtil.mDeals;
        mChildEventListener=new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                TravelDeal travelDeal=dataSnapshot.getValue(TravelDeal.class);
                
                    travelDeal.setId(dataSnapshot.getKey());
                
                mDeals.add(travelDeal);
                notifyDataSetChanged();
                
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };
        mReference.addChildEventListener(mChildEventListener);
    }

    @Override
    public void onBindViewHolder(@NonNull DealViewHolder dealViewHolder, int position) {
        TravelDeal deal=mDeals.get(position);
        dealViewHolder.bind(deal);

    }

    @Override
    public int getItemCount() {
        return mDeals.size();
    }

    public class DealViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        TextView title,amount,description;
        ImageView mImageView;
        public DealViewHolder(@NonNull View itemView) {
            super(itemView);
            title=itemView.findViewById(R.id.title);
            amount=itemView.findViewById(R.id.amount);
            description=itemView.findViewById(R.id.description);
            mImageView=itemView.findViewById(R.id.image);
            itemView.setOnClickListener(this);
        }
        public void bind(TravelDeal deal){
            title.setText(deal.getTitle());
            amount.setText(deal.getAmount());
            description.setText(deal.getDescription());
        }

        @Override
        public void onClick(View v) {
            int position = getAdapterPosition();
            TravelDeal deal = mDeals.get(position);
            Intent intent = new Intent(v.getContext(), AdminActivity.class);
            intent.putExtra("deals",deal);
            v.getContext().startActivity(intent);
            notifyDataSetChanged();
            
        }
            
                }
}
