package webserver;

import java.io.*;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import util.HttpRequestUtils;
import util.IOUtils;

import javax.xml.crypto.Data;

import static util.HttpRequestUtils.parseQueryString;

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

            BufferedReader br=new BufferedReader(new InputStreamReader(in));
            String request=br.readLine();
            log.info("access url: {}", request);

            if(request==null){
                log.info("request is null, return");
                return;
            }

            String file=null;
            String[] requests=request.split(" ");
            file=requests[1];

            String contentType=null;
            String line=br.readLine();
            int len=0;
            String cookie=null;

            while(line!=null && !line.equals("")){

                log.info("line: {}", line);

                String[] tokens=line.split(" |,");

                if("Accept:".equals(tokens[0])){
                    contentType=tokens[1]+",*/*;q=0.1";
                    log.info("accept: {}", tokens[1]);
                }

//                if("Accept-Language:".equals(tokens[0])){
//                    contentType+=tokens[1];
//                }

                if("Content-Length:".equals(tokens[0])){
                    len=Integer.parseInt(tokens[1]);
                    log.info("len: {}", len);
                }

                if("Cookie:".equals(tokens[0])){
                    cookie=tokens[1];
                }

                line=br.readLine();
            }

            // 페이지 이동
            if(isIndexPage(request)){
                responsePage(out, contentType, "./webapp/index.html");
            }

            if(isFormPage(request)){
                responsePage(out, contentType, "./webapp/user/form.html");
            }

            // 로그인 페이지
            if(isLoginPage(request)){
                responsePage(out, contentType, "./webapp/user/login.html");
            }

            // 사용자 리스트
            if(isUserListRequest(request)){
                if(isLogined(cookie)) {
                    responseUserListPage(out);
                }

                responsePage(out, contentType,"./webapp/user/login.html");

            }

            // 동작 수행
            if(isUserCreate(request)){
                addUser(parseQueryString(request));
                responseMsg(out, contentType, userList.get(userList.size()-1).toString());
            }

            // 회원가입
            if(isUserCreatePost(request)){
                addUser(parseBody(len, br));
                DataOutputStream dos=new DataOutputStream(out);
                byte[] data="hello".getBytes();
                response302Header(dos, data.length);
                responseBody(dos, data);
            }

            // 로그인
            if(isLoginRequest(request)){
                login(len, br, out);
            }

            responsePage(out, contentType, "./webapp/"+file);

