package com.ensontech.travelmantics.Activities;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import com.ensontech.travelmantics.DealAdapter;
import com.ensontech.travelmantics.FirebaseUtil;
import com.ensontech.travelmantics.R;
import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

public class UserActivity extends AppCompatActivity {
    RecyclerView mRecyclerView;
    
    

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);
       
        
       
        
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater=getMenuInflater();
        inflater.inflate(R.menu.item_menu,menu);
        MenuItem menuItem=menu.findItem(R.id.insert_item);
        if (FirebaseUtil.isAdmin==true){
            menuItem.setVisible(true);
        }
        else {
            menuItem.setVisible(false);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.insert_item:
                Intent intent=new Intent(getApplicationContext(),AdminActivity.class);
                startActivity(intent);
                return true;
            case R.id.logout:
                AuthUI.getInstance()
                        .signOut(getApplicationContext())
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()){
                                    Toast.makeText(getApplicationContext(),"Logout Successfully",Toast.LENGTH_LONG).show();
                                    FirebaseUtil.attachListnener();
                                }
                                
                            }
                        });
                FirebaseUtil.detachListener();
                return true;
        }
        return true;
    }

    @Override
    protected void onPause() {
        super.onPause();
        FirebaseUtil.detachListener();
    }

    @Override
    protected void onResume() {
        super.onResume();
        FirebaseUtil.openReference("traveldeals",this);
        mRecyclerView=findViewById(R.id.recylerview);
        LinearLayoutManager manager=new LinearLayoutManager(getApplicationContext(),LinearLayoutManager.VERTICAL,false);
        mRecyclerView.setLayoutManager(manager);
        final DealAdapter adapter=new DealAdapter();
        mRecyclerView.setAdapter(adapter);
        FirebaseUtil.attachListnener();
    }
    public  void showMenu(){
        invalidateOptionsMenu();
        
    }
}
