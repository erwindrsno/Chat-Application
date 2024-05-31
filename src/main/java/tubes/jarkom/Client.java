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

        OutputStream output = clientSocket.getOutputStream();
        PrintWriter writer = new PrintWriter(output,true);

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

            User user1 = new User(username, password, name);

            user1.setAction("register");

            String jsonMessage = gson.toJson(user1);

            writer.println(jsonMessage);
        }
        else if(action.equals("login")){
            System.out.print("Username : ");

            String username = input.readLine();
    
            System.out.print("Password : ");
    
            String password = input.readLine();

            User user1 = new User(username, password);

            user1.setAction("login");

            String jsonMessage = gson.toJson(user1);

            writer.println(jsonMessage);
        }

        // String received = inFromServer.readLine();

        // System.out.println("Server : " +received);
    }

    public void login(String username, String password){
        
    }
}
