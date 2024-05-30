package tubes.jarkom;

import java.io.*;
import java.net.*;
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
            // this.outToServer = new DataOutputStream(clientSocket.getOutputStream());

            // this.login();

            // clientSocket.close();
        }
        catch(Exception e){
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

    public void register(){

    }
}