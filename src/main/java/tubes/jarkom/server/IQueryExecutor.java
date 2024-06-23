package tubes.jarkom.server;

import java.sql.ResultSet;

public interface IQueryExecutor {
    ResultSet loginQuery(String username, String password);
    boolean registerQuery(String name, String username, String password);
    ResultSet insertRoomQuery(String roomName, int owner_id, String current);
    boolean joinRoomQuery(int usersId, int roomsId, String current);
    ResultSet checkOwnerQuery(String roomName, int userId);
    ResultSet checkOwnerQuery(Integer roomId, Integer userId);
    ResultSet getMemberIdQuery(String name);
    ResultSet getOwnedRoomQuery(int usersId);
    String getUserNameById(int userId);
    void sendMessage(String chat, String roomName, int userId, String current);
    boolean checkDuplicatedNameQuery(String name, String username);
    boolean isMemberInsideQuery(int userId, int roomId);
    ResultSet listAllAvailableRooms();
    ResultSet listAllMembersInTheRoom(Integer roomId);
    int getRoomIdByRoomName(String roomName);
    ResultSet listAllChatsInTheRoom(Integer roomId);
    boolean kickMember(Integer memberId, Integer roomId);
}
