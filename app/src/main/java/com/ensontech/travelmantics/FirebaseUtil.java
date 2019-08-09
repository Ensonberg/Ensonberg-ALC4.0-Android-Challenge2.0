package com.ensontech.travelmantics;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.widget.Toast;

import com.ensontech.travelmantics.Activities.UserActivity;
import com.firebase.ui.auth.AuthUI;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class FirebaseUtil {
    public static FirebaseDatabase mfireBaseDatabase;
    public static DatabaseReference mDatabaseRefrences;
    public static FirebaseAuth mFirebaseAuth;
    public static UserActivity mCallerActivity;
    public static FirebaseAuth.AuthStateListener mAuthListener;
    public static FirebaseUtil mFireBaseUtil;
    public static List<TravelDeal> mDeals;
    public static FirebaseStorage mStorage;
    public static StorageReference mStorageRef;
    public static boolean isAdmin;
    public static final   int RC_SIGN_IN=123;

    public FirebaseUtil(){
        
    }
    public static void openReference(String ref, final UserActivity callerActivity){
        if(mFireBaseUtil==null){
            mFireBaseUtil=new FirebaseUtil();
            mfireBaseDatabase=FirebaseDatabase.getInstance();
            mFirebaseAuth=FirebaseAuth.getInstance();
            mCallerActivity=callerActivity;
            mAuthListener=new FirebaseAuth.AuthStateListener() {
                @Override
                public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                    if (firebaseAuth.getCurrentUser()==null){
                    FirebaseUtil.signIn();}
                    else {
                        String userId=firebaseAuth.getUid();
                        checkAdim(userId);
                        
                    }
                    
                    Toast.makeText(callerActivity.getApplicationContext(),"Welcome Back",Toast.LENGTH_LONG).show();
                   
               connectStorage();    
                   
                }
            };
            
        }
        mDeals=new ArrayList<>();
        mDatabaseRefrences=mfireBaseDatabase.getReference().child(ref);
    }

    public static void signIn() {
        List<AuthUI.IdpConfig>providers= Arrays.asList(
                new AuthUI.IdpConfig.EmailBuilder().build(),
                new AuthUI.IdpConfig.GoogleBuilder().build()

        );
        //create and launch sign-in intent
        mCallerActivity.startActivityForResult(
                AuthUI.getInstance()
                        .createSignInIntentBuilder()
                        .setAvailableProviders(providers)
                        .build(),RC_SIGN_IN
        );
    }
    public static void checkAdim(String uId){
        FirebaseUtil.isAdmin=false;
        DatabaseReference reference=mfireBaseDatabase.getReference().child("administrators").child(uId);
        ChildEventListener listener=new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                FirebaseUtil.isAdmin=true;
                mCallerActivity.showMenu();
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
        reference.addChildEventListener(listener);
    }

    public static void attachListnener(){
        mFirebaseAuth.addAuthStateListener(mAuthListener);
    }
    public static void detachListener(){
        mFirebaseAuth.removeAuthStateListener(mAuthListener);
    }
    public static void connectStorage(){
        mStorage=FirebaseStorage.getInstance();
        mStorageRef=mStorage.getReference().child("deals_pics");
    }
}
