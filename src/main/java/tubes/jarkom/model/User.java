package tubes.jarkom.model;

public class User implements Action {
    private String username;
    private String password;
    private String name;
    String actionName;

    public User(String username, String password){
        this.username = username;
        this.password = password;
    }

    public User(String username, String password, String name){
        this.username = username;
        this.password = password;
        this.name = name;
    }

    public void setUsername(String username){
        this.username = username;
    }

    public String getUsername(){
        return this.username;
    }

    public void setPassword(String password){
        this.password = password;
    }

    public String getPassword(){
        return this.password;
    }

    public void setName(String name){
        this.name = name;
    }

    public String getName(){
        return this.name;
    }

    @Override
    public void setAction(String actionName) {
        this.actionName = actionName;
    }

    @Override
    public String getAction() {
        return this.actionName;
    }
}
