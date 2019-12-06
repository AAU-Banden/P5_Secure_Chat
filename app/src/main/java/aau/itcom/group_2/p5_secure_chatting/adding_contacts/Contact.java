package aau.itcom.group_2.p5_secure_chatting.adding_contacts;

import java.util.ArrayList;
import java.util.List;

public class Contact {
    private String name;
    private String lastName;
    private String email;
    private String phoneNumber;
    private String id;


    public Contact(String name, String lastName, String email, String phoneNumber, String id) {
        this.name = name;
        this.lastName = lastName;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.id = id;
    }

    public Contact(){

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

    public String getid() {
        return id;
    }

    public void setID(String ID) {
        this.id = ID;
    }
}
