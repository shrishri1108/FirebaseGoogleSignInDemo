package com.example.firebasegooglesignin;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.firebasegooglesignin.databinding.ActivityDashboardBinding;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.firebase.auth.FirebaseAuth;

public class Dashboard extends AppCompatActivity {

    private ActivityDashboardBinding dashboardBinding;
    private String user_name;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        dashboardBinding= ActivityDashboardBinding.inflate(getLayoutInflater());
        setContentView(dashboardBinding.getRoot());

        user_name= getIntent().getStringExtra("user_name");
        dashboardBinding.btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseAuth.getInstance().signOut();
                Toast.makeText(Dashboard.this, " Sign out completely . ", Toast.LENGTH_SHORT).show();
                Intent intent=new Intent(Dashboard.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        });
        GoogleSignInAccount account= GoogleSignIn.getLastSignedInAccount(this);
        dashboardBinding.tvUserName.setText(account.getDisplayName());
        Glide.with(this).load(account.getPhotoUrl()).into(dashboardBinding.imgUser);
        dashboardBinding.tvEmailId.setText(account.getEmail());

    }
}