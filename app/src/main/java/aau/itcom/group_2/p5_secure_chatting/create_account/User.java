package aau.itcom.group_2.p5_secure_chatting.create_account;

import java.util.List;

import aau.itcom.group_2.p5_secure_chatting.adding_contacts.Contact;
import aau.itcom.group_2.p5_secure_chatting.adding_contacts.ContactRequest;

public class User {
    private String name;
    private String lastName;
    private String email;
    private String phoneNumber;
    private String password;
    private String ID;
    private boolean isLoggedIn;
    private ContactRequest contactRequest;
    private List<Contact> contacts;


    public User() {
    }

    public User(String name, String lastName, String email, String phoneNumber, boolean isLoggedIn, String ID) {
        this.name = name;
        this.lastName = lastName;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.password = password;
        this.isLoggedIn = isLoggedIn;
        this.ID = ID;
    }



    public User(String name, String lastName, String email, String phoneNumber, String password,
                boolean isLoggedIn, String ID, ContactRequest contactRequest, List<Contact> contacts) {
        this.name = name;
        this.lastName = lastName;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.password = password;
        this.isLoggedIn = isLoggedIn;
        this.ID = ID;
        this.contactRequest = contactRequest;
        this.contacts = contacts;

    }
    public List<Contact> getContacts() {
        return contacts;
    }

    public void setContacts(List<Contact> contacts) {
        this.contacts = contacts;
    }

    public ContactRequest getContactRequest() {
        return contactRequest;
    }

    public void setContactRequest(ContactRequest contactRequest) {
        this.contactRequest = contactRequest;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getID() {
        return ID;
    }

    public void setID(String ID) {
        this.ID = ID;
    }

    public boolean isLoggedIn() {
        return isLoggedIn;
    }

    public void setLoggedIn(boolean loggedIn) {
        isLoggedIn = loggedIn;
    }
}
