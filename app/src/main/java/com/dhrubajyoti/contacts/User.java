package com.dhrubajyoti.contacts;

import java.util.ArrayList;

public class User {
    private String contact;
    private String password;
    private ArrayList<Contact> contacts;

    public User(String contact, String password, ArrayList<Contact> contacts) {
        this.contact = contact;
        this.password = password;
        this.contacts = contacts;
    }

    public User(String contact, String password) {
        this.contact = contact;
        this.password = password;
    }

    public User() {
    }

    public String getContact() {
        return contact;
    }

    public void setContact(String contact) {
        this.contact = contact;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public ArrayList<Contact> getContacts() {
        return contacts;
    }

    public void setContacts(ArrayList<Contact> contacts) {
        this.contacts = contacts;
    }
}
