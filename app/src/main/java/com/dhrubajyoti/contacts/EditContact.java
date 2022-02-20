package com.dhrubajyoti.contacts;

import static com.dhrubajyoti.contacts.MainActivity.contacts;
import static com.dhrubajyoti.contacts.MainActivity.database;
import static com.dhrubajyoti.contacts.MainActivity.mAuth;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.dhrubajyoti.contacts.databinding.ActivityEditContactBinding;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class EditContact extends AppCompatActivity {
    private ActivityEditContactBinding binding;
    private Contact contact = new Contact();
    String contactId = null;
    int position = -1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityEditContactBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setContentView(binding.getRoot());
        getSupportActionBar().hide();
        binding.contactTB.inflateMenu(R.menu.edit_menu);

        contactId = getIntent().getStringExtra("contactId");
        for(Contact c:contacts){
            if((c.getName()+"_"+c.getNumber()).equals(contactId)){
                contact = c;
                binding.edtName.setText(contact.getName());
                binding.edtPhone.setText(contact.getNumber());
                break;
            }
        }
        if(contact == null){
            Toast.makeText(this, "Something wrong happened", Toast.LENGTH_LONG).show();
            onBackPressed();
        }
        binding.btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        binding.btnSaveContact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveContact();
            }
        });

        binding.contactTB.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                if(item.getItemId() == R.id.btnDeleteContact){
                    new android.app.AlertDialog.Builder(EditContact.this).setTitle("Delete contact").setMessage("Are you sure to delete?")
                            .setNegativeButton("Delete", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    new ContactActivity().deleteContact(EditContact.this, contactId);
                                    startActivity(new Intent(EditContact.this, MainActivity.class));
                                }
                            }).setPositiveButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                        }
                    }).create().show();
                }
                return false;
            }
        });
    }

    private void saveContact(){
        String name = binding.edtName.getText().toString().trim();
        String number = binding.edtPhone.getText().toString().trim();
        for(Contact c: contacts){
            if((c.getName()+"_"+c.getNumber()).equals(name+"_"+number)){
                new AlertDialog.Builder(this).setMessage(name.equals("")?number:name+" already exists!")
                        .setNegativeButton("Dismiss", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                            }
                        }).create().show();
                return;
            }
        }
        if(!name.isEmpty() || !number.isEmpty()){
            try {
                database.getReference().child("Users").child(mAuth.getCurrentUser().getUid()).child("Contacts").child(contactId).removeValue()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        database.getReference().child("Users").child(mAuth.getCurrentUser().getUid()).child("Contacts").child(name+"_"+number).setValue(new Contact(name,number));
                        Toast.makeText(EditContact.this, name.equals("")?number:name +" saved", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(EditContact.this, ContactActivity.class).putExtra("contactId", name+"_"+number));
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(EditContact.this, "Something wrong happened, please try again", Toast.LENGTH_SHORT).show();
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
        startActivity(new Intent(this, ContactActivity.class).putExtra("contactId",contact.getName() + "_" + contact.getNumber()));
    }
}