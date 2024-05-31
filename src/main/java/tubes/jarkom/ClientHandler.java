package tubes.jarkom;

import tubes.jarkom.env.Env;
import tubes.jarkom.model.User;

import java.io.*;
import java.net.*;
import java.sql.*;
import java.util.Enumeration;

import com.google.gson.Gson;

//bikin register
public class ClientHandler implements Runnable{
    private Socket socket;
    private Gson gson;
    private DataOutputStream outToServer;
    private BufferedReader inFromClient;
    private User user;
    private Connection connection;
    private Statement statement;

    public ClientHandler(Socket socket){
        this.socket = socket;
        this.gson = new Gson();
    }

    @Override
    public void run() {
        System.out.println(Thread.currentThread().getName() + " is Running!");

        try{
            this.inFromClient = new BufferedReader(new InputStreamReader(this.socket.getInputStream()));
            // System.out.println("Connected to Server and my IP is: " + this.getMyLocalIPAddress());

            String jsonMessage = inFromClient.readLine();

            this.user = this.gson.fromJson(jsonMessage, User.class);

            connectDB();

            switch(user.getAction()) {
                case "login":
                    this.login();
                    break;

                case "register":
                    this.register();
                    break;
                default:
                  // code block
            }

            // System.out.println("Username nya adalah : " + user.getUsername());

            // System.out.println("Password nya adalah : " + user.getPassword());

            // System.out.println("Action nya adalah : " + user.getAction());

            // DataOutputStream outToClient = new DataOutputStream(this.socket.getOutputStream());

            // String clientSentence = inFromClient.readLine();

            // outToClient.writeBytes(clientSentence.toUpperCase()+"\n");

            // connectDB();

            // this.outToServer = new DataOutputStream(clientSocket.getOutputStream());

            // this.login();

            // clientSocket.close();
        }
        catch(Exception e){
            System.out.println(e);
        }
    }

    public void connectDB(){
        try{
            Class.forName("com.mysql.cj.jdbc.Driver");

            this.connection = DriverManager.getConnection(Env.getDBUrl(), Env.getDB_USERNAME(), Env.getDB_password());
    
            this.statement = connection.createStatement();

        } catch(Exception e){
            System.out.println(e);
        }
    }

    public void login(){

    }

    public void register(){
        try{
            String query = "INSERT INTO users (name, username, password) VALUES ('"+this.user.getName()+"', '"+this.user.getUsername()+"', '"+this.user.getPassword()+"');";

            System.out.println(query);

            this.execute_query(query);
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    public void execute_query(String query){
        try{
            int res = this.statement.executeUpdate(query);
            System.out.println(res);
            // while(res.next()){
            //     System.out.println(res.getInt(1) + " " + res.getString(2) + " " + res.getString(3) + " " + res.getString(4));
            // }
        } catch(Exception e){
            e.printStackTrace();
        }
    }

    // public String getMyLocalIPAddress(){
    //     try {
    //         InetAddress inetAddress = InetAddress.getLocalHost();
    //         return inetAddress.getHostAddress();
    //     } catch (UnknownHostException e) {
    //         e.printStackTrace();
    //         return "failed to return IP";
    //     }
    // }

    // public String getIPAddress(){
    //     try {
    //         Enumeration<NetworkInterface> networkInterfaces = NetworkInterface.getNetworkInterfaces();
    //         while (networkInterfaces.hasMoreElements()) {
    //             NetworkInterface networkInterface = networkInterfaces.nextElement();
    //             Enumeration<InetAddress> inetAddresses = networkInterface.getInetAddresses();
    //             while (inetAddresses.hasMoreElements()) {
    //                 InetAddress inetAddress = inetAddresses.nextElement();
    //                 if (!inetAddress.isLoopbackAddress() && inetAddress.isSiteLocalAddress()) {
    //                     return inetAddress.getHostAddress();
    //                 }
    //             }
    //         }
    //     } catch (Exception e) {
    //         e.printStackTrace();
    //     }
    //     return "failed";
    // }
}