package tubes.jarkom.model;

public class UserRoom {
    private Integer id;
    private String memberName;
    private String roomName;
    
    public UserRoom(String memberName, String roomName){
        this.memberName = memberName;
        this.roomName = roomName;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public void setMemberName(String memberName){
        this.memberName = memberName;
    }

    public String getMemberName(){
        return this.memberName;
    }

    public void setRoomName(String roomName){
        this.roomName = roomName;
    }

    public String getRoomName(){
        return this.roomName;
    }
}
