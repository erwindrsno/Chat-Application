package tubes.jarkom.server;

import java.sql.ResultSet;

public interface IQueryExecutor {
    ResultSet loginQuery(String username, String password);
    boolean registerQuery(String name, String username, String password);
    ResultSet insertRoomQuery(String roomName, int owner_id, String current);
    boolean joinRoomQuery(int usersId, int roomsId, String current);
    ResultSet checkOwnerQuery(String roomName, int userId);
    ResultSet getMemberIdQuery(String name);
    ResultSet getOwnedRoomQuery(int usersId);
    boolean checkDuplicatedNameQuery(String name);
    boolean isMemberInsideQuery(int userId, int roomId);
}
