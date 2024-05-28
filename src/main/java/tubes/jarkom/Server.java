package tubes.jarkom;

import java.io.*;
import java.net.*;
import java.sql.*;

public class Server {
    private final static String SERVER_NAME = "localhost";
    private final static String DB_URL = "jdbc:mysql://localhost:3306/chat_application";
    private final static String DB_USERNAME = "root";
    private final static String DB_PASSWORD = "";

    public static void main(String[] args) throws IOException{
        try{
            ServerSocket socket = new ServerSocket(6789);
            while(true){
                Socket connectionSocket = socket.accept();

                BufferedReader inFromClient = new BufferedReader(new InputStreamReader(connectionSocket.getInputStream()));

                String clientInput = inFromClient.readLine();

                System.out.println("Client input is : " + clientInput);

                // Class.forName("com.mysql.cj.jdbc.Driver");

                // Connection connection = DriverManager.getConnection(DB_URL, DB_USERNAME, DB_PASSWORD);
    
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