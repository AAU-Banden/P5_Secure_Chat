package aau.itcom.group_2.p5_secure_chatting.local_database;

import java.util.List;

import aau.itcom.group_2.p5_secure_chatting.chatting.Message;
import aau.itcom.group_2.p5_secure_chatting.key_creation.Key;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

@Dao
public interface KeyDAO {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public void insertKey(Key key);

    @Delete
    public void deleteKeys(Key... keys);


    @Query("SELECT * FROM keys")
    public List<Key> loadAllKeys();

    @Query("SELECT * FROM keys WHERE id LIKE :id")
    public Key loadKeyWithId(int id);


    @Query("SELECT * FROM keys WHERE keyName LIKE :keyName")
    public Key loadKeyWithKeyName(String keyName);




    @Query("DELETE FROM keys")
    public void nukeTable();
}
