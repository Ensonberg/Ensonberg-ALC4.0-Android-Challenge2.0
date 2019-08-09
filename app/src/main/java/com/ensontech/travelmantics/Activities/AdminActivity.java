package com.ensontech.travelmantics.Activities;

import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.ensontech.travelmantics.FirebaseUtil;
import com.ensontech.travelmantics.R;
import com.ensontech.travelmantics.TravelDeal;
import com.firebase.ui.auth.data.model.Resource;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.IOException;

public class AdminActivity extends AppCompatActivity {
    private EditText mTitle, mAmount, mDescription;
    
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mReference;
    private String amount, title, description;
    private TravelDeal mDeal;
    private Button mUploadImage;
    private ImageView mImage;
    private final int RESULT_LOAD_IMG=42;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);
        final Intent intent = getIntent();
       mFirebaseDatabase=FirebaseUtil.mfireBaseDatabase;
       mReference=mFirebaseDatabase.getReference().child("traveldeals");

        mTitle = findViewById(R.id.title);
        mAmount = findViewById(R.id.amount);
        mDescription = findViewById(R.id.description);
      
        mUploadImage=findViewById(R.id.add_image);
        mImage=findViewById(R.id.image);
        
        
        TravelDeal deal = intent.getParcelableExtra("deals");
        if (deal == null) {
            deal = new TravelDeal();
        }
        this.mDeal = deal;
        mTitle.setText(deal.getTitle());
        mAmount.setText(deal.getAmount());
        mDescription.setText(deal.getDescription());
        showImage(deal.getImageUrl());
    mUploadImage.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
            photoPickerIntent.setType("image/*");
            startActivityForResult(photoPickerIntent, RESULT_LOAD_IMG);
           
        }
    });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_item, menu);
        if(FirebaseUtil.isAdmin){
            menu.findItem(R.id.delete).setVisible(true);
            menu.findItem(R.id.save).setVisible(true);
            enableEditext(true);
        }
        else {
            menu.findItem(R.id.delete).setVisible(false);
            menu.findItem(R.id.save).setVisible(false);
            enableEditext(false);
        }
        return true;
    }

    private void checkFields() {

        amount = mAmount.getText().toString();
        title = mTitle.getText().toString();
        description = mDescription.getText().toString();
        if (TextUtils.isEmpty(amount)) {
            mAmount.setError("Enter an amount");
        } else if (TextUtils.isEmpty(title)) {
            mTitle.setError("Enter a title");
        } else if (TextUtils.isEmpty(description)) {
            mDescription.setError("Enter a description");
        } else {
            save();
            backToMenu();
            Toast.makeText(getApplicationContext(), "Saved", Toast.LENGTH_SHORT).show();
        }
    }

    private void save() {


        mDeal.setAmount(mAmount.getText().toString());
        mDeal.setDescription(mDescription.getText().toString());
        mDeal.setTitle(mTitle.getText().toString());
        if (mDeal.getId() == null) {
            mReference.push().setValue(mDeal);
        } else {
            mReference.child(mDeal.getId()).setValue(mDeal);
            
        }
       

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.save:
                checkFields();
                return  true;
            case R.id.delete:
                deleteDeal();
                Toast.makeText(getApplicationContext(),"Deal Deleted",Toast.LENGTH_LONG).show();
                backToMenu();
                return true;

        }

        return super.onOptionsItemSelected(item);
    }
    private void deleteDeal(){
        if (mDeal==null){
            Toast.makeText(getApplicationContext(),"Please Save the Deal Before Deleting",Toast.LENGTH_SHORT).show();
        }
        else {
            mReference.child(mDeal.getId()).removeValue();
            if (!(mDeal.getImageName()!=null&&mDeal.getImageName().isEmpty()==false)) {
                StorageReference ref = FirebaseUtil.mStorage.getReference().child(mDeal.getImageName());
                ref.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(getApplicationContext(), "Deleted Sucessfully", Toast.LENGTH_SHORT).show();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getApplicationContext(), "Fail to Delete", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }
        
    }
    private void backToMenu(){
        Intent intent = new Intent(this, UserActivity.class);
        startActivity(intent);
        finish();
    }
    private void enableEditext(boolean isEnabled){
        mTitle.setEnabled(isEnabled);
        mDescription.setEnabled(isEnabled);
        mAmount.setEnabled(isEnabled);
        mUploadImage.setEnabled(isEnabled);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode){
            case RESULT_LOAD_IMG:
                Uri selectedImage = data.getData();
               
                    mImage.setImageURI(selectedImage);
                    StorageReference reference=FirebaseUtil.mStorageRef.child(selectedImage.getLastPathSegment());
                    reference.putFile(selectedImage).addOnSuccessListener(
                            new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(final UploadTask.TaskSnapshot taskSnapshot) {
                            String pictureName=taskSnapshot.getStorage().getPath();
                            mDeal.setImageName(pictureName);
                            taskSnapshot.getStorage().getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    mDeal.setImageUrl(String.valueOf(uri));
                                  
                                    showImage(String.valueOf(uri));
                                   
                                   
                                }
                            });
                        }
                    }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                            double currentProgress = (100 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();
                            long mProgress=0;
                            if( currentProgress > (mProgress + 15)){
                                mProgress = (100 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();
                                //Log.d(TAG, "onProgress: upload is " + mProgress + "& done");
                                Toast.makeText(getApplicationContext(), mProgress + "%", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                
        }
        }
        private void showImage(String url){
         if (url !=null&& url.isEmpty()==false){
             int width = Resources.getSystem().getDisplayMetrics().widthPixels;
             Picasso.get().load(url).resize(width,width*2/3).centerCrop().into(mImage);
         }
        }
    }

