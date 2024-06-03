package tubes.jarkom;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Enumeration;

import com.google.gson.Gson;

import tubes.jarkom.model.Request;
import tubes.jarkom.model.Room;
import tubes.jarkom.model.User;

public class Client {
    private static boolean isLoggedIn = false;

    public static void main(String[] args) throws UnknownHostException, IOException {
        //Socket(server ip, server port)
        Socket clientSocket = new Socket("127.0.0.1", 6789);

        Gson gson = new Gson();

        BufferedReader input = new BufferedReader(new InputStreamReader(System.in));

        OutputStream output = clientSocket.getOutputStream();
        PrintWriter writer = new PrintWriter(output,true);
        InputStream inFromServer = clientSocket.getInputStream();
        BufferedReader readInputFromServer = new BufferedReader(new InputStreamReader(inFromServer));

        System.out.println("My ip address is : " + getMyLocalIPAddress());

        System.out.print("Action : ");
        String action = input.readLine();

        if(action.equals("register")){
            System.out.println("==REGISTER==");
            System.out.print("Username : ");

            String username = input.readLine();
    
            System.out.print("Password : ");
    
            String password = input.readLine();

            System.out.print("Name : ");

            String name = input.readLine();

            User user1 = new User(username, password);

            user1.setName(name);
            // user1.setAction("register");

            Request<String> req = new Request<>("register", gson.toJson(user1));

            String jsonMessage = gson.toJson(req);

            writer.println(jsonMessage);
        }
        else if(action.equals("login")){
            System.out.print("Username : ");

            String username = input.readLine();
    
            System.out.print("Password : ");
    
            String password = input.readLine();

            User user1 = new User(username, password);

            Request<String> req = new Request<>("login", gson.toJson(user1));

            String jsonMessage = gson.toJson(req);

            writer.println(jsonMessage);

            String response = readInputFromServer.readLine();

            System.out.println(response);

            if(response.equals("200")){
                isLoggedIn = true;
            }


            // System.out.println(user1.getIsLoggedIn());

            // System.out.println(user1.getIsLoggedIn());

            // if(!user1.getIsLoggedIn()){
            //     System.out.println("You must login first");
            // }
            // System.out.print("Room name : ");

            // String room_name = input.readLine();

            // user1.setAction("createRoom");

            // Room room1 = new Room(user1.getName(), room_name);

            // String jsonRoomMessage = gson.toJson(room1);

            // writer.println(jsonRoomMessage);
        }
        else if(action.equals("create")){
            // if(!user1.getIsLoggedIn()){
            //     System.out.println("You must login first");
            // }
            System.out.print("Room name : ");

            String room_name = input.readLine();

            // Request<String> req = new Request<>("login", gson.toJson(user1));

            // String jsonMessage = gson.toJson(req);

            // writer.println(jsonMessage);
        }
    }

        // String received = inFromServer.readLine();

        // System.out.println("Server : " +received);

    public static String getMyLocalIPAddress(){
        try {
            InetAddress inetAddress = InetAddress.getLocalHost();
            return inetAddress.getHostAddress();
        } catch (UnknownHostException e) {
            e.printStackTrace();
            return "failed to return IP";
        }
    }

    // public static String getIPAddress(){
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
