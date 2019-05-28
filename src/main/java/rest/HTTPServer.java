package rest;

import java.io.DataInputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class HTTPServer {

    static private ServerSocket serverSocket;

    public static void main(String args[]) throws Exception {
        Socket socket;

        try {
            serverSocket = new ServerSocket(Settings.PORT);
            System.out.println("Server is now running at http://localhost:" + Settings.PORT + "...");
        } catch (IOException e) {
            System.out.println("Server failed to open local port " + Settings.PORT);
            e.printStackTrace();
            System.exit(1);
        } while (true) {
            socket = serverSocket.accept();

            DataInputStream dataInputStream = new DataInputStream(socket.getInputStream());

            HTTPMessage request = new HTTPMessage(dataInputStream);

            System.out.println("CEEEEE");
            System.out.println(request.getURI());

            HTTPRouter thread = new HTTPRouter(socket, request);


            if(request.getURI().equals("/articles")) {
                thread.setPriority(1);
            }


            System.out.println(thread.getPriority());
            thread.start();
        }
    }

}
