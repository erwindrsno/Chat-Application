package tubes.jarkom.client;

import tubes.jarkom.env.Env;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;

import com.google.gson.Gson;

import tubes.jarkom.request.Request;
import tubes.jarkom.response.Response;
import tubes.jarkom.model.Room;
import tubes.jarkom.model.User;
import tubes.jarkom.model.UserRoom;
import tubes.jarkom.model.Chat;

public class Client implements IClient {
    private User user;
    private Gson gson;
    private OutputStream output;
    private PrintWriter writer;
    private InputStream inFromServer;
    private BufferedReader readInputFromServer;
    private Socket clientSocket;

    public Client() {
        this.user = new User();
        this.gson = new Gson();
        this.connectToServer();
        // this.listenOnMessage();
    }

    public void connectToServer() {
        // Socket(server ip, server port)
        try {
            // Socket(server ip, server port)
            this.clientSocket = new Socket(Env.getServerIP(), Env.getPort());
            this.output = clientSocket.getOutputStream();
            this.writer = new PrintWriter(output, true);
            this.inFromServer = clientSocket.getInputStream();
            this.readInputFromServer = new BufferedReader(new InputStreamReader(inFromServer));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void listenOnMessage(){
        new Thread(() -> {
            String response = "";

            try{
                response = this.readInputFromServer.readLine();

                @SuppressWarnings("unchecked")
                Response<String> res = gson.fromJson(response, Response.class);

                System.out.println("Message : " + res.getData());
            } catch (Exception e){
                e.printStackTrace();
            }
        }).start();
    }

    @Override
    public void register(String username, String password, String name) {
        this.user.setUsername(username);
        this.user.setPassword(password);
        this.user.setName(name);

        Request<String> req = new Request<>("register", gson.toJson(user));

        String response = "";

        writer.println(gson.toJson(req));

        try {
            response = readInputFromServer.readLine();
        } catch (Exception e) {
            e.printStackTrace();
        }

        @SuppressWarnings("unchecked")
        Response<String> res = gson.fromJson(response, Response.class);

        // empty user credentials so user must login once registered.
        this.user.setUsername("");
        this.user.setPassword("");
        this.user.setName("");

        System.out.println(res.getData());
    }

    @Override
    public void login(String username, String password) {
        this.user.setUsername(username);
        this.user.setPassword(password);

        Request<String> req = new Request<>("login", gson.toJson(user));

        writer.println(gson.toJson(req));

        String response = "";

        try {
            response = readInputFromServer.readLine();
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("sini lah bang masuknya");
        }

        @SuppressWarnings("unchecked")
        Response<String> res = gson.fromJson(response, Response.class);

        System.out.println(res.getData());

        if (res.getData().equals("200")) {
            this.user.setIsLoggedIn(true);
        }
    }

    @Override
    public void sendMessage(String message, String roomName) {
        Request<String> req = new Request<>("sendMessage", gson.toJson(new Chat(message, roomName, this.user.getName())));

        writer.println(gson.toJson(req));

        String response = "";
    }

    @Override
    public void createRoom(String roomName) {
        if (!this.user.getIsLoggedIn())
            return;

        Room room = new Room(roomName, user.getName());

        Request<String> req = new Request<>("createRoom", gson.toJson(room));

        writer.println(gson.toJson(req));

        String response = "";

        try {
            response = readInputFromServer.readLine();
        } catch (Exception e) {
            e.printStackTrace();
        }

        @SuppressWarnings("unchecked")
        Response<String> res = gson.fromJson(response, Response.class);

        System.out.println(res.getData());
    }

    @Override
    public void addMember(String memberName, String roomName) {
        if (!this.user.getIsLoggedIn())
            return;

        UserRoom userRoom = new UserRoom(memberName, roomName);

        Request<String> req = new Request<>("addMember", gson.toJson(userRoom));

        writer.println(gson.toJson(req));

        String response = "";

        try {
            response = readInputFromServer.readLine();
        } catch (Exception e) {
            e.printStackTrace();
        }

        @SuppressWarnings("unchecked")
        Response<String> res = gson.fromJson(response, Response.class);

        System.out.println(res.getData());
    }

    @Override
    public void logout() {
        if (!this.user.getIsLoggedIn())
            return;
        Request<String> req = new Request<>("logout");

        writer.println(gson.toJson(req));

        String response = "";

        try {
            response = readInputFromServer.readLine();
        } catch (Exception e) {
            e.printStackTrace();
        }

        @SuppressWarnings("unchecked")
        Response<String> res = gson.fromJson(response, Response.class);

        this.user.setIsLoggedIn(false);

        System.out.println(res.getData());
    }

    @Override
    public void exit() {
        try {
            Request<String> req = new Request<>("exit");
            writer.println(gson.toJson(req));
            this.user = null;
            this.clientSocket.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