//            DataOutputStream dos = new DataOutputStream(out);
//            byte[] body = "Hello World".getBytes();
//            response200Header(dos, body.length);
//            responseBody(dos, body);

        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }

    private void responseCss(BufferedReader br, OutputStream out) throws IOException {

        DataOutputStream dos=new DataOutputStream(out);
        String line=null;
        String accept=null;

        do{
            line=br.readLine();
            log.info("css response: {}", line);

            String[] tokens=line.split(" ");
            if("Accept:".equals(tokens[0])){
                accept=tokens[1];
                log.info("access info: {}", tokens[1]);
            }

        }while(line!=null || !line.equals(""));

        response200Header(dos, accept, accept.length());

    }

    private boolean isCss(String request) {

        String[] tokens=request.split(" ");

        if("GET".equals(tokens[0]) && "/css/styles.css".equals(tokens[1])){
            return true;
        }

        return false;
    }

    private void responseUserListPage(OutputStream out) throws IOException {

        StringBuilder sb=new StringBuilder();

        for(User user: userList){
            sb.append(user.toString());
        }

        String result=sb.toString();
        responseMsg(out, "text/html,*/*;q=0.1", result);
    }

    private void login(int len, BufferedReader br, OutputStream out) throws IOException {

        String body=IOUtils.readData(br, len);

        log.info("body : {}", body);

        Map<String, String> params=HttpRequestUtils.parseQueryString(body);
        String id=params.get("userId");
        String pwd=params.get("password");

        // 이런 계정 있는지 확인
        if(userList!=null) {

            for (User user : userList) {
                if (user.getUserId().equals(id) && user.getPassword().equals(pwd)) {
                    String page = "./webapp/index.html";
                    byte[] data = Files.readAllBytes(Paths.get(page));
                    responseLoginHeader(new DataOutputStream(out), data.length);
                    responseBody(new DataOutputStream(out), data);
                }
            }
        }

        String page="./webapp/user/login_failed.html";
        byte[] data=Files.readAllBytes(Paths.get(page));
        responseLoginFailHeader(new DataOutputStream(out), data.length);
        responseBody(new DataOutputStream(out), data);

    }

    private boolean isLogined(String cookie) {

        if(cookie!=null && "logined=true;".equals(cookie)){
            return true;
        }

        return false;
    }

    private boolean isUserCreatePost(String line) {

        String[] tokens=line.split(" ");

        if("POST".equals(tokens[0]) && "/user/create".equals(tokens[1])){
            return true;
        }

        return false;
    }

    private void responsePage(OutputStream out, String contentType, String page) throws IOException {
        log.info("contentType: {}", contentType);
        DataOutputStream dos = new DataOutputStream(out);
        byte[] body= Files.readAllBytes(Paths.get(page));
        response200Header(dos, contentType, body.length);
        responseBody(dos, body);
    }

    private void responseMsg(OutputStream out, String contentType, String msg) throws IOException {
        DataOutputStream dos = new DataOutputStream(out);
        byte[] body= msg.getBytes();
        response200Header(dos, contentType, body.length);
        responseBody(dos, body);
    }

    private User parseQueryString(String url){

        // ?까지 파싱
        int idx=url.indexOf("?");
        if(idx!=-1) {
            url = url.substring(idx + 1, url.length());
        }
        log.info("url: {}", url);

        Map<String, String> params=HttpRequestUtils.parseQueryString(url);
        String id=params.get("userId");
        String pwd=params.get("password");
        String nm=params.get("name");
        String mail=params.get("email");

        return new User(id, pwd, nm, mail);
    }

    private User parseBody(int len, BufferedReader br) throws IOException {

        log.info("parseBody");

        String body=IOUtils.readData(br, len);

        log.info("body : {}", body);

        Map<String, String> params=HttpRequestUtils.parseQueryString(body);
        String id=params.get("userId");
        String pwd=params.get("password");
        String nm=params.get("name");
        String mail=params.get("email");

        return new User(id, pwd, nm, mail);
    }

    private void addUser(User user){

        if(userList==null){
            userList=new ArrayList<>();
        }

        userList.add(user);
        log.info("add user: {}", userList.get(userList.size()-1).toString());

    }

    private boolean isUserCreate(String line) {

        String[] tokens=line.split(" ");

        if(tokens.length<2) return false;

        if("GET".equals(tokens[0]) && tokens[1].startsWith("/user/create?")){
            return true;
        }

        return false;
    }

    private boolean isIndexPage(String line){

        String[] tokens=line.split(" ");

        if("GET".equals(tokens[0]) && "/index.html".equals(tokens[1])) {
            return true;
        }

        return false;
    }

    private boolean isFormPage(String line){

        String[] tokens=line.split(" ");

        if("GET".equals(tokens[0]) && "/user/form.html".equals(tokens[1])){
            return true;
        }

        return false;
    }

    private boolean isLoginPage(String line){

        String[] tokens=line.split(" ");

        if("GET".equals(tokens[0]) && "/user/login.html".equals(tokens[1])){
            return true;
        }

        return false;
    }


    private boolean isLoginRequest(String line){

        String[] tokens=line.split(" ");

        if("POST".equals(tokens[0]) && "/user/login".equals(tokens[1])){
            return true;
        }

        return false;
    }

    private boolean isUserListRequest(String line){

        String[] tokens=line.split(" ");

        if("GET".equals(tokens[0]) && "/user/list".equals(tokens[1])){
            return true;
        }

        return false;
    }

    private void response200Header(DataOutputStream dos, String contentType, int lengthOfBodyContent) {
        try {
            dos.writeBytes("HTTP/1.1 200 OK \r\n");
            dos.writeBytes("Content-Type: "+contentType+"\r\n");
            log.info("content type: {}", contentType);
            dos.writeBytes("Content-Length: " + lengthOfBodyContent + "\r\n");
            dos.writeBytes("\r\n");
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }

    private void response200Header(DataOutputStream dos, int lengthOfBodyContent) {
        try {
            dos.writeBytes("HTTP/1.1 200 OK \r\n");
            dos.writeBytes("Content-Type: text/html;charset=utf-8\r\n");
            dos.writeBytes("Content-Length: " + lengthOfBodyContent + "\r\n");
            dos.writeBytes("\r\n");
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }

    private void responseLoginHeader(DataOutputStream dos, int lengthOfBodyContent) {
        try {
            dos.writeBytes("HTTP/1.1 200 OK \r\n");
            dos.writeBytes("Content-Type: text/html\r\n");
            dos.writeBytes("Content-Length: " + lengthOfBodyContent + "\r\n");
            dos.writeBytes("Set-Cookie: logined=true\r\n");
            dos.writeBytes("\r\n");
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }

    private void responseLoginFailHeader(DataOutputStream dos, int lengthOfBodyContent) {
        try {
            dos.writeBytes("HTTP/1.1 200 OK \r\n");
            dos.writeBytes("Content-Type: text/html\r\n");
            dos.writeBytes("Content-Length: " + lengthOfBodyContent + "\r\n");
            dos.writeBytes("Set-Cookie: logined=false\r\n");
            dos.writeBytes("\r\n");
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }

    private void response302Header(DataOutputStream dos, int lengthOfBodyContent) {
        try {
            dos.writeBytes("HTTP/1.1 302 Found \r\n");
            dos.writeBytes("Content-Type: text/html;charset=utf-8\r\n");
            dos.writeBytes("Location: /index.html\r\n");
            dos.writeBytes("Content-Length: " + lengthOfBodyContent + "\r\n");
            dos.writeBytes("\r\n");
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }

    private void responseBody(DataOutputStream dos, byte[] body) {
        try {
            dos.write(body, 0, body.length);
            dos.flush();
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }
}
