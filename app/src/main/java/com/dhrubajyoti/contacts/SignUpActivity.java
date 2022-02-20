package com.dhrubajyoti.contacts;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import static com.dhrubajyoti.contacts.MainActivity.contacts;
import static com.dhrubajyoti.contacts.MainActivity.mAuth;
import static com.dhrubajyoti.contacts.MainActivity.database;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.dhrubajyoti.contacts.databinding.ActivitySignUpBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class SignUpActivity extends AppCompatActivity {

    private ActivitySignUpBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySignUpBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        database = FirebaseDatabase.getInstance();
        mAuth = FirebaseAuth.getInstance();


        binding.btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                binding.progressBar.setVisibility(View.VISIBLE);
//                binding.parentLayout.setBackgroundColor(getResources().getColor(R.color.grey));
                try{
                    Long number = null;
                    try {
                        number = Long.parseLong(binding.edtEmail.getText().toString().trim());
                    }catch (Exception e){
                        Toast.makeText(SignUpActivity.this, "Please enter a valid 10 digit phone number!!", Toast.LENGTH_LONG).show();   return;
                    }
                    number = Long.parseLong(binding.edtEmail.getText().toString().trim());
                    if(number.toString().length()==10 && number.toString().charAt(0)!='0') {
                        if(!binding.edtPassword.getText().toString().equals(binding.edtRePassword.getText().toString())){
                            new AlertDialog.Builder(SignUpActivity.this).setMessage("Passwords does not matching").setNegativeButton("Dismiss", new DialogInterface.OnClickListener(){
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                }
                            }).create().show();
                            return;
                        }
                        Long finalNumber = number;
                        mAuth.createUserWithEmailAndPassword(number.toString() + "@gmail.com", binding.edtPassword.getText().toString().trim())
                                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                    @Override
                                    public void onComplete(@NonNull Task<AuthResult> task) {
                                        if (task.isSuccessful()) {
                                            User user = new User(finalNumber.toString(), binding.edtPassword.getText().toString());
                                            database.getReference().child("Users").child(task.getResult().getUser().getUid()).setValue(user);
                                            Toast.makeText(SignUpActivity.this, "User created successfully", Toast.LENGTH_LONG).show();
//                                            database.getReference().child("Users").child(task.getResult().getUser().getUid()).child("Contacts")
//                                                    .setValue("null");
                                            startActivity(new Intent(SignUpActivity.this, MainActivity.class));
                                        } else {
                                            Toast.makeText(SignUpActivity.this, task.getException().getMessage(), Toast.LENGTH_LONG).show();
                                        }
                                    }
                                });
                    }else{
                        Toast.makeText(SignUpActivity.this, "Please enter your valid 10 digit contact number!!", Toast.LENGTH_LONG).show();  return;
                    }
                }catch (Exception e){
                    Toast.makeText(SignUpActivity.this, "Something went wrong, please try again!", Toast.LENGTH_LONG).show();
                }finally {
//                    binding.progressBar.setVisibility(View.INVISIBLE);
//                    binding.parentLayout.setBackgroundColor(getResources().getColor(R.color.bgColor));
                }
            }
        });

        binding.tvAlreadyHaveAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(SignUpActivity.this, SignInActivity.class));
            }
        });
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }
}