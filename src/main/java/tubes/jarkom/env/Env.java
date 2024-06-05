package tubes.jarkom.env;

import io.github.cdimascio.dotenv.Dotenv;

public class Env {
    private static final Dotenv dotenv = Dotenv.load();

    public static String get(String key){
        return dotenv.get(key);
    }

    public static String getServerName(){
        return dotenv.get("SERVER_NAME");
    }

    public static String getDBName(){
        return dotenv.get("DB_NAME");
    }

    public static String getDBURL(){
        return dotenv.get("DB_URL");
    }

    public static String getDBUsername(){
        return dotenv.get("DB_USERNAME");
    }

    public static String getDBPassword(){
        return dotenv.get("DB_PASSWORD");
    }

    public static String getServerIP(){
        return dotenv.get("SERVER_IP");
    }

    public static int getPort(){
        int port = Integer.parseInt(dotenv.get("PORT"));
        return port;
    }
}
