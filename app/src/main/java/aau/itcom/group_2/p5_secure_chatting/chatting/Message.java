package aau.itcom.group_2.p5_secure_chatting.chatting;
import android.os.Build;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;


public class Message {
    String message;
    String sessionKey = null;
    String time;
    String id ;

    String idOfSender;

    LocalDateTime localDataTime;
    DateTimeFormatter dateTimeFormatter;



    public Message(String message, String sessionKey, String idOfSender) {
        this.message = message;
        this.sessionKey = sessionKey;
        this.idOfSender = idOfSender;
        this.time = getTime();
        this.id = UUID.randomUUID().toString();

    }
    public Message(){

    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTime (){
        localDataTime = LocalDateTime.now();
        dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        time = localDataTime.format(dateTimeFormatter);

        return time;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }



    public String getSessionKey() {
        return sessionKey;
    }

    public void setSessionKey(String sessionKey) {
        this.sessionKey = sessionKey;
    }

    public void setTime(String time) {
        this.time = time;
    }


    public String getIdOfSender() {
        return idOfSender;
    }

    public void setIdOfSender(String idOfSender) {
        this.idOfSender = idOfSender;
    }

}
