package tubes.jarkom;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;

import com.google.gson.Gson;

import tubes.jarkom.model.User;

public class Client {

    public static void main(String[] args) throws UnknownHostException, IOException {
        //127.0.0.1 ip server
        Socket clientSocket = new Socket("127.0.0.1", 6789);

        Gson gson = new Gson();

        BufferedReader input = new BufferedReader(new InputStreamReader(System.in));

        // DataOutputStream outToServer = new DataOutputStream(clientSocket.getOutputStream());

        OutputStream output = clientSocket.getOutputStream();
        PrintWriter writer = new PrintWriter(output,true);

        BufferedReader inFromServer = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

        System.out.print("Username : ");

        String username = input.readLine();

        System.out.print("Password : ");

        String password = input.readLine();

        // System.out.print("Name : ");

        // String name = input.readLine();

        // User user1 = new User(username,password,name);

        // user1.setAction("register");

        User user1 = new User(username, password);

        user1.setAction("login");

        String jsonMessage = gson.toJson(user1);

        writer.println(jsonMessage);

        // String received = inFromServer.readLine();

        // System.out.println("Server : " +received);
    }

    public void login(String username, String password){
        
    }
}
