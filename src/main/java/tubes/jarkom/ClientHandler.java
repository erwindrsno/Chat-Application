package tubes.jarkom;

import tubes.jarkom.env.Env;
import tubes.jarkom.model.Request;
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
    private BufferedReader inFromClient;

    private PrintWriter writer;
    private OutputStream output;

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
            this.output = this.socket.getOutputStream();
            this.writer = new PrintWriter(output, true);
            this.inFromClient = new BufferedReader(new InputStreamReader(this.socket.getInputStream()));
        }catch (Exception e){
            e.printStackTrace();
        }

        try{
            String jsonMessage = inFromClient.readLine();

            @SuppressWarnings("rawtypes")
            Request request = this.gson.fromJson(jsonMessage, Request.class);

            connectDB();

            //because to to deserialize, the data must be always a String, therefore .toString() method is used.
            //since above code is receiving the rawtype non generics.
            switch(request.getAction()){
                case "login":
                    this.user = this.gson.fromJson(request.getData().toString(), User.class);
                    this.login();
                    break;

                case "register":
                    this.user = this.gson.fromJson(request.getData().toString(), User.class);
                    this.register();
                    break;
            }

            // switch(user.getAction()) {
            //     case "login":
            //         this.login();
            //         break;

            //     case "register":
            //         this.register();
            //         break;

            //     case "createRoom":
            //         this.createRoom();
            //         break;
            //     default:
            //       // code block
            // }
        }
        catch(Exception e){
            System.out.println(e);
        }
    }

    public void connectDB(){
        try{
            Class.forName("com.mysql.cj.jdbc.Driver");

            this.connection = DriverManager.getConnection(Env.getDBUrl(), Env.getDB_USERNAME(), Env.getDB_PASSWORD());
    
            this.statement = connection.createStatement();

        } catch(Exception e){
            System.out.println(e);
        }
    }

    public void login(){
        try{
            String hashedPassword = Hashing.sha256()
                .hashString(user.getPassword(), StandardCharsets.UTF_8)
                .toString();

            String query = "SELECT * FROM users WHERE username='" + this.user.getUsername() + "' && password ='"+hashedPassword+"'";

            if(this.execute_query(query) != null){
                this.user.setIsLoggedIn(true);
                System.out.println("login sukses 200");
            }
            else{
                this.user.setIsLoggedIn(false);
            }
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    public void register(){
        try{
            String hashedPassword = Hashing.sha256()
                .hashString(user.getPassword(), StandardCharsets.UTF_8)
                .toString();
            // StringBuilder queryBuilder = new StringBuilder("INSERT INTO users (name, username, password) VALUE (',?,?'")
            //     .replace(52, 53, this.user.getUsername())
            //     .replace(54, 55, this.user.getPassword())
            //     .replace(56, 57, this.user.getName());

            // String query = queryBuilder.toString();

            // System.out.println(query);

            String query = "INSERT INTO users (name, username, password) VALUES ('"+this.user.getName()+"', '"+this.user.getUsername()+"', '"+hashedPassword+"');";

            System.out.println(this.execute_update(query));
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    public void createRoom(){
        try{
            System.out.println("masuk create room");
        }
        catch(Exception e){
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

    public ResultSet execute_query(String query){
        try{
            ResultSet res = this.statement.executeQuery(query);
            return res;
        } catch(Exception e){
            e.printStackTrace();
        }
        return null;
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