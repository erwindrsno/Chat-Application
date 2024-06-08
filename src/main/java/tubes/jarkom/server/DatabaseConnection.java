package tubes.jarkom.server;

import java.sql.Connection;
import java.sql.DriverManager;

import tubes.jarkom.env.Env;

public class DatabaseConnection {
    private Connection connection;

    public DatabaseConnection(){
        connectDB();
    }

    private void connectDB(){
        try{
            Class.forName("com.mysql.cj.jdbc.Driver");
            this.connection = DriverManager.getConnection(Env.getDBURL(), Env.getDBUsername(), Env.getDBPassword());
        }
        catch(Exception e){
            e.printStackTrace();
        }
    }

    public Connection getConnection(){
        return this.connection;
    }
}