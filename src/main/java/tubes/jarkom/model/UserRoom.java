package tubes.jarkom.model;

public class UserRoom {
    private Integer id;
    private String name;
    private String owner;
    
    public UserRoom(String name, String owner){
        this.name = name;
        this.owner = owner;
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

    public void setOwner(String owner){
        this.owner = owner;
    }

    public String getOwner(){
        return this.owner;
    }
}
