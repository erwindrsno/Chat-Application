package tubes.jarkom.client;

import tubes.jarkom.env.Env;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;

import com.google.gson.Gson;

import tubes.jarkom.model.Request;
import tubes.jarkom.model.Response;
import tubes.jarkom.model.Room;
import tubes.jarkom.model.User;

public class Client implements IClient{
    private User user;
    private Gson gson;
    private OutputStream output;
    private PrintWriter writer;
    private InputStream inFromServer;
    private BufferedReader readInputFromServer;
    private Socket clientSocket;

    public Client(){
        this.user = new User();
        this.gson = new Gson();
        this.connectToServer();
    }

    @Override
    public void register(String username, String password, String name) {
        this.user.setUsername(username);
        this.user.setPassword(password);
        this.user.setName(name);

        Request<String> req = new Request<>("register", gson.toJson(user));

        String response = "";

        writer.println(gson.toJson(req));

        try{
            response = readInputFromServer.readLine();
        }
        catch(Exception e){
            e.printStackTrace();
        }

        @SuppressWarnings("unchecked")
        Response<String> res = gson.fromJson(response, Response.class);

        //set user to null so user must login once registered.
        this.user = null;

        System.out.println(res.getData());
    }

    @Override
    public void login(String username, String password) {
        this.user.setUsername(username);
        this.user.setPassword(password);

        Request<String> req = new Request<>("login", gson.toJson(user));

        writer.println(gson.toJson(req));

        String response = "";

        try{
            response = readInputFromServer.readLine();
        }
        catch(Exception e){
            e.printStackTrace();
        }

        @SuppressWarnings("unchecked")
        Response<String> res = gson.fromJson(response, Response.class);

        if(res.getData().equals("200")){
            this.user.setIsLoggedIn(true);
        }

        System.out.println(res.getData());
    }

    @Override
    public void sendMessage() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'sendMessage'");
    }

    @Override
    public String createRoom(String roomName) {
        if(!this.user.getIsLoggedIn()) return "gabisa mas";

        Room room = new Room(roomName, user.getName());

        Request<String> req = new Request<>("createRoom", gson.toJson(room));

        writer.println(gson.toJson(req));
        
        String response = "";

        try{
            response = readInputFromServer.readLine();
        }
        catch(Exception e){
            e.printStackTrace();
        }

        @SuppressWarnings("unchecked")
        Response<String> res = gson.fromJson(response, Response.class);

        System.out.println(res.getData());

        return "bisa mas";
    }

    @Override
    public void logOut() {
        this.user.setIsLoggedIn(false);
        this.user = null;
        try{
            this.clientSocket.close();
        }
        catch(Exception e){
            e.printStackTrace();
        }
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'logOut'");
    }   
    
    public void connectToServer() {
        //Socket(server ip, server port)
        try{
            //Socket(server ip, server port)
            this.clientSocket = new Socket(Env.getServerIP(), Env.getPort());
            this.output = clientSocket.getOutputStream();
            this.writer = new PrintWriter(output,true);
            this.inFromServer = clientSocket.getInputStream();
            this.readInputFromServer = new BufferedReader(new InputStreamReader(inFromServer));
        }
        catch(Exception e){
            e.printStackTrace();
        }
    }
}