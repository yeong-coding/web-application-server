package webserver.controller;

import webserver.HttpRequest;

import java.io.IOException;

public interface Controller {
    void service(HttpRequest request, HttpResponse response) throws IOException;

}
