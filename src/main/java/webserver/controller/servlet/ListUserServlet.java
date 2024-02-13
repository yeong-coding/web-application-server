package webserver.controller.servlet;

import db.DataBase;
import model.User;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet("/user/list")
public class ListUserServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
//        DataBase.addUser(new User("hello", "dddd", "hello-name", "hello@gmail.com"));
//        DataBase.addUser(new User("hello2", "dddd2", "hello-name2", "hello2@gmail.com"));
        req.setAttribute("users", DataBase.findAll());
        RequestDispatcher rd=req.getRequestDispatcher("/user/list.jsp");
        rd.forward(req, resp);
    }

}
