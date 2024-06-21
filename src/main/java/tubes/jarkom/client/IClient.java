package tubes.jarkom.client;

public interface IClient {
    void register(String username, String password, String name);
    void login(String username, String password);
    void sendMessage(String message, String roomName);
    void createRoom(String roomName);
    void addMember(String memberName, String roomName);
    void logout();
    void exit();
}