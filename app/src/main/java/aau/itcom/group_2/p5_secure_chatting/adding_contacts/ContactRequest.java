package aau.itcom.group_2.p5_secure_chatting.adding_contacts;

import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;

import aau.itcom.group_2.p5_secure_chatting.create_account.User;

public class ContactRequest {



    String contactRequestID;
    Contact contact;
    String message;
    String userID;
    User user;
    FirebaseUser firebaseUser;
    FirebaseDatabase database;




    public ContactRequest(Contact contact) {
        this.contactRequestID = contact.getId();
        this.contact = contact;
        this.message = contact.getName() + " " + contact.getLastName() + " would like to add you to his contacts";
    }
    public ContactRequest(){

    }


    public void updateUI(){

    }

    public Contact getContact() {
        return contact;
    }

    public void setContact(Contact contact) {
        this.contact = contact;
    }

    public String getContactRequestID() {
        return contactRequestID;
    }

    public void setContactRequestID(String contactRequestID) {
        this.contactRequestID = contactRequestID;
    }
    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
