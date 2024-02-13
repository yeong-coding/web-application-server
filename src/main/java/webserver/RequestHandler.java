package webserver;

import java.io.*;
import java.net.Socket;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import util.HttpRequestUtils;
import webserver.controller.Controller;
import webserver.controller.RequestMapping;
import webserver.http.HttpRequest;
import webserver.http.HttpResponse;
import webserver.http.HttpSession;
import webserver.http.HttpSessions;

public class RequestHandler extends Thread {
    private static final Logger log = LoggerFactory.getLogger(RequestHandler.class);

    private Socket connection;
    private static List<User> userList;

    public RequestHandler(Socket connectionSocket) {
        this.connection = connectionSocket;
    }

    public void run() {
        log.debug("New Client Connect! Connected IP : {}, Port : {}", connection.getInetAddress(),
                connection.getPort());

        try (InputStream in = connection.getInputStream(); OutputStream out = connection.getOutputStream()) {
            // TODO 사용자 요청에 대한 처리는 이 곳에 구현하면 된다.
            log.info("connection: {}", connection.toString());

            HttpRequest request=new HttpRequest(in);
            HttpResponse response=new HttpResponse(out);

            if(request.getCookies().getCookie("JSESSIONID")==null){
                String jsessionid=UUID.randomUUID().toString();
                HttpSessions.getSession(jsessionid);
                response.addHeader("Set-Cookie", "JSESSIONID=" + jsessionid);
                log.info("jsessionid: {}", jsessionid);
            }

            Controller controller= RequestMapping.getController(request.getPath());
            log.info("request.getPath: {}", request.getPath());

            if(controller==null){
                String path=getDefaultPath(request.getPath());
                response.forward(path);
            }
            else {
                log.info("controller: {}", request.getPath());
                controller.service(request, response);
            }

        } catch (IOException e) {
            log.error(e.getMessage());
            throw new RuntimeException(e);
        }
    }

    private String getDefaultPath(String path) {

        if(path.equals("/")){
            return "/index.html";
        }
        return path;
    }
}
