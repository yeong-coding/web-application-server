package webserver.controller;

import db.DataBase;
import model.User;
import webserver.http.HttpRequest;
import webserver.http.HttpResponse;

import java.io.IOException;

public class CreateUserController extends AbstratController {

    @Override
    protected void doGet(HttpRequest request, HttpResponse response) throws IOException {

        response.forward("/user/form.html");
    }

    @Override
    public void doPost(HttpRequest request, HttpResponse response) throws IOException {

        String userId=request.getParameter("userId");
        String password=request.getParameter("password");
        String name=request.getParameter("name");
        String email=request.getParameter("email");

        DataBase.addUser(new User(userId, password, name, email));
        response.sendRedirect("/index.html");
    }
}
