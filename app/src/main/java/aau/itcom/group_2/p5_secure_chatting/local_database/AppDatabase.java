package aau.itcom.group_2.p5_secure_chatting.local_database;

import android.content.Context;

import aau.itcom.group_2.p5_secure_chatting.adding_contacts.Contact;
import aau.itcom.group_2.p5_secure_chatting.chatting.Message;
import aau.itcom.group_2.p5_secure_chatting.key_creation.Key;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

@Database(entities = {Contact.class, Message.class, Key.class}, version = 2)
public abstract class AppDatabase extends RoomDatabase {
    final static String DB_NAME = "app_database";
    private static AppDatabase instance;

    public static synchronized AppDatabase getInstance(Context context){
        if (instance == null){
            instance = Room.databaseBuilder(context.getApplicationContext(), AppDatabase.class, DB_NAME)
                    .fallbackToDestructiveMigration()
                    .allowMainThreadQueries() // not recommended - should use another thread (AsyncTask or LiveData)
                    .build();
        }
        return instance;
    }

    public abstract ContactDAO getContactDAO();
    public abstract MessageDAO getMessageDAO();
    public abstract KeyDAO getKeyDAO();


}

