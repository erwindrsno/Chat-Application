package tubes.jarkom.model;

public class Response <T> {
    T data;

    public Response(T data){
        this.data = data;
    }

    public void setData(T data){
        this.data = data;
    }

    public T getData(){
        return this.data;
    }
}
