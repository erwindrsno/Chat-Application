package tubes.jarkom;

import java.io.*;
import java.net.*;
import java.sql.*;

public class Server {
    public static void main(String[] args) throws IOException{
        try{
            ServerSocket socket = new ServerSocket(6789);
            while(true){
                Socket connectionSocket = socket.accept();

                Thread clientHandler = new Thread(new ClientHandler(connectionSocket), "Client 1");
                clientHandler.start();
            }
        }
        catch(Exception e){
            System.out.println(e);
        }
    }

}