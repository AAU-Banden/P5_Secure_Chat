package aau.itcom.group_2.p5_secure_chatting.local_database;

import java.util.List;

import aau.itcom.group_2.p5_secure_chatting.adding_contacts.Contact;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

@Dao
public interface ContactDAO {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public void insertContacts(Contact... contacts);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public void insertContact(Contact contact);


    @Update
    public void updataContacts(Contact... contacts);

    @Delete
    public void deleteContacts(Contact... contacts);


    @Query("SELECT * FROM contacts")
    public List<Contact> loadAllContacts();

    @Query("SELECT * FROM contacts WHERE id LIKE :id")
    public Contact loadContactWithId(String id);

    @Query("DELETE FROM contacts")
    public void nukeTable();

}
