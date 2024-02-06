package webserver.controller;

import webserver.HttpMethod;
import webserver.HttpRequest;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class AbstratController implements Controller {

    @Override
    public void service(HttpRequest request, HttpResponse response) throws IOException {

        HttpMethod method=request.getMethod();

        if(method.isPost()){
            doPost(request, response);
        }
        else {
            doGet(request, response);
        }

    }

    protected void doPost(HttpRequest request, HttpResponse response) throws IOException {

    }

    protected void doGet(HttpRequest request, HttpResponse response) throws IOException {

    }


}
