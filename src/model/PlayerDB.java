
package model;

import static com.sun.org.apache.xalan.internal.lib.ExsltDatetime.date;
import static com.sun.org.apache.xalan.internal.lib.ExsltDatetime.date;
import static com.sun.org.apache.xalan.internal.lib.ExsltDatetime.dateTime;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DateFormat;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
import static javax.xml.datatype.DatatypeConstants.DATETIME;

/**
 *
 * @author ISLAM
 */
public class PlayerDB {
    
    private static Connection connection;
    
    
    public static Connection Connect() throws SQLException, ClassNotFoundException{
         // make sure to import the connector
        Class.forName("com.mysql.jdbc.Driver");
        // All The Credentials
        String port = "3306";
        String DBName = "TicTacToe";
        String DBAdmin = "root";
        String DBAdminPassword="";        
        connection = DriverManager.getConnection("jdbc:mysql://localhost:"+port+"/"+DBName, DBAdmin, DBAdminPassword);
        return connection;
    }
  
    public static void CloseConnection(Connection connection) throws SQLException{
        connection.close();
    }
    
    
    public static  HashMap<String, Player> getAllPlayers() {
        try{
        HashMap<String, Player> hashmap = new HashMap<>();
            Connection conn = Connect();
            Statement stmt = conn.createStatement();
            String queryString = "select * from player";
            ResultSet rs = stmt.executeQuery(queryString);
            while (rs.next()) {
                Player p = new Player(rs.getString(2), rs.getString(3), rs.getString(4), rs.getInt(5), rs.getString(6), rs.getString(7),rs.getInt(8),rs.getString(9));
                p.setStatus(network.Handler.OFFLINESTATUS);
               hashmap.put(rs.getString("login_name"), p);
            }
            stmt.close();
            CloseConnection(conn);
             return hashmap;
        }
        catch(Exception e)
        {
            e.printStackTrace();
            return null;
        }
    }
    
    public static Player getPlayerInfo(String username) throws ClassNotFoundException, SQLException {
        Player player = new Player();
        Connection conn = Connect();
        Statement stmt = conn.createStatement();
        String queryString = "SELECT * FROM player WHERE login_name = '" + username + "'";
        ResultSet rs = stmt.executeQuery(queryString);
        while (rs.next())
        {
            player.setFname(rs.getString("first_name"));
            player.setLname(rs.getString("last_name"));
            player.setLoginName(rs.getString("login_name"));
            player.setTotalPoints(rs.getInt("total_points"));
            player.setPicPath(rs.getString("pic_path"));
            player.setStatus(rs.getString("status"));
        }
        stmt.close();
        CloseConnection(conn);
        return player;
    }
    
  
    public static boolean playerExisted(String username) throws SQLException, ClassNotFoundException {
        Connection conn = Connect();
        Statement stmt = conn.createStatement();
        String queryString = "select * from player where login_name ='" + username + "'";
        ResultSet rs = stmt.executeQuery(queryString);
        if (rs.next()) {
            stmt.close();
            CloseConnection(conn);
            return true;
        }
        stmt.close();
        CloseConnection(conn);
        return false;
    }
    
    public static boolean playerAuth(String username, String password) throws SQLException, ClassNotFoundException {
        boolean validAuth = false;
        if (playerExisted(username)) {   
                Connection conn = Connect();
                Statement stmt = conn.createStatement();
                String queryString = "select * from player where login_name ='" + username + "' and password='" + password + "'";
                ResultSet rs = stmt.executeQuery(queryString);
                if (rs.next()) {
                    validAuth = true;
                }
                stmt.close();
                CloseConnection(conn);           
        }
        return validAuth;
    }
    
    
    public static boolean updateScoreWin(String username) throws ClassNotFoundException {
        try {
            Connection conn = Connect();
            Statement stmt = conn.createStatement();
            String queryString = "UPDATE `player` SET `total_points`= total_points+10  WHERE login_name = '" + username + "' ";            
            stmt.executeUpdate(queryString);
            stmt.close();
            CloseConnection(conn);
            return true;
        } catch (SQLException ex) {
            ex.printStackTrace();
            System.out.println("Failed to update user score !");
            return false;
        }
    }
     public static boolean updateScoreDraw(String username) throws ClassNotFoundException {
        try {
            Connection conn = Connect();
            Statement stmt = conn.createStatement();
            String queryString = "UPDATE `player` SET `total_points`= total_points+5  WHERE login_name = '" + username + "' ";            
            stmt.executeUpdate(queryString);
            stmt.close();
            CloseConnection(conn);
            return true;
        } catch (SQLException ex) {
            ex.printStackTrace();
            System.out.println("Failed to update user score !");
            return false;
        }
    }
     
     
}
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    


