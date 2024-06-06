package tubes.jarkom.server;

import tubes.jarkom.env.Env;
import tubes.jarkom.request.Request;
import tubes.jarkom.model.Room;
import tubes.jarkom.model.User;
import tubes.jarkom.model.UserRoom;
import tubes.jarkom.response.Response;

import java.io.*;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.google.common.hash.Hashing;
import com.google.gson.Gson;

public class ClientHandler implements Runnable {
    private Socket socket;
    private Gson gson;
    private BufferedReader inFromClient;

    private PrintWriter writer;
    private OutputStream output;

    private User user;

    private Connection connection;
    private Statement statement;

    private boolean flag;

    private static int clientCount = 0;

    public ClientHandler(Socket socket) {
        this.socket = socket;
        this.gson = new Gson();
        clientCount++;
        this.flag = true;
    }

    @Override
    public void run() {
        System.out.println(Thread.currentThread().getName() + " is Running!");
        System.out.println("Total client is : " + clientCount);

        try {
            this.output = this.socket.getOutputStream();
            this.writer = new PrintWriter(output, true);
            this.inFromClient = new BufferedReader(new InputStreamReader(this.socket.getInputStream()));

            connectDB();
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            while (this.flag) {
                String jsonMessage = inFromClient.readLine();

                // not using generic due to request generic class may varies.
                @SuppressWarnings("rawtypes")
                Request request = this.gson.fromJson(jsonMessage, Request.class);

                // for some reason, the request.getData() is unable to cast to a specific types
                // (due to gson library reason)
                // e.g : (User)request.getData()
                // hence .getData() must be convert to string and deserealize it using gson.
                switch (request.getAction()) {
                    case "login":
                        this.user = this.gson.fromJson(request.getData().toString(), User.class);
                        this.login();
                        break;

                    case "register":
                        this.user = this.gson.fromJson(request.getData().toString(), User.class);
                        this.register();
                        break;

                    case "createRoom":
                        this.createRoom(this.gson.fromJson(request.getData().toString(), Room.class));
                        break;

                    case "addMember":
                        this.addMember(this.gson.fromJson(request.getData().toString(), UserRoom.class));
                        break;

                    case "logout":
                        this.logout();
                        break;

                    case "exit":
                        this.exit();
                        this.flag = !flag;
                        break;
                }
            }
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    public void connectDB() throws Exception {
        Class.forName("com.mysql.cj.jdbc.Driver");

        this.connection = DriverManager.getConnection(Env.getDBURL(), Env.getDBUsername(), Env.getDBPassword());

        this.statement = connection.createStatement();
    }

    public void login() throws Exception {
        String hashedPassword = Hashing.sha256()
                .hashString(user.getPassword(), StandardCharsets.UTF_8)
                .toString();

        String query = "SELECT * FROM users WHERE username='" + user.getUsername() + "' && password ='" + hashedPassword
                + "'";

        ResultSet queryRes = this.execute_query(query);

        Response<String> res;

        // .next() method move the cursor for one index.
        // set name because during login, the object that is send by client doesnt have
        // name inside the user object.
        if (queryRes.next()) {
            this.user.setName(queryRes.getString(2));
            System.out.println(this.user.getName() + " has logged in");
            this.user.setIsLoggedIn(true);

            do {
                this.user.setId(queryRes.getInt(1));
            } while (queryRes.next());

            res = new Response<>("200");
        } else {
            res = new Response<>("401");
            this.user.setIsLoggedIn(false);
        }

        writer.println(gson.toJson(res));
    }

    public void register() throws Exception {
        String hashedPassword = Hashing.sha256()
                .hashString(user.getPassword(), StandardCharsets.UTF_8)
                .toString();

        String query = "INSERT INTO users (name, username, password) VALUES ('" + this.user.getName() + "', '"
                + this.user.getUsername() + "', '" + hashedPassword + "');";

        Response<String> res = (this.execute_update(query)) ? new Response<>("201") : new Response<>("500");

        writer.println(gson.toJson(res));
    }

    public void createRoom(Room room) throws Exception {
        SimpleDateFormat ft = new SimpleDateFormat("dd-MM-yyyy");
        String created_at = ft.format(new Date());

        String query = "INSERT INTO rooms (name, owner_id, created_at) VALUES ('" + room.getName() + "', '"
                + this.user.getId() + "', '" + created_at + "');";

        Response<String> res = (this.execute_update(query))
                ? new Response<>(room.getName() + " has been created at " + created_at)
                : new Response<>("500");

        writer.println(gson.toJson(res));
    }

    public void addMember(UserRoom userRoom) throws Exception{
        System.out.println("sawadeekap");
    }

    public void logout() throws Exception{
        this.user.setIsLoggedIn(false);

        Response<String> res = new Response<>("log out succeed");

        writer.println(gson.toJson(res));
    }

    public void exit() throws Exception {
        this.user = null;
        clientCount--;
        System.out.println("A client disconnected, remains : " + clientCount);
        this.socket.close();
    }

    public boolean execute_update(String query) {
        try {
            int res = this.statement.executeUpdate(query);
            if (res == 1)
                return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public ResultSet execute_query(String query) {
        try {
            ResultSet res = this.statement.executeQuery(query);
            return res;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}