package tubes.jarkom;

import tubes.jarkom.env.Env;

import java.io.*;
import java.net.*;
import java.sql.*;

public class Server {


    public static void main(String[] args) throws IOException{
        try{
            ServerSocket socket = new ServerSocket(6789);
            while(true){
                Socket connectionSocket = socket.accept();

                BufferedReader inFromClient = new BufferedReader(new InputStreamReader(connectionSocket.getInputStream()));

                String clientInput = inFromClient.readLine();

                System.out.println("Client input is : " + clientInput);

                // Class.forName("com.mysql.cj.jdbc.Driver");

                // Connection connection = DriverManager.getConnection(Env.getDBUrl(), Env.getDB_USERNAME(), Env.getDB_password());
    
                // Statement statement = connection.createStatement();
    
                // String query = "SELECT * FROM USERS";
    
                // ResultSet res = statement.executeQuery(query);
    
                // while(res.next()){
                //     System.out.println(res.getInt(1) + " " + res.getString(2) + " " + res.getString(3) + " " + res.getString(4));
                // }
                // connection.close();
            }
        }
        catch(Exception e){
            System.out.println(e);
        }
    }

}