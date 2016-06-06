package com.theironyard.javawithclojure.jhporter;

import java.util.ArrayList;

/**
 * Created by jeffryporter on 6/6/16.
 */
public class User
{
    String name;
    String password;
    ArrayList<Messages> messages;

    public User(String name, String password)
    {
        this.name = name;
        this.password= password;
        messages = new ArrayList<>();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void addPost(String blogpost)
    {
        Messages message = new Messages(blogpost);
        messages.add(message);
    }

    public void removePost(Messages post)
    {
        messages.remove(post);
    }
}
