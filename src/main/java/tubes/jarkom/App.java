package tubes.jarkom;

/**
 * Hello world!
 *
 */

 //TODO:
 //1. Bikin database
 //2. Bikin model(?)
 //3. Bikin Front end
public class App 
{
    public static void main(String[] args){
        Thread c1 = new Thread(new Client(), "Client-1");
        Thread c2 = new Thread(new Client(), "Client-2");

        c1.start();
        // c2.start();

    }
}