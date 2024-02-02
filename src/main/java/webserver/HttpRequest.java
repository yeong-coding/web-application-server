package webserver;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;

public class HttpRequest {

    private static final Logger log = LoggerFactory.getLogger(HttpRequest.class);

    private String method;
    private String path;
    private Map<String, String> header;
    private Map<String, String> parameter;

    public HttpRequest(InputStream in) throws IOException {

        BufferedReader br=new BufferedReader(new InputStreamReader(in));
        String line=br.readLine();
        log.info("line: {}", line);
        String[] tokens=line.split(" ");

        if("GET".equals(tokens[0])){
            method=tokens[0];
            int idx=tokens[1].indexOf("?");
            path=tokens[1].substring(0, idx);
            parseParameter(tokens[1].substring(idx+1));

            while(true){
                line=br.readLine();
                if(line==null || line.isBlank()) {
                    in.close();
                    br.close();
                    return;
                }
                parseHeader(line);
            }

        }

        if("POST".equals(tokens[0])){
            method=tokens[0];
            path=tokens[1];

            while(true){
                line=br.readLine();
                if(line.isBlank()) {
                    break;
                }
                parseHeader(line);
            }

            line=br.readLine();
            parseParameter(line);

        }

        in.close();
        br.close();



    }

    public void parseHeader(String line) throws IOException {

        if(header==null){
            header=new HashMap<String, String>();
        }

        String[] params=line.split(": ");
        header.put(params[0], params[1]);

    }

    public void parseParameter(String line){

        if(parameter==null){
            parameter=new HashMap<String, String>();
        }

        String[] params=line.split("&|=");

        for(int i=0; i<params.length; i+=2){
            log.info("params: {}, {}", params[i], params[i+1]);
            parameter.put(params[i], params[i+1]);
        }

    }

    public String getMethod(){
        return method;
    }

    public String getPath(){
        return path;
    }

    public String getHeader(String key){
        return header.get(key);
    }

    public String getParameter(String key){
        return parameter.get(key);
    }

}
