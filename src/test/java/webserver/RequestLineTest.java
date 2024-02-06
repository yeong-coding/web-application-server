package webserver;

import org.junit.Test;
import static junit.framework.TestCase.assertEquals;

public class RequestLineTest {

    @Test
    public void create_method(){
        RequestLine line=new RequestLine("GET /index.html HTTP/1.1");
        assertEquals(HttpMethod.GET, line.getMethod());
        assertEquals("/index.html", line.getPath());

        line=new RequestLine("POST /index.html HTTP/1.1");
        assertEquals(HttpMethod.POST, line.getMethod());
        assertEquals("/index.html", line.getPath());
    }

    @Test
    public void create_path_and_params(){
        RequestLine line=new RequestLine("GET /user/create?userId=javajigi&password=password&name=JaeSung HTTP/1.1");
        assertEquals(HttpMethod.GET, line.getMethod());
        assertEquals("/user/create", line.getPath());
        assertEquals("javajigi", line.getParams("userId"));
        assertEquals("password", line.getParams("password"));
        assertEquals("JaeSung", line.getParams("name"));

        line=new RequestLine("POST /user/create HTTP/1.1");
        assertEquals(HttpMethod.POST, line.getMethod());
        assertEquals("/user/create", line.getPath());
    }

}
