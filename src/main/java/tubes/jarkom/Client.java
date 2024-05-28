package tubes.jarkom;

import java.io.*;
import java.net.*;
import java.sql.*;


//bikin register
public class Client implements Runnable{
    private Socket clientSocket;
    private BufferedReader input;
    private String username;
    private String password;
    private DataOutputStream outToServer;

    @Override
    public void run() {
        System.out.println(Thread.currentThread().getName() + " is Running!");
        this.input = new BufferedReader(new InputStreamReader(System.in));

        try{
            this.clientSocket = new Socket(this.getIpAddress(), 6789);
            System.out.println("Connected to Server and my IP is: " + this.getIpAddress());

            this.outToServer = new DataOutputStream(clientSocket.getOutputStream());

            System.out.print("Username : ");
            this.username = this.input.readLine();

            System.out.print("Password : ");
            this.password = this.input.readLine();

            this.login();

            clientSocket.close();
        }
        catch(Exception e){
            System.out.println(e);
        }
    }

    public String getIpAddress(){
        try {
            InetAddress inetAddress = InetAddress.getLocalHost();
            return inetAddress.getHostAddress();
        } catch (UnknownHostException e) {
            e.printStackTrace();
            return "failed to return IP";
        }
    }

    public void login(){
        try{
            System.out.println("My username is : " + this.username);
            System.out.println("My pw is : " + this.password);
            this.outToServer.writeBytes(username+"\n");
            this.outToServer.writeBytes(password+"\n");
        } catch (Exception e){
            e.printStackTrace();
        }
    }
}