package tubes.jarkom.server;

import tubes.jarkom.request.Request;
import tubes.jarkom.model.Room;
import tubes.jarkom.model.User;
import tubes.jarkom.model.UserRoom;
import tubes.jarkom.response.Response;
import tubes.jarkom.model.Chat;

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

                    // case "addMember":
                    // this.addMember(this.gson.fromJson(request.getData().toString(),
                    // UserRoom.class));
                    // break;

                    case "joinRoom":
                        this.joinRoom(this.gson.fromJson(request.getData().toString(), UserRoom.class));
                        break;

                    case "isMemberInside":
                        this.isMemberInside(this.gson.fromJson(request.getData().toString(), UserRoom.class));
                        break;

                    case "isOwnerOfTheRoom":
                        this.isOwnerOfTheRoom(this.gson.fromJson(request.getData().toString(), Room.class));
                        break;

                    case "kickMember":
                        this.kickMember(this.gson.fromJson(request.getData().toString(), UserRoom.class));
                        break;

                    case "sendMessage":
                        this.sendMessage(this.gson.fromJson(request.getData().toString(), Chat.class));
                        break;

                    case "listAllChatsInTheRoom":
                        this.listAllChatsInTheRoom(this.gson.fromJson(request.getData().toString(), Integer.class));
                        break;

                    case "listAllRooms":
                        this.listAllRooms();
                        break;

                    case "listAllMembersInTheRoom":
                        this.listAllMembersInTheRoom(this.gson.fromJson(request.getData().toString(), Integer.class));
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

        res = new Response<>(rsLogin.getInt("id") + "");

        System.out.println(this.user.getName() + " has logged in");

        writer.println(gson.toJson(res));
    }

    public void register() throws Exception {
        String hashedPassword = Hashing.sha256()
                .hashString(this.user.getPassword(), StandardCharsets.UTF_8)
                .toString();

        if (this.qe.checkDuplicatedNameQuery(this.user.getName(), this.user.getUsername())) {
            Response<String> res = new Response<>("400");
            writer.println(gson.toJson(res));
            return;
        }

        boolean isRegistered = this.qe.registerQuery(this.user.getName(), this.user.getUsername(), hashedPassword);

        Response<String> res = isRegistered ? new Response<>("201") : new Response<>("400");
        writer.println(gson.toJson(res));
    }

    public void createRoom(Room room) throws Exception {
        SimpleDateFormat ft = new SimpleDateFormat("dd-MM-yyyy");
        String current = ft.format(new Date());
        int roomId = 0;

        if (this.isDuplicatedRoom(room.getName())) {
            Response<String> res = new Response<>("500");
            writer.println(gson.toJson(res));
            return;
        }

        ResultSet rs = this.qe.insertRoomQuery(room.getName(), this.user.getId(), current);

        this.ownedRooms.add(room.getName());

        if (rs.next()) {
            roomId = rs.getInt(1);
        }

        boolean hasJoinedRoom = this.qe.joinRoomQuery(this.user.getId(), roomId, current);

        Response<String> res = hasJoinedRoom ? new Response<>("200")
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

        if (this.qe.isMemberInsideQuery(memberId, roomId)) {
            Response<String> res = new Response<>("Sorry, that guy is one of us already!");
            writer.println(gson.toJson(res));
            return;
        }

        boolean hasJoinedRoom = this.qe.joinRoomQuery(memberId, roomId, current);

        Response<String> res = hasJoinedRoom
                ? new Response<>(userRoom.getMemberName() + " has joined " + userRoom.getRoomName() + " at " + current)
                : new Response<>("500");

        writer.println(gson.toJson(res));
    }

    public void joinRoom(UserRoom userRoom) throws Exception {
        SimpleDateFormat ft = new SimpleDateFormat("dd-MM-yyyy");
        String current = ft.format(new Date());

        int roomId = this.qe.getRoomIdByRoomName(userRoom.getRoomName());
        int memberId = userRoom.getUserId();

        // System.out.println("room id : " + roomId);
        // System.out.println("member id : " + memberId);

        if (this.qe.isMemberInsideQuery(memberId, roomId)) {
            Response<String> res = new Response<>("400");
            writer.println(gson.toJson(res));
            return;
        }

        boolean hasJoinedRoom = this.qe.joinRoomQuery(memberId, roomId, current);

        Response<String> res = hasJoinedRoom
                ? new Response<>("200")
                : new Response<>("500");

        writer.println(gson.toJson(res));
    }

    public void isMemberInside(UserRoom userRoom) {
        // System.out.println("userid nyaaa : " + userRoom.getUserId());
        // System.out.println("roomid nyaaa : " + userRoom.getId());
        // this.qe.isMemberInsideQuery(userRoom.getUserId(), userRoom.getId());

        Response<Boolean> res = this.qe.isMemberInsideQuery(userRoom.getUserId(), userRoom.getId())
                ? new Response<>(true)
                : new Response<>(false);

        writer.println(gson.toJson(res));
    }

    public void sendMessage(Chat message) throws Exception {
        SimpleDateFormat ft = new SimpleDateFormat("dd-MM-yyyy");
        String current = ft.format(new Date());

        System.out.println("Message nya : " + message.getChats());
        System.out.println("Kirim ke room : " + message.getRoom_name());
        System.out.println("user id nya itu : " + this.user.getId());

        this.qe.sendMessage(message.getChats(), message.getRoom_name(), this.user.getId(), current);
    }

    public void logout() throws Exception {
        this.user.setIsLoggedIn(false);

        Response<String> res = new Response<>("log out succeed");

        writer.println(gson.toJson(res));
    }

    public void listAllRooms() throws Exception {
        ResultSet rs = this.qe.listAllAvailableRooms();
        ArrayList<Room> alRoom = new ArrayList<>();
        while (rs.next()) {
            alRoom.add(new Room(rs.getInt("id"), rs.getString("name"), this.qe.getUserNameById(rs.getInt("owner_id"))));
        }
        Response<ArrayList<Room>> res = new Response<>(alRoom);

        writer.println(gson.toJson(res));
    }

    public void listAllMembersInTheRoom(Integer roomId) throws Exception {
        ResultSet rs = this.qe.listAllMembersInTheRoom(roomId);
        ArrayList<User> alUserPerRoom = new ArrayList<>();
        while (rs.next()) {
            alUserPerRoom.add(new User(rs.getInt("id"), rs.getString("name")));
        }
        Response<ArrayList<User>> res = new Response<>(alUserPerRoom);

        writer.println(gson.toJson(res));
    }

    public void listAllChatsInTheRoom(Integer roomId) throws Exception {
        ResultSet rs = this.qe.listAllChatsInTheRoom(roomId);
        ArrayList<Chat> alChatPerRoom = new ArrayList<>();
        while (rs.next()) {
            alChatPerRoom.add(new Chat(rs.getString("chats"), rs.getString("name"), rs.getInt("id")));
            // System.out.println(rs.getString("chats"));
            // System.out.println(rs.getString("name"));
        }
        // Response<ArrayList<Chat>> res = new Response<>(alChatPerRoom);
        String strAlChatPerRoom = gson.toJson(alChatPerRoom);

        Response<String> res = new Response<>(strAlChatPerRoom);

        writer.println(gson.toJson(res));
    }

    public void kickMember(UserRoom userRoom) throws Exception {
        // System.out.println("to be kick id : " + userRoom.getUserId());
        // System.out.println("room : " + userRoom.getId());
        // boolean hasKicked = this.qe.kickMember(userRoom.getUserId(),
        // userRoom.getId());
        Response<Boolean> res = this.qe.kickMember(userRoom.getUserId(), userRoom.getId()) ? new Response<>(true)
                : new Response<>(false);
        writer.println(gson.toJson(res));
    }

    public void isOwnerOfTheRoom(Room room) throws Exception {
        ResultSet rs = this.qe.checkOwnerQuery(room.getId(), room.getOwner_id());
        while (rs.next()) {
            Response<Boolean> res = new Response<>(true);
            writer.println(gson.toJson(res));
            return;
        }
        Response<Boolean> res = new Response<>(false);
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