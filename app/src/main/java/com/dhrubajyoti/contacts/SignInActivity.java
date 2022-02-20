package com.dhrubajyoti.contacts;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import static com.dhrubajyoti.contacts.MainActivity.contacts;
import static com.dhrubajyoti.contacts.MainActivity.mAuth;
import static com.dhrubajyoti.contacts.MainActivity.database;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.dhrubajyoti.contacts.databinding.ActivitySignInBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

public class SignInActivity extends AppCompatActivity {

    private ActivitySignInBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySignInBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        database = FirebaseDatabase.getInstance();
        mAuth = FirebaseAuth.getInstance();

        binding.btnSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                binding.progressBar.setVisibility(View.VISIBLE);
//                binding.parentLayout.setBackgroundColor(getResources().getColor(R.color.grey));
                try{
                    Long number = null;
                    try {
                        number = Long.parseLong(binding.edtEmail.getText().toString().trim());
                    }catch (Exception e){
                        Toast.makeText(SignInActivity.this, "Please enter your registered 10 digit phone number!!", Toast.LENGTH_LONG).show();   return;
                    }
                    if(number.toString().length()==10 && number.toString().charAt(0)!='0') {
                        mAuth.signInWithEmailAndPassword(number.toString()+"@gmail.com",binding.edtPassword.getText().toString().trim())
                                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                    @Override
                                    public void onComplete(@NonNull Task<AuthResult> task) {
                                        if(task.isSuccessful()){
                                            Toast.makeText(SignInActivity.this, "Sign in successful", Toast.LENGTH_SHORT).show();
                                            startActivity(new Intent(SignInActivity.this, MainActivity.class));
                                        }else{
                                            Toast.makeText(SignInActivity.this, task.getException().getMessage(), Toast.LENGTH_LONG).show();
                                        }
                                    }
                                });
                    }else{
                        Toast.makeText(SignInActivity.this, "Please enter your registered 10 digit phone number!!", Toast.LENGTH_LONG).show();  return;
                    }
                }catch (Exception e){
                    Toast.makeText(SignInActivity.this, "Something went wrong, please try again!", Toast.LENGTH_LONG).show();
                }finally {
//                    binding.progressBar.setVisibility(View.INVISIBLE);
//                    binding.parentLayout.setBackgroundColor(getResources().getColor(R.color.bgColor));
                }
            }
        });

        binding.tvDontHaveAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(SignInActivity.this, SignUpActivity.class));
            }
        });
    }
}