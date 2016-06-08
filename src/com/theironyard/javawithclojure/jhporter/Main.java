package com.theironyard.javawithclojure.jhporter;

import jodd.json.JsonParser;
import jodd.json.JsonSerializer;
import spark.ModelAndView;
import spark.Session;
import spark.Spark;
import spark.template.mustache.MustacheTemplateEngine;


import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

import static spark.Spark.staticFileLocation;

public class Main {

    public static final String FILE_LOCATION = "Users.json";

    static HashMap<String, User> userMap = new HashMap<>();
    static ArrayList<User> users = new ArrayList<>();

    Scanner input;

    public static void main(String[] args)
    {
        //loadUserData(FILE_LOCATION);
        staticFileLocation("/public");
        Spark.init();
        Spark.get(
                "/",
                (request, response) -> {
                    Session session = request.session();
                    String name = session.attribute("currentUserName");
                    User user = userMap.get(name);

                    if (user == null)
                    {
                        return new ModelAndView(null, "index.html");
                    }
                    else
                    {
                        return new ModelAndView(user, "messages.html");
                    }
                },
                new MustacheTemplateEngine()
        );
        Spark.post(
                "/create-user",
                (request,response) -> {
                    String username = request.queryParams("username");
                    String password = request.queryParams("password");
                    Session session = request.session();
                    User user;
                    if (username.isEmpty() || password.isEmpty())
                    {
                        response.redirect("/");
                        return "";
                    }

                    if (!userMap.containsKey(username))
                    {
                        user = new User(username, password);
                        userMap.put(username, user);
                        //users.add(userMap.get(username));
                        saveUsers(FILE_LOCATION);
                    }
                    if (userMap.get(username).getPassword().equals(password))
                    {
                        session.attribute("currentUserName", username);
                    }

                    response.redirect("/");
                    return "";
                }
        );
        Spark.post(
                "/create-message",
                (request,response) -> {
                    String blogpost = request.queryParams("blog");
                    Session session = request.session();
                    String username = session.attribute("currentUserName");

                    if (!blogpost.isEmpty())
                    {
                        userMap.get(username).addPost(blogpost);
                        //users.add(userMap.get(username));
                        saveUsers(FILE_LOCATION);
                    }
                    blogpost = null;


                    response.redirect("/");
                    return "";
                }
        );
        Spark.post(
                "/logout",
                (request, response) -> {
                    response.redirect("/");
                    Session session = request.session();
                    session.invalidate();
                    return"";
                }
        );
//        Spark.post(
//                "/delete-message",
//                (request, response) -> {
//                    int id;
//                    int messageId;
//                    Session session = request.session();
//                    String username = session.attribute("currentUserName");
//                    messageId = Integer.valueOf(request.queryParams("messageId"));
//                    User user = userMap.get(username);
//                    id = searchByid(messageId, user.messages);
//                    user.messages.remove(id);
//
//                    response.redirect("/");
//                    return "";
//
//                }
//        );
        Spark.post(
                "/update-message",
                (request, response) -> {
                    int id;
                    int messageId;
                    String newMessage;
                    Session session = request.session();
                    String username = session.attribute("currentUserName");
                    messageId = Integer.valueOf(request.queryParams("messageId"));
                    newMessage = request.queryParams("newMessage");
                    User user = userMap.get(username);
                    id = searchByid(messageId, user.messages);
                    if (newMessage.isEmpty())
                    {
                        user.messages.remove(id);
                        saveUsers(FILE_LOCATION);
                    }
                    else
                    {
                        user.messages.get(id).setMessage(newMessage);
                        saveUsers(FILE_LOCATION);

                    }

                    response.redirect("/");
                    return "";
                }
        );

    }

    public static int searchByid(int idTag, ArrayList<Message> messages)
    {

        int min = 0;
        int max = messages.size();
        boolean isFound = false;
        int mid;
        int idLocation=-1;
        while(!isFound && max>=min)
        {
            mid = (min+max)/2;
            if (messages.get(mid).id == idTag)
            {
                idLocation = mid;
                isFound = true;
            }
            else
            {
                if (messages.get(mid).id > idTag)
                {
                    max = mid - 1;
                }
                else if (messages.get(mid).id < idTag)
                {
                    min = mid + 1;
                }
            }
        }
        return idLocation;
    }

    public static void saveUsers(String fileLoc)
    {
        JsonSerializer serializer = new JsonSerializer();
        String json = serializer.include("*").serialize(userMap);
        File f = new File(fileLoc);
        try
        {
            FileWriter fw = new FileWriter(f);
            fw.write(json);
            fw.close();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    public static void loadUserData(String fileLoc)
    {
        HashMap<String, User> map = new HashMap<>();
        File f = new File(fileLoc);
        try
        {
            Scanner scanner = new Scanner(f);
            scanner.useDelimiter("\\Z");
            String contents = scanner.next();
            JsonParser parser = new JsonParser();
            Map<String, User> contentsMap = parser.parse(contents);
            for (Map.Entry<String, User> h : contentsMap.entrySet())
            {
               userMap.put((String)h.getKey(), new User((String)h.getValue().name,(String)h.getValue().password,
                       (ArrayList<Message>)h.getValue().messages));
            }
            System.out.println();
        }
        catch (FileNotFoundException e)
        {

        }

        //return map;
    }
}
