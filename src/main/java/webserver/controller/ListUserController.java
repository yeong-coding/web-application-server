package webserver.controller;

import db.DataBase;
import model.User;
import webserver.http.HttpRequest;
import webserver.http.HttpResponse;

import java.io.IOException;
import java.util.Collection;

public class ListUserController extends AbstratController {

    @Override
    public void doGet(HttpRequest request, HttpResponse response) throws IOException {

        if(!request.getHeader("Cookie").contains("logined=true;")){
            response.sendRedirect("/user/login.html");
            return;
        }

        Collection<User> users= DataBase.findAll();
        StringBuilder sb=new StringBuilder();
        sb.append("<table border='1'>");

        for(User user: users){
            sb.append("<tr>");
            sb.append("<td>"+user.getUserId()+"</td>");
            sb.append("<td>"+user.getName()+"</td>");
            sb.append("<td>"+user.getEmail()+"</td>");
            sb.append("</tr>");
        }

        sb.append("</table>");
        response.forwardBody(sb.toString());
    }
}
