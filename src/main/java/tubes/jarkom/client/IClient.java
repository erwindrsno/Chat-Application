package tubes.jarkom.client;

public interface IClient {
    void register(String username, String password, String name);
    void login(String username, String password);
    void sendMessage();
    void createRoom(String roomName);
    void logOut();
    void exit();
}