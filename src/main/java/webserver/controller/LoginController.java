package webserver.controller;

import db.DataBase;
import model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import webserver.HttpRequest;
import webserver.RequestHandler;

import java.io.IOException;

public class LoginController extends AbstratController{

    private static final Logger log = LoggerFactory.getLogger(LoginController.class);

    @Override
    public void doPost(HttpRequest request, HttpResponse response) throws IOException {

        String userId=request.getParameter("userId");
        String password=request.getParameter("password");

        User user=DataBase.findUserById(userId);

        if(user!=null && userId.equals(user.getUserId()) && password.equals(user.getPassword())){
            log.debug("로그인 성공!!!!!!");
            response.addHeader("Set-Cookie", "logined=true");
            response.sendRedirect("/index.html");
        }
        log.debug("로그인 실패!!!!!!");
        response.addHeader("Set-Cookie", "logined=false");
        response.forward("/user/login_failed.html");

    }
}
