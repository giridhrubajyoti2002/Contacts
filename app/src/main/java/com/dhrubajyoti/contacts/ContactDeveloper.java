package com.dhrubajyoti.contacts;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.dhrubajyoti.contacts.databinding.ActivityContactDeveloperBinding;

public class ContactDeveloper extends AppCompatActivity {
    private ActivityContactDeveloperBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityContactDeveloperBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        getSupportActionBar().setTitle("Contact Developer");

        binding.btnDeveloper.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_VIEW)
//                        .setData(Uri.parse("dhrubajyotigiri2002@gmail.com"))
//                        .setClassName("com.google.android.gm", "com.google.android.gm.ComposeActivityGmail")
//                        .setPackage("com.google.android.gm")
//                        .setType("message/rfc822")
//                        .putExtra(Intent.EXTRA_EMAIL,R.string.developer_email)
                        .setData(Uri.parse("mailto:" + "dhrubajyotigiri2002@gmail.com"));
                        startActivity(intent);
            }
        });
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(this, MainActivity.class));
    }
}