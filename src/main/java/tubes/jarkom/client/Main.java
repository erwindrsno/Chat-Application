package tubes.jarkom.client;

import java.io.BufferedReader;
import java.io.IOError;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.Enumeration;

public class Main {
    public static void main(String[] args) throws IOException{
        IClient client = new Client();

        BufferedReader input = new BufferedReader(new InputStreamReader(System.in));

        System.out.println("My ip address is : " + getIPAddress());

        while(true){
            System.out.print("Action : ");
            String action = input.readLine();

            switch(action){
                case "login":
                    handleLogin(input, client);
                    break;
                
                case "register":
                    handleRegister(input, client);
                    break;

                case "create":
                    handleCreateRoom(input, client);
                    break;
            }
        }
    }

    public static void handleLogin(BufferedReader input, IClient client) throws IOException{
        System.out.println("==LOGIN==");
        System.out.print("Username : ");
        String username = input.readLine();

        System.out.print("Password : ");
        String password = input.readLine();

        client.login(username, password);
    }

    public static void handleRegister(BufferedReader input, IClient client) throws IOException{
        System.out.println("==REGISTER==");
        System.out.print("Username : ");
        String username = input.readLine();

        System.out.print("Password : ");
        String password = input.readLine();

        System.out.print("Name : ");
        String name = input.readLine();

        client.register(username, password, name);
    }

    public static void handleCreateRoom(BufferedReader input, IClient client) throws IOException{
        System.out.println("==CREATE ROOM==");
        System.out.print("Room name : ");
        String roomName = input.readLine();
        
        client.createRoom(roomName);
    }

    public static String getIPAddress(){
        try {
            Enumeration<NetworkInterface> networkInterfaces = NetworkInterface.getNetworkInterfaces();
            while (networkInterfaces.hasMoreElements()) {
                NetworkInterface networkInterface = networkInterfaces.nextElement();
                Enumeration<InetAddress> inetAddresses = networkInterface.getInetAddresses();
                while (inetAddresses.hasMoreElements()) {
                    InetAddress inetAddress = inetAddresses.nextElement();
                    if (!inetAddress.isLoopbackAddress() && inetAddress.isSiteLocalAddress()) {
                        return inetAddress.getHostAddress();
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "failed";
    }
}
