package com.example.edubose;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class SettingsActivity extends AppCompatActivity{

    private Button updateAccountSettings;
    private EditText userName;
    private CircleImageView userProfileImage;
    private String currentUserId;
    private DatabaseReference currentUserRef, userStateRef;
    private StorageReference userProfileImagesRef;
    private ProgressDialog loadingBar;
    private TextView userNumber;
    private ArrayList<String> lvTitles, lvDescription;
    private ArrayList<Integer> lvImages;
    private String[] changedThings = new String[1];
    private ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        initializeFields();
        retrieveUserInfo();
        setAllOnClickListeners();
    }

    @Override
    protected void onResume() {
        super.onResume();
        userStateRef.setValue("online");
    }

    @Override
    protected void onPause() {
        super.onPause();
        userStateRef.setValue("offline");
    }

    private void setListViewAdapter(String retrieveName) {

            changedThings[0] = retrieveName;

            String[] listViewTitle = {"General", "Stats","Friends","Logout"};
            String[] listViewDescription = {"Privacy etc...","Records etc.." ,"Add or remove","logout of your account"};
            Integer[] listViewImages = {R.drawable.ic_info,R.drawable.ic_assessment, R.drawable.ic_friends,R.drawable.ic_exit};

            lvTitles.addAll(Arrays.asList(listViewTitle));
            lvDescription.addAll(Arrays.asList(listViewDescription));
            lvImages.addAll(Arrays.asList(listViewImages));

        List<HashMap<String, String>> aList = new ArrayList<>();
        for (int i = 0; i < 4; i++) {
            HashMap<String, String> hm = new HashMap<>();
            hm.put("ListTitle", lvTitles.get(i));
            hm.put("ListDescription", lvDescription.get(i));
            hm.put("ListImages", Integer.toString(lvImages.get(i)));
            aList.add(hm);
        }
        String[] from = {"ListImages", "ListTitle", "ListDescription"};
        int[] to = {R.id.settings_listView_images, R.id.settings_title, R.id.settings_description};
        SimpleAdapter simpleAdapter = new SimpleAdapter(getBaseContext(), aList, R.layout.new_settings, from, to);
        listView.setAdapter(simpleAdapter);
    }

    private void setAllOnClickListeners() {
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0) {
                    Toast.makeText(SettingsActivity.this, "General Selcted", Toast.LENGTH_SHORT).show();
                } else if (position == 1) {
                    Toast.makeText(SettingsActivity.this, "Stats Selcted", Toast.LENGTH_SHORT).show();
                } else if (position == 2) {
                    Toast.makeText(SettingsActivity.this, "Friends Selcted", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(SettingsActivity.this, "Logout Selcted", Toast.LENGTH_SHORT).show();
//                    Intent intent = new Intent(getApplicationContext(), DetailedSettingsActivity.class);
//                    intent.putExtra("heading", lvTitles.get(position));
//                    intent.putExtra("id", currentUserId);
//                    startActivity(intent);
                }
            }
        });

        updateAccountSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateSettings();
            }
        });
        userProfileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (CropImage.hasPermissionInManifest(SettingsActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE))
                    CropImage.activity().start(SettingsActivity.this);
            }
        });
    }

    private void initializeFields() {
        updateAccountSettings = findViewById(R.id.saveChanges);
        userName = findViewById(R.id.new_user_profile_name);
        userNumber = findViewById(R.id.new_user_number);
        userProfileImage = findViewById(R.id.new_users_profile_image);
        loadingBar = new ProgressDialog(this);
        Toolbar settingsToolbar = findViewById(R.id.settings_toolbar);
        setSupportActionBar(settingsToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowCustomEnabled(true);
        getSupportActionBar().setTitle("Account Settings");
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        currentUserId = mAuth.getCurrentUser().getUid();
        currentUserRef = FirebaseDatabase.getInstance().getReference().child("Users").child(currentUserId);
        userStateRef = currentUserRef.child("userState");
        currentUserRef.keepSynced(true);
        userProfileImagesRef = FirebaseStorage.getInstance().getReference().child("Profile Images");
        listView = findViewById(R.id.settings_list_view);
        lvTitles = new ArrayList<>();
        lvDescription = new ArrayList<>();
        lvImages = new ArrayList<>();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);

            if (resultCode == RESULT_OK) {

                loadingBar.setTitle("Set Profile Image");
                loadingBar.setMessage("Please Wait , Image Updating..");
                loadingBar.setCanceledOnTouchOutside(false);
                loadingBar.show();

                Uri resultUri = result.getUri();
                final StorageReference filePath = userProfileImagesRef.child(currentUserId + ".jpg");

                Bitmap bmp = null;
                try {
                    bmp = getResizedBitmap(MediaStore.Images.Media.getBitmap(SettingsActivity.this.getContentResolver(), resultUri),450);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                bmp.compress(Bitmap.CompressFormat.JPEG, 15, baos);
                byte[] byteData = baos.toByteArray();
                //uploading the image
                UploadTask uploadTask = filePath.putBytes(byteData);

                uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        filePath.getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                            @Override
                            public void onComplete(@NonNull Task<Uri> task) {
                                if (task.isSuccessful()) {
                                    currentUserRef.child("userimage").setValue(task.getResult().toString());
                                    System.out.println("Success in adding photo apparently!");
                                }
                            }
                        });
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(SettingsActivity.this, "Upload Failed -> " + e, Toast.LENGTH_LONG).show();
                    }
                });

                loadingBar.dismiss();
            }
        }
    }

    public Bitmap getResizedBitmap(Bitmap image, int maxSize) {
        int width = image.getWidth();
        int height = image.getHeight();

        float bitmapRatio = (float)width / (float) height;
        if (bitmapRatio > 1) {
            width = maxSize;
            height = (int) (width / bitmapRatio);
        } else {
            height = maxSize;
            width = (int) (height * bitmapRatio);
        }
        return Bitmap.createScaledBitmap(image, width, height, true);
    }

    private void updateSettings() {
        if (TextUtils.isEmpty(userName.getText().toString())) {
            Toast.makeText(this, "Please fill in the blanks..", Toast.LENGTH_SHORT).show();
        } else if (userName.getText().toString().length()<7) {
            Toast.makeText(this, "Cannot be less than 7 characters..", Toast.LENGTH_SHORT).show();
        } else  {
            if (!changedThings[0].equals(userName.getText().toString())) {
                currentUserRef.child("username").setValue(userName.getText().toString());
                Toast.makeText(this, "Profile Updated Successfully!", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void retrieveUserInfo() {
        currentUserRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    userNumber.setText(String.valueOf(dataSnapshot.child("number").getValue()));
                    userName.setText(String.valueOf(dataSnapshot.child("username").getValue()));
                    userName.setSelection(userName.length());
                    if ((dataSnapshot.hasChild("image"))) Picasso.get().load(String.valueOf(dataSnapshot.child("image").getValue())).
                            placeholder(R.drawable.profile_image).into(userProfileImage);
                    setListViewAdapter(String.valueOf(dataSnapshot.child("name").getValue()));
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {}
        });
    }
}


