package webserver.controller;

import db.DataBase;
import model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import webserver.http.HttpRequest;
import webserver.http.HttpResponse;
import webserver.http.HttpSession;

import javax.servlet.RequestDispatcher;
import javax.servlet.annotation.WebServlet;
import java.io.IOException;
import java.util.Collection;

//@WebServlet("/user/list")
public class ListUserController extends AbstratController {

    private static final Logger log=LoggerFactory.getLogger(ListUserController.class);

    @Override
    public void doGet(HttpRequest request, HttpResponse response) throws IOException {

        if(isLogin(request.getSession())){

            response.forward("/user/list.jsp");
            return;
        }

        response.forward("/user/login.html");
    }

    public boolean isLogin(HttpSession session){
        Object user=session.getAttribute("user");
        if(user==null){
            return false;
        }
        return true;
    }
}
