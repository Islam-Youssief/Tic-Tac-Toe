package network;

import model.Player;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import play_with_computer.Game;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import main.ServerApp;
import static network.Server.allPlayers;

/**
 *
 * @author ISLAM
 */
public class ServerSession extends Thread{
    public static HashMap<String,ServerSession> onlinePlayers = new HashMap<String,ServerSession>();
    private Player player;
    private boolean connected = false;
    private Socket socket;
    private ObjectInputStream downLink;
    private ObjectOutputStream upLink;
    private Game game;
    private static int moveNum = 0;
    
    public ServerSession(Socket socket) throws IOException{
        this.socket = socket;
        downLink = new ObjectInputStream(socket.getInputStream());
        upLink = new ObjectOutputStream(socket.getOutputStream());
        // Starting the server session thread
        start();
    }
    public void run(){
        while(true){
            try{
                Handler request = (Handler)downLink.readObject();
                requestHandle(request);
            }catch(IOException ioex){
                closeConnection();
            }catch(ClassNotFoundException cnfex){
                // error invalid request sent by client
            } catch (SQLException ex) {
                Logger.getLogger(ServerSession.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    private void closeConnection(){
        try{
            connected = false;
            upLink.close();
            downLink.close();
            socket.close();
        }catch(IOException ioex){
            //error connection already closed
        }
    }
    private void requestHandle(Handler request) throws SQLException, ClassNotFoundException{
        switch(request.getType()){
            case LOGIN:
                playerLogin(request);
                break;
            case LOGOUT:
                playerLogout();
                break;
            case REGISTER : 
                playerSignup(request.getData("login_name"), request.getData("password"),
                             request.getData("first_name"),request.getData("last_name"),
                             request.getData("pic_path"));
                break;
            case GAME_REQUEST :
                requestGame(request);
                break;
            case GAME_RESPONSE :
                respondGame(request);
                break;
            case AIGAME_REQUEST :
                AIrequestGame();
                break;
//                islam
            case MOVE:
                handleMove(request);
                break;
            case CHAT:
                chatHandler(request);
                break;
            default:
                //client sent unknown request type
                break;
        }
    }
    private void playerLogin(Handler request) throws SQLException, ClassNotFoundException{
        Handler loginResult = new Handler(Handler.HandType.LOGIN);
        boolean playerAuth = model.PlayerDB.playerAuth(request.getData("login_name"), request.getData("password"));
        if(playerAuth)
        {
            player = model.PlayerDB.getPlayerInfo(request.getData("login_name"));
            loginResult.setData("signal", Handler.SUCCESS);
            loginResult.setData("login_name", player.getLoginName());
            loginResult.setData("first_name", player.getFname());
            loginResult.setData("last_name", player.getLname());
            loginResult.setData("pic_path", player.getPicPath());
            loginResult.setData("score", String.valueOf(player.getTotalPoints()));
            Server.allPlayers.get(player.getLoginName()).setStatus(Handler.ONLINESTATUS);
            ServerSession.onlinePlayers.put(player.getLoginName(), this);
            sendRequest(loginResult);
            initConnection();
            pushNotification("status", Server.allPlayers.get(player.getLoginName()).getStatus());
        }
        else
        {
            loginResult.setData("signal", Handler.FAILURE);
            sendRequest(loginResult);
            connected = false;
        }
    }
    private void initConnection(){
        for(Map.Entry<String, Player> player : allPlayers.entrySet()){
            Handler request = new Handler(Handler.HandType.INIT);
            request.setData("login_name", player.getValue().getLoginName());
            request.setData("score", String.valueOf(player.getValue().getTotalPoints()));
            request.setData("pic_path", player.getValue().getPicPath());
            request.setData("status", player.getValue().getStatus());
            this.sendRequest(request);
        }
    }
    public void pushNotification(String key, String value){
        onlinePlayers.entrySet().forEach((session) -> {
            Handler notification = new Handler(Handler.HandType.NOTIFY);
            notification.setData("login_name", player.getLoginName());
            notification.setData("key", key); //status | score
            notification.setData("value", value);//online|score+5
            session.getValue().sendRequest(notification);
        });
        ServerApp.serverController.refreshPlayersTable();
    }
    private void playerLogout(){
        onlinePlayers.remove(this);
        Server.allPlayers.get(player.getLoginName()).setStatus(Handler.OFFLINESTATUS);
        ServerApp.serverController.refreshPlayersTable();
        pushNotification("status", Server.allPlayers.get(player.getLoginName()).getStatus());
        closeConnection();
    }
    private void playerSignup(String username, String password,String fname,String lname,String picpath) throws SQLException, ClassNotFoundException{
        Handler result = new Handler(Handler.HandType.REGISTER);
        if(!model.PlayerDB.playerExisted(username)){
            if(model.PlayerDB.insertPlayer(fname,lname ,username,password, picpath)){
                result.setData("signal", Handler.SUCCESS);
                String status = "offline";
                Player newPlayer = new Player(fname, lname, username, 0, status,password,picpath);
                newPlayer.setStatus(Handler.OFFLINESTATUS);
                broadcastNewPlayer(newPlayer);
                Server.allPlayers.put(username, newPlayer);
                ServerApp.serverController.refreshPlayersTable();
            }
        }
        else{
           result.setData("signal", Handler.FAILURE);}
        sendRequest(result);
        
    
        }
    }
}
