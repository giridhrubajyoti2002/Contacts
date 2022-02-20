package com.dhrubajyoti.contacts;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.dhrubajyoti.contacts.databinding.ActivityMainBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    public static FirebaseDatabase database;
    public static FirebaseAuth mAuth;
    public static ArrayList<Contact> contacts = new ArrayList<>();
    public static ContactsAdapter adapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        getSupportActionBar().hide();
        binding.mainActivityTB.inflateMenu(R.menu.main_menu);

        database = FirebaseDatabase.getInstance();
        mAuth = FirebaseAuth.getInstance();

        if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.INTERNET) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.INTERNET}, 1);
        }

        if(mAuth.getCurrentUser()==null){
            startActivity(new Intent(this,SignUpActivity.class));
        }else{
            loadData();
        }

        binding.swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                loadData();
                binding.swipeRefreshLayout.setRefreshing(false);
            }
        });

        adapter = new ContactsAdapter(this);
        binding.contactsRV.setAdapter(adapter);
        binding.contactsRV.setLayoutManager(new LinearLayoutManager(this));


        binding.btnCreateContact.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            startActivity(new Intent(MainActivity.this, CreateContact.class));
        }
    });

        binding.mainActivityTB.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                if(item.getItemId() == R.id.btnShareApp){
                    startActivity(new Intent().setAction(Intent.ACTION_SEND).setType("text/plain")
                            .putExtra(Intent.EXTRA_TEXT,"Hey check out my app at: https://play.google.com/store/apps/details?id=" + getApplicationContext().getPackageName()));
                }else if(item.getItemId() == R.id.btnLogOut){
                    mAuth.signOut();
                    startActivity(new Intent(MainActivity.this,SignInActivity.class));
                }else if(item.getItemId() == R.id.btnContactDeveloper){
                    startActivity(new Intent(MainActivity.this, ContactDeveloper.class));
                }
                return false;
            }
        });
    }

    public void loadData(){
        if(((ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE)).getActiveNetworkInfo() != null) {
            database.getReference().child("Users").child(mAuth.getCurrentUser().getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.hasChild("Contacts")) {
                        try {
                            database.getReference().child("Users").child(mAuth.getCurrentUser().getUid()).child("Contacts")
                                    .addValueEventListener(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                                            contacts.clear();
                                            if (snapshot != null) {
                                                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                                                    contacts.add(dataSnapshot.getValue(Contact.class));
                                                }
                                            }
                                            Collections.sort(contacts, new Comparator<Contact>() {
                                                public int compare(Contact c1, Contact c2) {
                                                    if (c1.getName().length() == 0) {
                                                        return 1;
                                                    } else if (c2.getName().length() == 0) {
                                                        return -1;
                                                    } else if (Character.isDigit(c1.getName().charAt(0)) == Character.isDigit(c2.getName().charAt(0))) {
                                                        return c1.getName().compareToIgnoreCase(c2.getName());
                                                    } else {
                                                        return -1 * c1.getName().compareToIgnoreCase(c2.getName());
                                                    }
                                                }
                                            });
                                            adapter.notifyDataSetChanged();
                                        }
                                        @Override
                                        public void onCancelled(@NonNull DatabaseError error) {
                                            Toast.makeText(MainActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
                                        }
                                    });
                        } catch (Exception e) {
                            Toast.makeText(MainActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
                            return;
//                          alertDialog.getButton(DialogInterface.BUTTON_NEGATIVE).performClick();
                        }
                    } else {
                        new AlertDialog.Builder(MainActivity.this).setTitle("No saved contact")
                                .setNegativeButton("Dismiss", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                    }
                                }).setPositiveButton("Create New", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                startActivity(new Intent(MainActivity.this, CreateContact.class));
                            }
                        }).setCancelable(false).create().show();
                    }
                }
                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                }
            });
        }else{
            new AlertDialog.Builder(this).setTitle("No Internet").setMessage("Please connect to the internet").setCancelable(false)
                    .setPositiveButton("Connect", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            startActivity(new Intent(Settings.ACTION_WIFI_SETTINGS).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
                            new AlertDialog.Builder(MainActivity.this).setMessage("Please refresh the page").setCancelable(false)
                                    .setPositiveButton("Refresh", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                            loadData();
                                        }
                                    }).create().show();
                        }
                    }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    finish();
                }
            }).create().show();
        }
    }


    public static String getName(int position){
        return contacts.get(position).getName().equals("")?contacts.get(position).getNumber():contacts.get(position).getName();
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }
}