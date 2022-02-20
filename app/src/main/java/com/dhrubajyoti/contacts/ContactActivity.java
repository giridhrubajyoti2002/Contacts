package com.dhrubajyoti.contacts;

import static com.dhrubajyoti.contacts.MainActivity.contacts;
import static com.dhrubajyoti.contacts.MainActivity.database;
import static com.dhrubajyoti.contacts.MainActivity.mAuth;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.dhrubajyoti.contacts.databinding.ActivityContactBinding;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;

public class ContactActivity extends AppCompatActivity {
    private ActivityContactBinding binding;
    private Contact contact = new Contact();
    private String contactId = null;
    final int PERMISSION_CODE = 1;
    int position = -1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityContactBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        getSupportActionBar().hide();
        binding.contactTB.inflateMenu(R.menu.contact_menu);

        contactId = getIntent().getStringExtra("contactId");
        for(int i = 0; i<contacts.size(); i++){
            Contact c = contacts.get(i);
            if((c.getName()+"_"+c.getNumber()).equals(contactId)){
                contact = contacts.get(i);
                position = i;
                binding.contactNameTV.setText(MainActivity.getName(i));
                if(!contact.getNumber().equals("")) {
                    binding.numberTV.setText(contact.getNumber());
                }
                break;
            }
        }
        if(contact == null){
            Toast.makeText(this, "Something wrong happened", Toast.LENGTH_LONG).show();
            onBackPressed();
        }

        binding.btnCall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                call();
            }
        });
        binding.btnEditContact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(ContactActivity.this, EditContact.class).putExtra("contactId", contactId));
            }
        });
        binding.btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(ContactActivity.this, MainActivity.class));
            }
        });

        binding.contactTB.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                if(item.getItemId() == R.id.btnDeleteContact){
                    new AlertDialog.Builder(ContactActivity.this).setTitle("Delete contact").setMessage("Are you sure to delete "+MainActivity.getName(position)+"?")
                            .setNegativeButton("Delete", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    deleteContact(ContactActivity.this, contactId);
                                    startActivity(new Intent(ContactActivity.this, MainActivity.class));
                                }
                            }).setPositiveButton("Cancel", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                }
                        }).create().show();
                }else if(item.getItemId() == R.id.btnShareContact){
                    Intent sendIntent = new Intent()
                            .setAction(Intent.ACTION_SEND)
                            .putExtra(Intent.EXTRA_TEXT,
                                    contact.getName().equals("")?"":("Name : "+contact.getName()+"\n")
                                            + (contact.getNumber().equals("")?"":("Number : "+contact.getNumber())))
                            .setType("text/plain");
                    startActivity(sendIntent);
                }
                return false;
            }
        });

    }

    private void call(){
        if(!contact.getNumber().equals("")) {
            if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(ContactActivity.this, new String[]{Manifest.permission.CALL_PHONE}, PERMISSION_CODE);
            } else {
                try {
                    startActivity(new Intent(Intent.ACTION_DIAL).setData(Uri.parse("tel:" + contact.getNumber())));
                } catch (android.content.ActivityNotFoundException ex) {
                    Toast.makeText(getApplicationContext(), "Something wrong happened", Toast.LENGTH_SHORT).show();
                }
            }
        }else{
            startActivity(new Intent(ContactActivity.this, EditContact.class).putExtra("contactId", contactId));
        }
    }

    public void deleteContact(Context context, String contactId){
        Contact contact = null;
        for(Contact c:contacts){
            if((c.getName()+"_"+c.getNumber()).equals(contactId)){
                contact = c;
                break;
            }
        }
        if(contact!=null){
            try {
                Contact finalContact = contact;
                database.getReference().child("Users").child(mAuth.getCurrentUser().getUid()).child("Contacts").child(contactId).removeValue()
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                Toast.makeText(context, finalContact.getName().equals("")?finalContact.getNumber():finalContact.getName() +" deleted", Toast.LENGTH_SHORT).show();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(context, "Something went wrong, Please try again", Toast.LENGTH_SHORT).show();
                    }
                });;
            }catch (Exception e){
                Toast.makeText(context, e.getMessage(), Toast.LENGTH_LONG).show();
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode == PERMISSION_CODE){
            if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                call();
            }else{
                Toast.makeText(this, "Please give CALL permission to start a call", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).setData(Uri.parse("package:" + getApplicationContext().getPackageName())));
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.contact_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.btnDeleteContact:
//                deleteContact();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(this, MainActivity.class));
    }
}