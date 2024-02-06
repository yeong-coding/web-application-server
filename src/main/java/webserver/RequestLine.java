package webserver;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import util.HttpRequestUtils;

import java.util.HashMap;
import java.util.Map;

public class RequestLine {

    private static final Logger log= LoggerFactory.getLogger(RequestLine.class);

    private HttpMethod method;
    private String path;
    private Map<String, String> params=new HashMap<>();

    public RequestLine(String line){

        log.debug("RequestLine - line: {}", line);

        String[] tokens=line.split(" ");

        if("GET".equals(tokens[0])){
            method=HttpMethod.GET;
            int idx=tokens[1].indexOf("?");
            if(idx!=-1) {
                path = tokens[1].substring(0, idx);
            }
            else {
                path=tokens[1];
            }
            params= HttpRequestUtils.parseQueryString(tokens[1].substring(idx+1));
        }

        if("POST".equals(tokens[0])){
            method=HttpMethod.POST;
            path=tokens[1];
            log.info("POST path: {}", path);

        }
    }

    public HttpMethod getMethod() {
        return method;
    }

    public String getPath() {
        return path;
    }

    public String getParams(String key) {
        return params.get(key);
    }

    public Map<String, String> getParams(){
        return params;
    }
}
