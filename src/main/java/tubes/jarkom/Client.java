package tubes.jarkom;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.UnknownHostException;

public class Client {
    public static void main(String[] args) throws UnknownHostException, IOException {
        Socket clientSocket = new Socket("127.0.0.1", 6789);

        BufferedReader input = new BufferedReader(new InputStreamReader(System.in));

        DataOutputStream outToServer = new DataOutputStream(clientSocket.getOutputStream());

        BufferedReader inFromServer = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

        System.out.print("Message to Server : ");

        String Sentence = input.readLine();

        outToServer.writeBytes(Sentence + '\n');

        String received = inFromServer.readLine();

        System.out.println("Server : " +received);
    }
}
