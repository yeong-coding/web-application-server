package webserver.controller;

import webserver.http.HttpMethod;
import webserver.http.HttpRequest;
import webserver.http.HttpResponse;

import java.io.IOException;

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
