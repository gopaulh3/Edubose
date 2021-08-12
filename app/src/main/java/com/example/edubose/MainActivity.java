package com.example.edubose;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private String currentUserId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initStuff();
    }

    private void initStuff() {
        Toolbar mToolbar = findViewById(R.id.main_page_toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle(R.string.app_name);

        String[] s = {"Basic Math","Advanced Math","Basic Bio","Advanced Bio","Basic Chem","Basic Bio"};
        Integer[] i = {R.drawable.simplemath,R.drawable.thewildrobot,R.drawable.themartian,R.drawable.hediedwith,R.drawable.mariasemples,R.drawable.thewildrobot};
        List<String> name = Arrays.asList(s);
        List<Integer> drawables = Arrays.asList(i);

        RecyclerView myrv = findViewById(R.id.recyclerView_main);
        RecyclerViewAdapter myAdapter = new RecyclerViewAdapter(this, name, drawables);
        myrv.setLayoutManager(new GridLayoutManager(this, 3));
        myrv.setAdapter(myAdapter);

        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        if (mAuth.getCurrentUser() != null) {
            currentUserId = mAuth.getCurrentUser().getUid();
            verifyUser();
        } else {
            startActivity(new Intent(getApplicationContext(), LoginActivity.class));
        }
    }

    private void verifyUser() {
        DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
        rootRef.child("Users").child(currentUserId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (String.valueOf(dataSnapshot.child("username").getValue()).substring(0, 7).equals("Default")) {
                    Toast.makeText(MainActivity.this, "Condition Met", Toast.LENGTH_SHORT).show();
//                    startActivity(new Intent(getApplicationContext(),SettingsActivity.class));
                } else {
                    Log.i("MainActivity", "User Logged In Successfully!");
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {}
        });
    }
}
