package webserver.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

public class HttpResponse {

    private static final Logger log=LoggerFactory.getLogger(HttpResponse.class);

    private static DataOutputStream dos=null;
    private Map<String, String> header=new HashMap<String, String>();

    public HttpResponse(OutputStream out){
        dos=new DataOutputStream(out);
    }

    public void forward(String path) {

        try {
            byte[] body = Files.readAllBytes(new File("./webapp" + path).toPath());

            if(path.endsWith(".css")){
                header.put("Content-Type", "text/css");
            }
            else if(path.endsWith(".js")){
                header.put("Content-Type", "javascript");
            }
            else {
                header.put("Content-Type", "text/html;charset=utf-8");
            }
            header.put("Content-Length", body.length+"");
            response200Header(body.length);
            responseBody(body);

        }catch(IOException io){
            log.debug(io.getMessage());
        }
    }

    public void forwardBody(String body){
        byte[] contents=body.getBytes();
        header.put("Content-Type", "text/html;charset=utf-8");
        header.put("Content-Length", contents.length+"");
        response200Header(contents.length);
        responseBody(contents);
    }

    public void sendRedirect(String path) throws IOException{

        dos.writeBytes("HTTP/1.1 302 Found \r\n");

        addHeader("Content-Type", "text/html");
        addHeader("Content-Length", "0");
        addHeader("Location", path);

        processHeaders();

        dos.flush();
    }

    private void response200Header(int len) {
        try {
            dos.writeBytes("HTTP/1.1 200 OK \r\n");

            for(Map.Entry<String, String> entry: header.entrySet()){
                dos.writeBytes(entry.getKey()+": "+entry.getValue()+"\r\n");
            }
            dos.writeBytes("\r\n");

        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }

    private void processHeaders(){

        try {
            for (Map.Entry<String, String> entry : header.entrySet()) {
                dos.writeBytes(entry.getKey() + ": " + entry.getValue() + "\r\n");
                log.info("processHeaders=>{}, {}", entry.getKey(), entry.getValue());
            }
            dos.writeBytes("\r\n");
        } catch(IOException io){
            log.debug(io.getMessage());
        }
    }

    private void responseBody(byte[] body) {
        try {
            dos.write(body, 0, body.length);
            dos.flush();

        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }

    public void addHeader(String key, String val){
        header.put(key, val);
    }


}
