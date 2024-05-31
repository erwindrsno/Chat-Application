package tubes.jarkom.model;

public class Request <T> {
    String action;
    T data;

    public Request(String action, T data){
        this.action = action;
        this.data = data;
    }

    public void setAction(String action){
        this.action = action;
    }

    public String getAction(){
        return this.action;
    }

    public void setData(T data){
        this.data = data;
    }

    public T getData(){
        return this.data;
    }
}
