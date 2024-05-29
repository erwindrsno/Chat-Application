package tubes.jarkom;

import java.io.*;
import java.net.*;


//bikin register
public class Client implements Runnable{
    private Socket clientSocket;
    private BufferedReader input;
    private DataOutputStream outToServer;

    @Override
    public void run() {
        System.out.println(Thread.currentThread().getName() + " is Running!");
        this.input = new BufferedReader(new InputStreamReader(System.in));

        try{
            this.clientSocket = new Socket(this.getIpAddress(), 6789);
            System.out.println("Connected to Server and my IP is: " + this.getIpAddress());

            this.outToServer = new DataOutputStream(clientSocket.getOutputStream());

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
            System.out.print("Username : ");
            String username = this.input.readLine();

            System.out.print("Password : ");
            String password = this.input.readLine();

            System.out.println("My username is : " + username);
            System.out.println("My pw is : " + password);
            this.outToServer.writeBytes(username+"\n");
            this.outToServer.writeBytes(password+"\n");
        } catch (Exception e){
            e.printStackTrace();
        }
    }
}