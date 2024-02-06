package webserver;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import util.HttpRequestUtils;
import util.IOUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;

public class HttpRequest {

    private static final Logger log = LoggerFactory.getLogger(HttpRequest.class);

    private Map<String, String> header=new HashMap<String, String>();
    private Map<String, String> params=new HashMap<String, String>();
    private RequestLine requestLine;


    public HttpRequest(InputStream in) {

        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(in));
            String line = br.readLine();
            log.debug("line: {}", line);

            if (line == null) return;

            requestLine=new RequestLine(line);

            line = br.readLine();

            while (line!=null && !line.equals("")) {
                log.debug("header: {}", line);
                String[] tokens = line.split(":");
                header.put(tokens[0].trim(), tokens[1].trim());
                line = br.readLine();
            }

            if (getMethod().isPost()) {
                String body = IOUtils.readData(br, Integer.parseInt(header.get("Content-Length")));
                log.debug("body: {}", body);
                params = HttpRequestUtils.parseQueryString(body);
            }
            else {
                params=requestLine.getParams();
            }
        } catch (IOException io){
            log.debug(io.getMessage());
        }

    }

    public HttpMethod getMethod(){
        return requestLine.getMethod();
    }

    public String getPath(){
        return requestLine.getPath();
    }

    public Map<String, String> getHeader(){
        return header;
    }

    public String getHeader(String key){
        return header.get(key);
    }

    public String getParameter(String key){
        return params.get(key);
    }

}
