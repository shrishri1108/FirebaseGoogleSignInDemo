package com.example.firebasegooglesignin;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.example.firebasegooglesignin.databinding.ActivityMainBinding;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

import java.time.Duration;
import java.util.Random;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding mainBinding;
    private GoogleSignInClient googleSignInClient;
    private FirebaseAuth auth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mainBinding= ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(mainBinding.getRoot());

        auth= FirebaseAuth.getInstance();
        processrequest();
        mainBinding.btnGoogleSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                processLogin();
            }
        });
    }

    private void processLogin() {
        Intent signInIntent= googleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, 101);
    }

    private void processrequest() {

        GoogleSignInOptions gso= new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        //Build a GoogleSignInClient with the options secified by gso .
        googleSignInClient= GoogleSignIn.getClient(this,gso);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from Launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if(requestCode == 101){
            Task<GoogleSignInAccount> task= GoogleSignIn.getSignedInAccountFromIntent(data);
            try{
                // Google Sign In was successful ,  authenticate with Firebase
                GoogleSignInAccount account= task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account.getIdToken());
            }
            catch (ApiException e){
                Snackbar.make( mainBinding.mainLayout, "Exception is occuring "+ e.getMessage(), BaseTransientBottomBar.LENGTH_LONG)
                        .show();
            }
        }

    }

    private void firebaseAuthWithGoogle(String idToken) {
        AuthCredential credential= GoogleAuthProvider.getCredential(idToken, null);
        auth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            // Sign in success , update UI .
                            Log.d(TAG, "onComplete:  success");
                            FirebaseUser user= auth.getCurrentUser();
                            updateUI(user);
                        }
                        else {
                            Log.w(TAG, "onComplete: ", task.getException());
                            Snackbar.make(mainBinding.mainLayout, "Authentication Failed.", BaseTransientBottomBar.LENGTH_LONG)
                                    .setAction("UNDO",
                                            // If undo button is pressed the toast message
                                            new View.OnClickListener() {
                                                @Override
                                                public void onClick(View view) {
                                                    Toast.makeText(MainActivity.this, "Undo is Clicked . ", Toast.LENGTH_SHORT).show();
                                                }
                                            })
                                    .show();
                            updateUI(null);
                        }
                    }
                });
    }

    @Override
    protected void onStart() {
        super.onStart();
        //  Check if user is signed in (non-null)  and update UI accordinly .
        FirebaseUser currentUser= auth.getCurrentUser();
        updateUI(currentUser);

    }

    private void updateUI(FirebaseUser user) {
        if(user!=null){
            Intent intent= new Intent(MainActivity.this, Dashboard.class);
            Toast.makeText(this, "LoggedIn Successfully ! ", Toast.LENGTH_SHORT).show();
            intent.putExtra("name", user+"");
            startActivity(intent);
            finish();
        }
    }

}