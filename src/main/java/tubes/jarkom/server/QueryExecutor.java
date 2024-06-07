package tubes.jarkom.server;

import java.sql.Connection;

public class QueryExecutor implements IQueryExecutor{
    Connection conn;

    public QueryExecutor(Connection conn){
        this.conn = conn;
    }

    @Override
    public void loginQuery(){

    }
}
