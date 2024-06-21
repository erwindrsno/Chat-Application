package tubes.jarkom.model;

public class Chat {
    private String chats;
    private String room_name;
    private String sender_name;

    public Chat(String chats, String room_name, String sender_name){
        this.chats = chats;
        this.room_name = room_name;
        this.sender_name = sender_name;
    }

    public String getChats() {
        return chats;
    }

    public void setChats(String chats) {
        this.chats = chats;
    }

    public String getRoom_name() {
        return this.room_name;
    }

    public void setRoom_name(String room_name) {
        this.room_name = room_name;
    }

    public String getSender_name() {
        return this.sender_name;
    }

    public void setSender_id(String sender_name) {
        this.sender_name = sender_name;
    }
}
