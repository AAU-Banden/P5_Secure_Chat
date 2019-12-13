package aau.itcom.group_2.p5_secure_chatting.local_database;

import java.util.List;

import aau.itcom.group_2.p5_secure_chatting.adding_contacts.Contact;
import aau.itcom.group_2.p5_secure_chatting.chatting.Message;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

@Dao
public interface MessageDAO {
    @Insert (onConflict = OnConflictStrategy.REPLACE)
    public void insertMessage(Message message);


    @Delete
    public void deleteMessages(Message... messages);


    @Query("SELECT * FROM messages")
    public List<Message> loadAllMessages();

    @Query("SELECT * FROM messages WHERE id LIKE :id")
    public List<Message> loadMessagesWithId(String id);

    @Query("SELECT * FROM messages WHERE idOfSender LIKE :id")
    public List<Message> loadMessagesWithIdOfContact(String id);

    @Query("DELETE FROM messages")
    public void nukeTable();
}
