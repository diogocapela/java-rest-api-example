package rest;

import java.io.*;
import java.net.Socket;

public class HTTPRouter extends Thread {
    private Socket socket;
    private HTTPMessage request;

    private DataOutputStream dataOutputStream;

    public HTTPRouter(Socket socket, HTTPMessage request) {
        this.socket = socket;
        this.request = request;
    }

    public void run() {
        try {
            dataOutputStream = new DataOutputStream(socket.getOutputStream());

            HTTPMessage response = new HTTPMessage();

            String method = request.getMethod();
            String endpoint = request.getURI();

            switch (method) {
                // GET Router
                //========================================================
                case "GET":

                    switch (endpoint) {
                        case "/":
                            response.setResponseStatus("200 OK");
                            response.setContentFromString("<html><body><h1>Home</h1></body></html>", "text/html");
                            break;
                        case "/articles":
                            response.setResponseStatus("200 OK");
                            response.setContentFromString("<html><body><h1>Articles</h1></body></html>", "text/html");
                            break;
                        default:
                            response.setContentFromString("<html><body><h1>404 File not Found</h1></body></html>", "text/html");
                            response.setResponseStatus("404 Not Found");
                    }

                    response.send(dataOutputStream);
                    break;

                // POST Router
                //========================================================
                case "POST":

                    response.send(dataOutputStream);
                    break;

                // PUT Router
                //========================================================
                case "PUT":

                    response.send(dataOutputStream);
                    break;

                // DELETE Router
                //========================================================
                case "DELETE":


                    response.send(dataOutputStream);
                    break;
            }

            socket.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}

