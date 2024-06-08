package tubes.jarkom.server;

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
import java.util.ArrayList;
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
    private ArrayList<String> ownedRooms;

    private boolean flag;

    private IQueryExecutor qe;

    private DatabaseConnection db;

    private static int clientCount = 0;

    public ClientHandler(Socket socket) {
        this.socket = socket;
        this.gson = new Gson();
        this.ownedRooms = new ArrayList<>();
        this.flag = true;
        this.db = new DatabaseConnection();
        this.qe = new QueryExecutor(this.db.getConnection());
    }

    @Override
    public void run() {
        System.out.println(Thread.currentThread().getName() + " is Running!");
        System.out.println("Total client is : " + clientCount);

        clientCount++;

        try {
            this.output = this.socket.getOutputStream();
            this.writer = new PrintWriter(output, true);
            this.inFromClient = new BufferedReader(new InputStreamReader(this.socket.getInputStream()));

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

    public void login() throws Exception {
        String hashedPassword = Hashing.sha256()
                .hashString(this.user.getPassword(), StandardCharsets.UTF_8)
                .toString();

        Response<String> res;

        ResultSet rsLogin = this.qe.loginQuery(this.user.getUsername(), hashedPassword);

        // .next() method move the cursor for one record.
        // set name because during login, the object that is send by client doesnt have
        // name inside the user object.
        if (!rsLogin.next()) {
            res = new Response<>("401");
            this.user.setIsLoggedIn(false);
            writer.println(gson.toJson(res));
            return;
        }
        this.user.setName(rsLogin.getString(2));
        this.user.setIsLoggedIn(true);
        this.user.setId(rsLogin.getInt(1));

        ResultSet rsGetOwnedRoom = this.qe.getOwnedRoomQuery(this.user.getId());

        if (!rsGetOwnedRoom.next()) {
            System.out.println(this.user.getName() + " doesn't have own any rooms");
        } else {
            do {
                this.ownedRooms.add(rsGetOwnedRoom.getString(1));
            } while (rsGetOwnedRoom.next());
        }

        res = new Response<>("200");

        System.out.println(this.user.getName() + " has logged in");

        writer.println(gson.toJson(res));
    }

    public void register() throws Exception {
        String hashedPassword = Hashing.sha256()
                .hashString(this.user.getPassword(), StandardCharsets.UTF_8)
                .toString();

        System.out.println(this.user.getName());
        boolean isDuplicatedName = this.qe.checkDuplicatedNameQuery(this.user.getName());

        if(isDuplicatedName){
            Response<String> res = new Response<>("Sorry, someone took that cool name, try another name!");
            writer.println(gson.toJson(res));
            return;
        }

        boolean isRegistered = this.qe.registerQuery(this.user.getName(), this.user.getUsername(), hashedPassword);

        Response<String> res = isRegistered ? new Response<>("201") : new Response<>("failed");
        writer.println(gson.toJson(res));
    }

    public void createRoom(Room room) throws Exception {
        SimpleDateFormat ft = new SimpleDateFormat("dd-MM-yyyy");
        String current = ft.format(new Date());

        if (this.isDuplicatedRoom(room.getName())) {
            Response<String> res = new Response<>("Sorry, that is a duplicated room name, try another name!");
            writer.println(gson.toJson(res));
            return;
        }

        ResultSet rs = this.qe.insertRoomQuery(room.getName(), this.user.getId(), current);

        this.ownedRooms.add(room.getName());

        int roomId = 0;
        if (rs.next()) {
            roomId = rs.getInt(1);
        }

        boolean hasJoinedRoom = this.qe.joinRoomQuery(this.user.getId(), roomId, current);

        Response<String> res = hasJoinedRoom ? new Response<>(room.getName() + " has been created at " + current)
                : new Response<>("500");

        writer.println(gson.toJson(res));
    }

    public void addMember(UserRoom userRoom) throws Exception {
        SimpleDateFormat ft = new SimpleDateFormat("dd-MM-yyyy");
        String current = ft.format(new Date());

        int roomId = 0;
        int memberId = 0;

        ResultSet rsCheckOwner = this.qe.checkOwnerQuery(userRoom.getRoomName(), this.user.getId());

        if (rsCheckOwner.next()) {
            roomId = rsCheckOwner.getInt(1);
        }

        ResultSet rsGetMemberId = this.qe.getMemberIdQuery(userRoom.getMemberName());

        if (rsGetMemberId.next()) {
            memberId = rsGetMemberId.getInt(1);
            System.out.println("member id adalah : " + memberId);
        } else {
            System.out.println("no member with this id");
        }

        boolean hasJoinedRoom = this.qe.joinRoomQuery(memberId, roomId, current);

        Response<String> res = hasJoinedRoom
                ? new Response<>(userRoom.getMemberName() + " has joined " + userRoom.getRoomName() + " at " + current)
                : new Response<>("500");

        writer.println(gson.toJson(res));
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

    private boolean isDuplicatedRoom(String roomName) {
        for (int i = 0; i < this.ownedRooms.size(); i++) {
            if (roomName.equals(this.ownedRooms.get(i))) {
                return true;
            }
        }
        return false;
    }
}