package com.example.trackvault;
/*
 * Author: Krithika Kasaragod
 * FileName: AuthActivity.java
 */
import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.example.trackvault.Fragments.LoginFragment;
import com.example.trackvault.Fragments.SignUpFragment;
import com.google.firebase.auth.FirebaseAuth;

public class AuthActivity extends AppCompatActivity implements IService{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auth);

        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        if(mAuth.getCurrentUser()==null) {
            getSupportFragmentManager().beginTransaction().add(R.id.containerMain,new LoginFragment(), "LoginFragment")
                    .commit();
        }else{
            Intent intent= new Intent(this, MainActivity.class);
            startActivity(intent);
            finish();
        }
    }

    @Override
    public void gotoForumsFragment() {
        Intent intent= new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    public void gotoRegisterFragment() {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.containerMain, new SignUpFragment(),getString(R.string.registerFragment))
                .addToBackStack(null)
                .commit();
    }

    @Override
    public void gotoPreviousFragment() {
        getSupportFragmentManager().popBackStack();
    }
}