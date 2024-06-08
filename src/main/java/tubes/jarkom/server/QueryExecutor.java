package tubes.jarkom.server;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class QueryExecutor implements IQueryExecutor {
    Connection connection;

    public QueryExecutor(Connection connection) {
        this.connection = connection;
    }

    @Override
    public ResultSet loginQuery(String username, String password) {
        String loginQuery = "SELECT * FROM users WHERE username = ? && password = ?";
        try {
            PreparedStatement psLoginQuery = this.connection.prepareStatement(loginQuery,
                    PreparedStatement.RETURN_GENERATED_KEYS);
            psLoginQuery.setString(1, username);
            psLoginQuery.setString(2, password);
            ResultSet rs = psLoginQuery.executeQuery();
            return rs;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public boolean registerQuery(String name, String username, String password) {
        String registerQuery = "INSERT INTO users (name, username, password) VALUES (?,?,?)";
        try {
            PreparedStatement psRegisterQuery = this.connection.prepareStatement(registerQuery,
                    PreparedStatement.RETURN_GENERATED_KEYS);
            psRegisterQuery.setString(1, name);
            psRegisterQuery.setString(2, username);
            psRegisterQuery.setString(3, password);
            int isRegistered = psRegisterQuery.executeUpdate();
            // ResultSet rs = psRegisterQuery.getGeneratedKeys();
            if (isRegistered == 0)
                return false;
            return true;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public ResultSet insertRoomQuery(String roomName, int owner_id, String current) {
        String insertRoomQuery = "INSERT INTO rooms (name, owner_id, created_at) VALUES (?,?,?)";
        try{
            PreparedStatement psInsertRoom = this.connection.prepareStatement(insertRoomQuery, PreparedStatement.RETURN_GENERATED_KEYS);
            psInsertRoom.setString(1, roomName);
            psInsertRoom.setInt(2, owner_id);
            psInsertRoom.setString(3, current);
            psInsertRoom.executeUpdate();

            return psInsertRoom.getGeneratedKeys();
        }
        catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public boolean joinRoomQuery(int usersId, int roomsId, String current) {
        String joinRoomQuery = "INSERT INTO users_rooms (users_id, rooms_id, joined_at) VALUES (?,?,?)";
        try{
            PreparedStatement psJoinRoom = this.connection.prepareStatement(joinRoomQuery, PreparedStatement.RETURN_GENERATED_KEYS);
            psJoinRoom.setInt(1, usersId);
            psJoinRoom.setInt(2, roomsId);
            psJoinRoom.setString(3, current);
            int isInserted = psJoinRoom.executeUpdate();
            if(isInserted == 1) return true;
            return false;
        }
        catch (Exception e){
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public ResultSet checkOwnerQuery(String roomName, int userId) {
        String checkOwnerQuery = "SELECT * FROM rooms WHERE name = ? && owner_id = ? ";
        try{
            PreparedStatement psCheckOwner = this.connection.prepareStatement(checkOwnerQuery,
            PreparedStatement.RETURN_GENERATED_KEYS);
            psCheckOwner.setString(1, roomName);
            psCheckOwner.setInt(2, userId);
            ResultSet rsCheckOwner = psCheckOwner.executeQuery();
            return rsCheckOwner;
        }
        catch(Exception e){
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public ResultSet getMemberIdQuery(String name) {
        try{
            String getMemberIdQuery = "SELECT id FROM users WHERE name = ?";
            PreparedStatement psGetMemberId = this.connection.prepareStatement(getMemberIdQuery,
                    PreparedStatement.RETURN_GENERATED_KEYS);
            psGetMemberId.setString(1, name);
            ResultSet rsGetMemberId = psGetMemberId.executeQuery();
            return rsGetMemberId;
        }
        catch(Exception e){
            e.printStackTrace();
            return null;
        }
    }
}
