package tubes.jarkom.model;

public class User{
    private Integer id;
    private String username;
    private String password;
    private String name;
    private boolean isLoggedIn;

    public User(){}

    public User(String username, String password){
        this.username = username;
        this.password = password;
        this.isLoggedIn = false;
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

    public void setId(Integer id){
        this.id = id;
    }

    public Integer getId(){
        return this.id;
    }

    public void setIsLoggedIn(boolean isLoggedIn){
        if(!isLoggedIn){
            this.setId(null);
            this.setUsername(null);
            this.setPassword(null);
            this.setName(null);
        }
        this.isLoggedIn = isLoggedIn;
    }

    public boolean getIsLoggedIn(){
        return this.isLoggedIn;
    }
}
