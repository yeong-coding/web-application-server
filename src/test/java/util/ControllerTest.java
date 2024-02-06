package util;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import webserver.HttpRequest;
import webserver.controller.AbstratController;
import webserver.controller.Controller;
import webserver.controller.HttpResponse;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;


public class ControllerTest {

    private static final Logger log= LoggerFactory.getLogger(ControllerTest.class);

    private static String testDirectory="./src/test/resources/";


    @Test
    public static void request_LOGIN() throws Exception {
//        InputStream in=new FileInputStream(new File(testDirectory+"Http_GET.txt"));
//        HttpRequest request=new HttpRequest(in);
//
//        Controller controller=new AbstratController(request, new HttpResponse());
    }
}
