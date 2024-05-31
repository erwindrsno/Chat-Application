package tubes.jarkom;

import tubes.jarkom.env.Env;
import tubes.jarkom.model.User;

import java.io.*;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.sql.*;
import java.util.Enumeration;

import com.google.common.hash.Hashing;
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

            String hashedPassword = Hashing.sha256()
                .hashString(user.getPassword(), StandardCharsets.UTF_8)
                .toString();

            String query = "INSERT INTO users (name, username, password) VALUES ('"+this.user.getName()+"', '"+this.user.getUsername()+"', '"+hashedPassword+"');";

            System.out.println(this.execute_update(query));
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    public boolean execute_update(String query){
        try{
            int res = this.statement.executeUpdate(query);
            if (res == 1) return true;
        } catch(Exception e){
            e.printStackTrace();
        }
        return false;
    }

    public void execute_query(String query){
        try{
            ResultSet res = this.statement.executeQuery(query);
            System.out.println(res);
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