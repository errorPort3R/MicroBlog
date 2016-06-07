package com.theironyard.javawithclojure.jhporter;

import spark.ModelAndView;
import spark.Session;
import spark.Spark;
import spark.template.mustache.MustacheTemplateEngine;


import java.util.HashMap;

import static spark.Spark.staticFileLocation;

public class Main {


    static HashMap<String, User> userMap = new HashMap<>();

    public static void main(String[] args)
    {
        staticFileLocation("/public");
        Spark.init();
        Spark.get(
                "/",
                (request, response) -> {
                    Session session = request.session();
                    String name = session.attribute("currentUserName");
                    User user = userMap.get(name);
                    //http://localhost:4567/style.css
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
                    if (!userMap.containsKey(username))
                    {
                        user = new User(username, password);
                        userMap.put(username, user);
                    }
                    if (password.equals(userMap.get(username).password))
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
                    session.attribute("currentUserName", null);
                    return"";
                }
        );
    }
}
