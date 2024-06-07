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
import com.mysql.cj.x.protobuf.MysqlxPrepare.Prepare;

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

        Response<String> res;

        String loginQuery = "SELECT * FROM users WHERE username = ? && password = ?";
        PreparedStatement psLoginQuery = this.connection.prepareStatement(loginQuery, PreparedStatement.RETURN_GENERATED_KEYS);
        psLoginQuery.setString(1, this.user.getUsername());
        psLoginQuery.setString(2, hashedPassword);
        ResultSet rs = psLoginQuery.executeQuery();

        // .next() method move the cursor for one index.
        // set name because during login, the object that is send by client doesnt have
        // name inside the user object.
        if(rs.next()){
            this.user.setName(rs.getString(2));
            System.out.println(this.user.getName() + " has logged in");
            this.user.setIsLoggedIn(true);

            do {
                this.user.setId(rs.getInt(1));
            } while (rs.next());

            res = new Response<>("200");
        }
        else {
            res = new Response<>("401");
            this.user.setIsLoggedIn(false);
        }

        writer.println(gson.toJson(res));
    }

    public void register() throws Exception {
        String hashedPassword = Hashing.sha256()
                .hashString(this.user.getPassword(), StandardCharsets.UTF_8)
                .toString();

        Response<String> res;

        String registerQuery = "INSERT INTO users (name, username, password) VALUES (?,?,?)";
        PreparedStatement psRegisterQuery = this.connection.prepareStatement(registerQuery, PreparedStatement.RETURN_GENERATED_KEYS);
        psRegisterQuery.setString(1, this.user.getName());
        psRegisterQuery.setString(2, this.user.getUsername());
        psRegisterQuery.setString(3, hashedPassword);
        int isRegistered = psRegisterQuery.executeUpdate();

        // ResultSet rs = psRegisterQuery.getGeneratedKeys();

        if(isRegistered == 1){
            res = new Response<>("201");
        }
        else{
            res = new Response<>("failed");
        }

        writer.println(gson.toJson(res));
    }

    public void createRoom(Room room) throws Exception {
        Response<String> res;

        SimpleDateFormat ft = new SimpleDateFormat("dd-MM-yyyy");
        String current = ft.format(new Date());

        String insertRoomQuery = "INSERT INTO rooms (name, owner_id, created_at) VALUES (?,?,?)";
        PreparedStatement psInsertRoom = this.connection.prepareStatement(insertRoomQuery, PreparedStatement.RETURN_GENERATED_KEYS);
        psInsertRoom.setString(1, room.getName());
        psInsertRoom.setInt(2, this.user.getId());
        psInsertRoom.setString(3, current);
        psInsertRoom.executeUpdate();

        ResultSet rs = psInsertRoom.getGeneratedKeys();

        int roomId = 0;
        if(rs.next()){
            roomId = rs.getInt(1);
        }

        String joinRoomQuery = "INSERT INTO users_rooms (users_id, rooms_id, joined_at) VALUES (?,?,?)";
        PreparedStatement psJoinRoom = this.connection.prepareStatement(joinRoomQuery, PreparedStatement.RETURN_GENERATED_KEYS);
        psJoinRoom.setInt(1,this.user.getId());
        psJoinRoom.setInt(2, roomId);
        psJoinRoom.setString(3, current);
        int isInserted = psJoinRoom.executeUpdate();

        if(isInserted == 1){
            res = new Response<>(room.getName() + " has been created at " + current);
        }
        else{
            res = new Response<>("500");
        }

        writer.println(gson.toJson(res));
    }

    public void addMember(UserRoom userRoom) throws Exception {
        System.out.println("masuk add member");
        System.out.println("id user ini adalah " + this.user.getId());
        System.out.println("nama room nya adalah " + userRoom.getRoomName());
        Response<String> res; 

        SimpleDateFormat ft = new SimpleDateFormat("dd-MM-yyyy");
        String current = ft.format(new Date());

        int roomId = 0;
        int memberId = 0;

        String checkOwnerQuery = "SELECT * FROM rooms WHERE name = ? && owner_id = ? ";
        PreparedStatement psCheckOwner = this.connection.prepareStatement(checkOwnerQuery, PreparedStatement.RETURN_GENERATED_KEYS);
        psCheckOwner.setString(1, userRoom.getRoomName());
        psCheckOwner.setInt(2,this.user.getId());
        ResultSet rsCheckOwner = psCheckOwner.executeQuery();

        if(rsCheckOwner.next()){
            roomId = rsCheckOwner.getInt(1);
        }

        String getMemberIdQuery = "SELECT id FROM users WHERE name = ?";
        PreparedStatement psGetMemberId = this.connection.prepareStatement(getMemberIdQuery, PreparedStatement.RETURN_GENERATED_KEYS);
        psGetMemberId.setString(1,userRoom.getMemberName());
        ResultSet rsGetMemberId = psGetMemberId.executeQuery();

        if(rsGetMemberId.next()){
            memberId = rsGetMemberId.getInt(1);
            System.out.println("member id adalah : " + memberId);
        }
        //if not owner, break
        // if(!rsCheckOwner.next()) return;
        // if(!rsGetMemberId.next()) return;

        String joinRoomQuery = "INSERT INTO users_rooms (users_id, rooms_id, joined_at) VALUES (?,?,?)";
        PreparedStatement psJoinRoom = this.connection.prepareStatement(joinRoomQuery, PreparedStatement.RETURN_GENERATED_KEYS);
        psJoinRoom.setInt(1, memberId);
        psJoinRoom.setInt(2, roomId);
        psJoinRoom.setString(3, current);
        int isInserted = psJoinRoom.executeUpdate();

        if(isInserted == 1){
            res = new Response<>("Successfully added " + userRoom.getMemberName() + " to " + userRoom.getRoomName());
            writer.println(gson.toJson(res));
        }
        else{
            System.out.println("sorry failed man");
        }

        // check if the one who addMember is the owner of the room
        // check if the member is already inside or not
        // true = proceed to add
        // false = break
    }

    public void logout() throws Exception {
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