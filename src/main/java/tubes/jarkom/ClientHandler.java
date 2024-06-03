package tubes.jarkom;

import tubes.jarkom.env.Env;
import tubes.jarkom.model.Request;
import tubes.jarkom.model.Room;
import tubes.jarkom.model.User;
import tubes.jarkom.model.Response;

import java.io.*;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.sql.*;
import java.util.Enumeration;

import com.google.common.hash.Hashing;
import com.google.gson.Gson;

//bikin register
public class ClientHandler implements Runnable{
    private Socket socket;
    private Gson gson;
    private BufferedReader inFromClient;

    private PrintWriter writer;
    private OutputStream output;

    private User user;

    private Connection connection;
    private Statement statement;

    public ClientHandler(Socket socket){
        this.socket = socket;
        this.gson = new Gson();
    }

    @Override
    public void run() {
        System.out.println(Thread.currentThread().getName() + " is Running!");

        try{
            this.output = this.socket.getOutputStream();
            this.writer = new PrintWriter(output, true);
            this.inFromClient = new BufferedReader(new InputStreamReader(this.socket.getInputStream()));
        }catch (Exception e){
            e.printStackTrace();
        }

        try{
            String jsonMessage = inFromClient.readLine();

            //not using generic due to request generic class may varies.
            @SuppressWarnings("rawtypes")
            Request request = this.gson.fromJson(jsonMessage, Request.class);

            connectDB();

            //since above request is receiving the rawtype non generics object. Compiler doesnt know that the data is String.
            //Hence .toString() method is used.
            switch(request.getAction()){
                case "login":
                    this.user = this.gson.fromJson(request.getData().toString(), User.class);
                    this.login();
                    break;

                case "register":
                    this.user = this.gson.fromJson(request.getData().toString(), User.class);
                    this.register();
                    break;

                case "createRoom":
                    this.createRoom(null);
            }
        }
        catch(Exception e){
            System.out.println(e);
        }
    }

    public void connectDB(){
        try{
            Class.forName("com.mysql.cj.jdbc.Driver");

            this.connection = DriverManager.getConnection(Env.getDBUrl(), Env.getDB_USERNAME(), Env.getDB_PASSWORD());
    
            this.statement = connection.createStatement();

        } catch(Exception e){
            System.out.println(e);
        }
    }

    public void login(){
        try{
            String hashedPassword = Hashing.sha256()
                .hashString(user.getPassword(), StandardCharsets.UTF_8)
                .toString();

            String query = "SELECT * FROM users WHERE username='" + user.getUsername() + "' && password ='"+hashedPassword+"'";

            if(this.execute_query(query) != null){
                this.user.setIsLoggedIn(true);
                System.out.println("login sukses 200");

                Response<String> res = new Response<>("200");

                writer.println(gson.toJson(res));
            }
            else{
                this.user.setIsLoggedIn(false);
            }
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    public void register(){
        try{
            String hashedPassword = Hashing.sha256()
                .hashString(user.getPassword(), StandardCharsets.UTF_8)
                .toString();

            String query = "INSERT INTO users (name, username, password) VALUES ('"+this.user.getName()+"', '"+this.user.getUsername()+"', '"+hashedPassword+"');";

            if(this.execute_update(query)){
                System.out.println("register sukses 201");

                Response<String> res = new Response<>("201");

                writer.println(gson.toJson(res));
            }
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    public void createRoom(Room room){
        try{
            System.out.println("masuk create room");
        }
        catch(Exception e){
            e.printStackTrace();
        }
    }

    public boolean execute_update(String query){
        try{
            int res = this.statement.executeUpdate(query);
            if (res == 1) return true;
        } catch(Exception e){
            e.printStackTrace();
        }
        return false;
    }

    public ResultSet execute_query(String query){
        try{
            ResultSet res = this.statement.executeQuery(query);
            return res;
        } catch(Exception e){
            e.printStackTrace();
        }
        return null;
    }
}