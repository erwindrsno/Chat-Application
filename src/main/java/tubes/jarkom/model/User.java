package tubes.jarkom.model;

public class User{
    private String username;
    private String password;
    private String name;
    private int id;
    private int userCount;
    private boolean isLoggedIn;
    String actionName;

    public User(){}

    public User(String username, String password){
        this.username = username;
        this.password = password;
        this.isLoggedIn = false;
        userCount++;
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

    public void setId(int id){
        this.id = id;
    }

    public int getId(){
        return this.id;
    }

    public void setIsLoggedIn(boolean isLoggedIn){
        this.isLoggedIn = isLoggedIn;
    }

    public boolean getIsLoggedIn(){
        return this.isLoggedIn;
    }


    // @Override
    // public void setAction(String actionName) {
    //     this.actionName = actionName;
    // }

    // @Override
    // public String getAction() {
    //     return this.actionName;
    // }
}
