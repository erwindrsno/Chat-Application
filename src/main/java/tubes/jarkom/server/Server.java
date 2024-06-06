package tubes.jarkom.server;

import java.io.*;
import java.net.*;
import java.sql.*;

public class Server {
    static int clientCount;
    public static void main(String[] args) throws IOException{
        try{
            ServerSocket socket = new ServerSocket(6789);
            System.out.println("Server is up");
            clientCount = 0;
            while(true){
                Socket connectionSocket = socket.accept();

                Thread clientHandler = new Thread(new ClientHandler(connectionSocket), "Client " + clientCount);
                clientHandler.start();

            }
        }
        catch(Exception e){
            System.out.println(e);
        }
    }

}