package com.example.edubose;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;

public class LoginActivity extends AppCompatActivity {

    private Button loginButton,signUpButton,signUpLoginButton;
    private TextView loginRegisterTextView;
    private TextView confirmPasswordTextView;
    private EditText emailEditText,passwordEditText,confirmPasswordEditText;
    private boolean isLoginOption;
    private FirebaseAuth mAuth;
    private ProgressDialog loadingBar;
    private DatabaseReference usersRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        initStuff();
    }

    public void clicky(View view) {
        switch (view.getId()) {
            case R.id.btnLogin:
                if (!isLoginOption) {
                    isLoginOption = true;
                    isTrue();
                }
                break;
            case R.id.btnSignup:
                if (isLoginOption) {
                    isLoginOption = false;
                    isTrue();
                }
                break;
            case R.id.loginOrSignUpButton:
                String email = emailEditText.getText().toString();
                String password = passwordEditText.getText().toString();
                if (isLoginOption) {
                    allowUserToLogin(email,password);
                } else {
                    createNewAccount(email,password);
                }
                break;
            default:
                break;
        }
    }

    private void isTrue() {
        if (isLoginOption) {
            loginRegisterTextView.setText("Login");
            confirmPasswordEditText.setVisibility(View.INVISIBLE);
            confirmPasswordTextView.setText("Forgot Password?");
            signUpLoginButton.setText("Login");
            loginButton.setBackgroundResource(R.drawable.loginbuttonshape);
            signUpButton.setBackgroundResource(R.drawable.signupbuttonshape);
            loginButton.setTextColor(Color.WHITE);
            signUpButton.setTextColor(Color.BLACK);

        } else {
            loginRegisterTextView.setText("Register");
            confirmPasswordEditText.setVisibility(View.VISIBLE);
            confirmPasswordTextView.setText("Confirm Password");
            signUpLoginButton.setText("Register");
            loginButton.setBackgroundResource(R.drawable.signupbuttonshape);
            signUpButton.setBackgroundResource(R.drawable.loginbuttonshape);
            loginButton.setTextColor(Color.BLACK);
            signUpButton.setTextColor(Color.WHITE);
        }
    }

    private void initStuff() {
        ImageView facebookImageView, googleImageView, twitterImageView;
        mAuth = FirebaseAuth.getInstance();
        usersRef = FirebaseDatabase.getInstance().getReference().child("Users");
        isLoginOption = true;
        loginButton = findViewById(R.id.btnLogin);
        signUpButton = findViewById(R.id.btnSignup);
        signUpLoginButton = findViewById(R.id.loginOrSignUpButton);
        facebookImageView = findViewById(R.id.facebook_imageVIew);
        googleImageView = findViewById(R.id.google_imageVIew);
        twitterImageView = findViewById(R.id.twitter_imageView);
        TextView logoTextView = findViewById(R.id.logo_textView);
        loginRegisterTextView = findViewById(R.id.loginOrSignUp);
        TextView emailTextView = findViewById(R.id.email_textView);
        TextView passwordTextView = findViewById(R.id.password_textView);
        confirmPasswordTextView = findViewById(R.id.confirmPassword_TextView);
        emailEditText = findViewById(R.id.emailEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        confirmPasswordEditText = findViewById(R.id.confirmPasswordEditText);
        loadingBar = new ProgressDialog(this);
    }

    private void sendUserToMainActivity() {
        Intent mainIntent = new Intent(LoginActivity.this, MainActivity.class);
        mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(mainIntent);
        finish();
    }

    private void allowUserToLogin(String email, String password) {
        if (TextUtils.isEmpty(password) || TextUtils.isEmpty(email)) {
            Toast.makeText(this, "Please Fill In The Blanks", Toast.LENGTH_SHORT).show();
        } else {
            loadingBar.setTitle("Sign In");
            loadingBar.setMessage("Please Wait...");
            loadingBar.setCanceledOnTouchOutside(true);
            loadingBar.show();

            mAuth.signInWithEmailAndPassword(email,password)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {

                                String currentUserId = mAuth.getCurrentUser().getUid();
                                String deviceToken = FirebaseInstanceId.getInstance().getToken();

                                usersRef.child(currentUserId).child("device_token").setValue(deviceToken).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            sendUserToMainActivity();
                                            Toast.makeText(LoginActivity.this, "Login Successful", Toast.LENGTH_SHORT).show();
                                            loadingBar.dismiss();
                                        }
                                    }
                                });

                            } else {
                                String preMessage = task.getException().toString();
                                String message = preMessage.substring(preMessage.indexOf("The"));

                                Toast.makeText(LoginActivity.this, message, Toast.LENGTH_LONG).show();
                                loadingBar.dismiss();
                            }
                        }
                    });
        }
    }
    private void createNewAccount(String email, String password) {
        String confirmPassword = confirmPasswordEditText.getText().toString();
        if (TextUtils.isEmpty(password) || TextUtils.isEmpty(email)) {
            Toast.makeText(getApplicationContext(), "Please Fill In The Blanks", Toast.LENGTH_SHORT).show();
        } else if (!password.equals(confirmPassword)) {
            Toast.makeText(getApplicationContext(), "Passwords do not match!", Toast.LENGTH_SHORT).show();
        } else  {
            loadingBar.setTitle("Creating New Account");
            loadingBar.setMessage("Account being created...");
            loadingBar.setCanceledOnTouchOutside(true);
            loadingBar.show();

            mAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                String deviceToken = FirebaseInstanceId.getInstance().getToken();

                                String currentUserId = mAuth.getCurrentUser().getUid();
                                usersRef.child(currentUserId).setValue("");

                                usersRef.child(currentUserId).child("device_token").setValue(deviceToken);
                                usersRef.child(currentUserId).child("uid").setValue(currentUserId);
                                usersRef.child(currentUserId).child("username").setValue("Default"+currentUserId.substring(20));


                                sendUserToMainActivity();
                                Toast.makeText(LoginActivity.this, "Account Created Successfully", Toast.LENGTH_SHORT).show();
                                loadingBar.dismiss();
                            } else {
                                String preMessage = task.getException().toString();
                                String message = preMessage.substring(preMessage.indexOf("The"));

                                Toast.makeText(LoginActivity.this, message, Toast.LENGTH_LONG).show();
                                loadingBar.dismiss();
                            }
                        }
                    });
        }
    }
}
