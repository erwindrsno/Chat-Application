package tubes.jarkom.model;

public class Room {
    private int id;
    private String name;
    private String owner;
    private Integer owner_id;

    public Room(String name, String owner){
        this.name = name;
        this.owner = owner;
    }

    public Room(int id, String name, String owner, Integer owner_id){
        this.id = id;
        this.name = name;
        this.owner = owner;
        this.owner_id = owner_id;
    }

    public Room(int id, int owner_id){
        this.id = id;
        this.owner_id = owner_id;
    }

    public void setName(String name){
        this.name = name;
    }

    public String getName(){
        return this.name;
    }

    public void setOwner(String owner){
        this.owner = owner;
    }

    public String getOwner(){
        return this.owner;
    }

    public int getId() {
        return this.id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Integer getOwner_id() {
        return owner_id;
    }

    public void setOwner_id(Integer owner_id) {
        this.owner_id = owner_id;
    }
}
