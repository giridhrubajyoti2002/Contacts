package com.dhrubajyoti.contacts;

import static com.dhrubajyoti.contacts.MainActivity.adapter;
import static com.dhrubajyoti.contacts.MainActivity.contacts;
import static com.dhrubajyoti.contacts.MainActivity.mAuth;
import static com.dhrubajyoti.contacts.MainActivity.database;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.provider.ContactsContract;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class ContactsAdapter extends RecyclerView.Adapter<ContactsAdapter.ViewHolder> {
    private Context mContext;

    public ContactsAdapter(Context mContext) {
        this.mContext = mContext;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.model_contact, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.name.setText(MainActivity.getName(position));
        holder.contactModel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mContext.startActivity(new Intent(mContext,ContactActivity.class)
                        .putExtra("contactId",contacts.get(holder.getAdapterPosition()).getName() + "_" + contacts.get(holder.getAdapterPosition()).getNumber()));
            }
        });
        holder.btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new AlertDialog.Builder(mContext).setTitle("Delete contact").setMessage("Are you sure to delete "+MainActivity.getName(holder.getAdapterPosition())+"?")
                        .setNegativeButton("Delete", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                new ContactActivity().deleteContact(mContext,contacts.get(holder.getAdapterPosition()).getName()+"_"+contacts.get(holder.getAdapterPosition()).getNumber());
                            }
                        }).setPositiveButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                            }
                        }).create().show();
            }
        });
        if(!contacts.get(position).getName().equals("") && Character.isAlphabetic(contacts.get(position).getName().charAt(0))){
            int res = mContext.getResources()
                    .getIdentifier("alphabet_"+Character.toLowerCase(contacts.get(position).getName().charAt(0)),"drawable",mContext.getPackageName());
            Picasso.get().load(res).into(holder.icon);
        }
    }

    @Override
    public int getItemCount() {
        return contacts.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder{
        private ImageView icon, btnDelete;
        private TextView name;
        private RelativeLayout contactModel;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.contactNameTV);
            icon = itemView.findViewById(R.id.iconIV);
            btnDelete = itemView.findViewById(R.id.btnDelete);
            contactModel = itemView.findViewById(R.id.contactModel);


        }
    }
}
