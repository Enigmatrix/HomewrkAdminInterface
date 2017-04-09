package app;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Created by enigm on 3/28/2017.
 */
public class DBConnection {
    private static Connection conn;
    private static String url = "jdbc:mysql://localhost:3306/";
    public static Connection connect(String db, String user, String pass) throws SQLException{
        try{
            Class.forName("com.mysql.jdbc.Driver").newInstance();
        }catch(ClassNotFoundException cnfe){
            System.err.println("Error: "+cnfe.getMessage());
        }catch(InstantiationException ie){
            System.err.println("Error: "+ie.getMessage());
        }catch(IllegalAccessException iae){
            System.err.println("Error: "+iae.getMessage());
        }
        conn = DriverManager.getConnection(url+db,user,pass);
        return conn;
    }
    public static Connection getConnection(String db, String user, String pass) throws SQLException, ClassNotFoundException{
        if(conn !=null && !conn.isClosed())
            return conn;
        connect( db,  user, pass);
        return conn;
    }
}
