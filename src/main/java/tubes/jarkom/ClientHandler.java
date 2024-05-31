package tubes.jarkom;

import tubes.jarkom.env.Env;
import java.io.*;
import java.net.*;
import java.sql.*;
import java.util.Enumeration;


//bikin register
public class ClientHandler implements Runnable{
    private Socket socket;
    private BufferedReader input;
    private DataOutputStream outToServer;

    public ClientHandler(Socket socket){
        this.socket = socket;
    }

    @Override
    public void run() {
        System.out.println(Thread.currentThread().getName() + " is Running!");

        try{
            System.out.println("Connected to Server and my IP is: " + this.getMyLocalIPAddress());

            BufferedReader inFromClient = new BufferedReader(new InputStreamReader(this.socket.getInputStream()));

            DataOutputStream outToClient = new DataOutputStream(this.socket.getOutputStream());

            String clientSentence = inFromClient.readLine();

            outToClient.writeBytes(clientSentence.toUpperCase()+"\n");

            connectDB();

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

            Connection connection = DriverManager.getConnection(Env.getDBUrl(), Env.getDB_USERNAME(), Env.getDB_password());
    
            Statement statement = connection.createStatement();
    
            String query = "SELECT * FROM USERS";
    
            ResultSet res = statement.executeQuery(query);
    
            while(res.next()){
                System.out.println(res.getInt(1) + " " + res.getString(2) + " " + res.getString(3) + " " + res.getString(4));
            }
        } catch(Exception e){
            System.out.println(e);
        }
    }

    public String getMyLocalIPAddress(){
        try {
            InetAddress inetAddress = InetAddress.getLocalHost();
            return inetAddress.getHostAddress();
        } catch (UnknownHostException e) {
            e.printStackTrace();
            return "failed to return IP";
        }
    }

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

    public void register(){

    }
}