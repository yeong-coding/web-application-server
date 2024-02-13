package webserver.controller;

import db.DataBase;
import model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import webserver.http.HttpRequest;
import webserver.http.HttpResponse;
import webserver.http.HttpSession;

import java.io.IOException;

public class LoginController extends AbstratController{

    private static final Logger log = LoggerFactory.getLogger(LoginController.class);

    @Override
    protected void doGet(HttpRequest request, HttpResponse response) throws IOException {
        response.forward("/user/login.html");
    }

    @Override
    public void doPost(HttpRequest request, HttpResponse response) throws IOException {

        User user=DataBase.findUserById(request.getParameter("userId"));

        if(user!=null){
            if(user.login(request.getParameter("password"))){
                log.debug("로그인 성공!!!!!!");
                HttpSession session=request.getSession();
                session.setAttribute("user", user);
                response.sendRedirect("/user/list");
                return;
            }
        }

        log.debug("로그인 실패!!!!!!");
        response.forward("/user/login_failed.html");

    }
}
