package com.example.edubose;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class InviteToPlayActivity extends AppCompatActivity {

    TextView headings_details_textView,tv5;
    Button startOrVsButton;
    DatabaseReference rootRef,usersRef;
    FirebaseAuth mAuth;
    String currentUserId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_invite_to_play);
        initStuff();
    }

    private void initStuff() {
        final String[] strings = getIntent().getStringArrayExtra("Info");
        startOrVsButton = findViewById(R.id.vs_button);
        headings_details_textView = findViewById(R.id.heading_details_textVIew);
        tv5 = findViewById(R.id.tv5);
        String s = "Time : "+strings[0] +"        Difficulty : "+strings[1]+"     Formation : "+strings[2]+" vs "+strings[3];
        headings_details_textView.setText(s);
        rootRef = FirebaseDatabase.getInstance().getReference();
        usersRef = rootRef.child("Users");
        mAuth = FirebaseAuth.getInstance();

        if (mAuth.getCurrentUser()==null) {
            Intent intent = new Intent(getApplicationContext(),LoginActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        }

        currentUserId = mAuth.getCurrentUser().getUid();

        usersRef.child(currentUserId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                tv5.setText(String.valueOf(dataSnapshot.child("username").getValue()));
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {}
        });

        if (strings[3].equals("0")) {
            startOrVsButton.setText("Start");
        }

        startOrVsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(),GameActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                intent.putExtra("strings",getIntent().getStringArrayExtra("Info"));
                startActivity(intent);
            }
        });


    }
}
