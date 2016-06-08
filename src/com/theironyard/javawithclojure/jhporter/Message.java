package com.theironyard.javawithclojure.jhporter;

/**
 * Created by jeffryporter on 6/6/16.
 */
public class Message
{
    private static int idIterator = 11010;
    String message;
    int id;

    public Message(String message) {
        this.message = message;
        id = idIterator;
        idIterator++;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public static int getIdIterator() {
        return idIterator;
    }

    public int getId() {
        return id;
    }
}
