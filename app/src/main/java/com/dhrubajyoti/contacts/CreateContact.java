package com.dhrubajyoti.contacts;

import static com.dhrubajyoti.contacts.MainActivity.adapter;
import static com.dhrubajyoti.contacts.MainActivity.contacts;
import static com.dhrubajyoti.contacts.MainActivity.mAuth;
import static com.dhrubajyoti.contacts.MainActivity.database;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.dhrubajyoti.contacts.databinding.ActivityCreateContactBinding;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

public class CreateContact extends AppCompatActivity {

    private ActivityCreateContactBinding binding;
    private Contact contact = new Contact();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCreateContactBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        getSupportActionBar().hide();

        binding.btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(CreateContact.this);
                builder.setMessage("Your changes have not been saved").setPositiveButton("Save", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        saveContact();
                    }
                }).setNegativeButton("Discard", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        onBackPressed();
                    }
                });
                if(binding.edtName.getText().toString().equals("") && binding.edtNumber.getText().toString().equals("")){
                    onBackPressed();
                }else{
                    builder.create().show();
                }
            }
        });
        binding.btnSaveContact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveContact();
            }
        });

    }

    private void saveContact(){
        contact.setName(binding.edtName.getText().toString().trim());
        contact.setNumber(binding.edtNumber.getText().toString().trim());
        String contactId = contact.getName() + "_" + contact.getNumber();
        for(Contact c: contacts){
            if((c.getName()+"_"+c.getNumber()).equals(contactId)){
                new AlertDialog.Builder(this).setTitle(contact.getName()+" already exists!")
                        .setNegativeButton("Dismiss", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                            }
                        }).create().show();
                return;
            }
        }
        if(!contact.getName().isEmpty() || !contact.getNumber().isEmpty()){
            try {
                database.getReference().child("Users").child(mAuth.getCurrentUser().getUid()).child("Contacts").child(contactId).setValue(contact).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Toast.makeText(CreateContact.this, contact.getName().equals("")?contact.getNumber():contact.getName() +" saved", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(CreateContact.this, ContactActivity.class).putExtra("contactId", contactId));
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(CreateContact.this, "Something went wrong, please try again.", Toast.LENGTH_SHORT).show();
                    }
                });
            }catch (Exception e){
                Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
            }
        }else{
            Toast.makeText(this, "Contact must have a name or number", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(this,MainActivity.class));
    }
}