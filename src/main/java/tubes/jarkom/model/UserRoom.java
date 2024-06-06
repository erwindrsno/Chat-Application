package tubes.jarkom.model;

public class UserRoom {
    private Integer id;
    private String name;
    private String roomName;
    
    public UserRoom(String name, String roomName){
        this.name = name;
        this.roomName = roomName;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public void setName(String name){
        this.name = name;
    }

    public String getName(){
        return this.name;
    }

    public void setOwner(String roomName){
        this.roomName = roomName;
    }

    public String getOwner(){
        return this.roomName;
    }
}
