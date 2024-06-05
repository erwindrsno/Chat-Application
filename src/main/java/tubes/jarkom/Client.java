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
import tubes.jarkom.model.Response;
import tubes.jarkom.model.Room;
import tubes.jarkom.model.User;

public class Client {
    // private static boolean isLoggedIn = false;
    private static User user;
    private static BufferedReader input;
    private static OutputStream output;
    private static PrintWriter writer;
    private static InputStream inFromServer;
    private static BufferedReader readInputFromServer;
    private static Gson gson;

    public static void main(String[] args) throws UnknownHostException, IOException {
        //Socket(server ip, server port)
        Socket clientSocket = new Socket("127.0.0.1", 6789);

        gson = new Gson();

        input = new BufferedReader(new InputStreamReader(System.in));

        output = clientSocket.getOutputStream();
        writer = new PrintWriter(output,true);
        inFromServer = clientSocket.getInputStream();
        readInputFromServer = new BufferedReader(new InputStreamReader(inFromServer));

        System.out.println("My ip address is : " + getIPAddress());

        while(true){
            if(user == null){
                System.out.print("Action : ");
                String action = input.readLine();
    
                switch(action){
                    case "login":
                        login();
                        break;
    
                    case "register":
                        register();
                        break;
    
                    default:
                        System.out.println("nope");
                        break;
                }
            }
            else{
                System.out.print("Action : ");
                String action = input.readLine();

                if(action.equals("logout")){
                    logOut(clientSocket);
                    break;
                }

                switch(action){
                    case "create":
                        createRoom();
                        break;

                    default:
                        System.out.println("nopeee");
                        break;
                }
            }
        }
    }

    public static void login(){
        try{
            System.out.println("==LOGIN==");
            System.out.print("Username : ");

            String username = input.readLine();
    
            System.out.print("Password : ");
    
            String password = input.readLine();
    
            user = new User(username, password);
    
            Request<String> req = new Request<>("login", gson.toJson(user));
    
            writer.println(gson.toJson(req));
    
            String response = readInputFromServer.readLine();

            @SuppressWarnings("unchecked")
            Response<String> res = gson.fromJson(response, Response.class);
    
            System.out.println(res.getData());

            if(res.getData().equals("401")){
                user = null;
            }
        }
        catch(Exception e){
            e.printStackTrace();
        }
    }

    public static void register() throws IOException{
        System.out.println("==REGISTER==");
        System.out.print("Username : ");

        String username = input.readLine();

        System.out.print("Password : ");

        String password = input.readLine();

        System.out.print("Name : ");

        String name = input.readLine();

        user = new User(username, password);

        user.setName(name);

        Request<String> req = new Request<>("register", gson.toJson(user));

        writer.println(gson.toJson(req));

        String response = readInputFromServer.readLine();

        @SuppressWarnings("unchecked")
        Response<String> res = gson.fromJson(response, Response.class);

        System.out.println(res.getData());

        //set user to null so user must login once registered.
        user = null;
    }

    public static void createRoom() throws IOException{
        System.out.println("==CREATE==");
        System.out.print("Room name : ");

        String room_name = input.readLine();

        Room room = new Room(room_name, user.getName());

        Request<String> req = new Request<>("createRoom", gson.toJson(room));

        writer.println(gson.toJson(req));

        String response = readInputFromServer.readLine();

        @SuppressWarnings("unchecked")
        Response<String> res = gson.fromJson(response, Response.class);

        System.out.println(res.getData());
    }

    public static void logOut(Socket clientSocket){
        try{
            user = null;
            clientSocket.close();
        }
        catch (Exception e){
            e.printStackTrace();
        }
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
