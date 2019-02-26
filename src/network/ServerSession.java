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
    public void sendRequest(Handler request){
        try{
            this.upLink.writeObject(request);
        }catch(IOException ioex){
            //error cannot send request to client
        }
    }
    public void chatHandler(Handler request){
        onlinePlayers.get(request.getData("sender")).sendRequest(request);
        if(!request.getData("sender").equals(request.getData("receiver"))){
            if(onlinePlayers.containsKey(request.getData("receiver")))
                onlinePlayers.get(request.getData("receiver")).sendRequest(request);    
        }            
    }
    private void broadcastNewPlayer(Player newPlayer){
        onlinePlayers.entrySet().forEach((session) -> {
            Handler request = new Handler(Handler.HandType.INIT);
            request.setData("login_name", newPlayer.getLoginName());
            request.setData("score", String.valueOf(newPlayer.getTotalPoints()));
            request.setData("pic_path", newPlayer.getPicPath());
            request.setData("status", newPlayer.getStatus());
            session.getValue().sendRequest(request);
        });
    }
    
    public void requestGame(Handler incoming){
        //handle request from client 1 and forward it to client2
        Handler outgoing=new Handler(Handler.HandType.GAME_REQUEST,"source",player.getLoginName());
        if(onlinePlayers.containsKey(incoming.getData("destination"))){
            onlinePlayers.get(incoming.getData("destination")).sendRequest(outgoing);
            
            ServerApp.server.allPlayers.get(player.getLoginName()).setStatus(Handler.BUSYSTATUS);
            ServerSession.onlinePlayers.get(player.getLoginName()).pushNotification("status", Handler.BUSYSTATUS);
            ServerApp.server.allPlayers.get(incoming.getData("destination")).setStatus(Handler.BUSYSTATUS);
            ServerSession.onlinePlayers.get(incoming.getData("destination")).pushNotification("status", Handler.BUSYSTATUS);
            ServerApp.serverController.refreshPlayersTable();
        }
    }
    
    public void respondGame(Handler incoming)
    {
        //handle response from client 2 and forward it to client1
        if(incoming.getData("response").equals("accept"))
        {
                game=new Game(incoming.getData("destination"),player.getLoginName());
                onlinePlayers.get(incoming.getData("destination")).game=game;
        }
        else
        {
            ServerApp.server.allPlayers.get(player.getLoginName()).setStatus(Handler.ONLINESTATUS);
            ServerSession.onlinePlayers.get("destination").pushNotification("status", Handler.ONLINESTATUS);
            ServerApp.serverController.refreshPlayersTable();
        }
        Handler outgoing=new Handler(Handler.HandType.GAME_RESPONSE,"source",player.getLoginName());
        outgoing.setData("response", incoming.getData("response"));
        if(onlinePlayers.containsKey(incoming.getData("destination"))){
            onlinePlayers.get(incoming.getData("destination")).sendRequest(outgoing);        
        }
    }
    
    private void AIrequestGame()
    {
        ServerApp.server.allPlayers.get(player.getLoginName()).setStatus(Handler.BUSYSTATUS);
        ServerSession.onlinePlayers.get(player.getLoginName()).pushNotification("status", Handler.BUSYSTATUS);
        ServerApp.serverController.refreshPlayersTable();
    }
    private void handleMove(Handler request) throws ClassNotFoundException {
         if(request.getData("target")!=null&&request.getData("target").equals("computer")){
                System.out.println("Testing ....");
         }
         else
         {
            if(game.validateMove(player.getLoginName(), Integer.parseInt(request.getData("x")), Integer.parseInt(request.getData("y")))){
                switch (game.checkForWin(player.getLoginName(), Integer.parseInt(request.getData("x")), Integer.parseInt(request.getData("y"))))
                {
                    case "gameOn":
                        if(game.incMove%2==0)
                            onlinePlayers.get(game.getPlayer1()).sendRequest(request);
                        else
                            onlinePlayers.get(game.getPlayer2()).sendRequest(request);
                        break;
                    case "win" :
                        sendRequest(new Handler(Handler.HandType.GAME_OVER,"line","You win !"));
                        Handler lose=new Handler(Handler.HandType.GAME_OVER,"line","You lose !");
                        String username=player.getLoginName();
                        model.PlayerDB.updateScoreWin(username);
                        ServerApp.server.allPlayers.get(this.player.getLoginName()).setTotalPoints(ServerApp.server.allPlayers.get(this.player.getLoginName()).getTotalPoints()+10);
                        ServerApp.serverController.refreshPlayersTable();
                        lose.setData("x", request.getData("x"));
                        lose.setData("y", request.getData("y"));
                        onlinePlayers.get(game.incMove%2==1?game.getPlayer1():game.getPlayer2()).sendRequest(lose);
                        ServerApp.server.allPlayers.get(game.getPlayer1()).setStatus(Handler.ONLINESTATUS);
                        ServerSession.onlinePlayers.get(game.getPlayer1()).pushNotification("status", Handler.ONLINESTATUS);
                        ServerApp.server.allPlayers.get(game.getPlayer2()).setStatus(Handler.ONLINESTATUS);
                        ServerSession.onlinePlayers.get(game.getPlayer2()).pushNotification("status", Handler.ONLINESTATUS);
                        ServerApp.serverController.refreshPlayersTable();
                        game=null;
                        break;
                    case "draw":
                        sendRequest(new Handler(Handler.HandType.GAME_OVER,"line","Draw !"));
                        Handler draw=new Handler(Handler.HandType.GAME_OVER,"line","Draw !");
                        String username2=player.getLoginName();
                        model.PlayerDB.updateScoreDraw(username2);
                       
                        ServerApp.server.allPlayers.get(this.player.getLoginName()).setTotalPoints(ServerApp.server.allPlayers.get(this.player.getLoginName()).getTotalPoints()+5);
                        draw.setData("x", request.getData("x"));
                        draw.setData("y", request.getData("y"));
                        onlinePlayers.get(game.incMove%2==1?game.getPlayer1():game.getPlayer2()).sendRequest(draw);
                        ServerApp.server.allPlayers.get(game.getPlayer1()).setStatus(Handler.ONLINESTATUS);
                        ServerSession.onlinePlayers.get(game.getPlayer1()).pushNotification("status", Handler.ONLINESTATUS);
                        ServerApp.server.allPlayers.get(game.getPlayer2()).setStatus(Handler.ONLINESTATUS);
                        ServerSession.onlinePlayers.get(game.getPlayer2()).pushNotification("status", Handler.ONLINESTATUS);
                        ServerApp.serverController.refreshPlayersTable();
                        game=null;
                        break;
                }
            }
        

        }
    }
}
