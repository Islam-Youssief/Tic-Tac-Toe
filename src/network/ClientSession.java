package network;

import main.ClientApp;
import model.Player;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.HashMap;
import java.util.Vector;
import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.ButtonType;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import static main.ServerApp.primaryStage;
import model.PlayerDB;

/**
 *
 * @author ISLAM
 */
public class ClientSession {
    public static HashMap<String, Player> allPlayers = new HashMap<String, Player>();
    public Player player;
    private  String player1;
    private  String  player2;
    private Socket socket;
    private final int portNumber;
    private final String ipAddress;
    private ObjectInputStream downLink;
    private ObjectOutputStream upLink;
    public boolean connected = false;
    private boolean loggedin = false;
    public boolean IAmX=false;
    public boolean myTurn;
    private Button[][] btns = {
                {ClientApp.gameController.b1,ClientApp.gameController.b2,ClientApp.gameController.b3},
                {ClientApp.gameController.b4,ClientApp.gameController.b5,ClientApp.gameController.b6},
                {ClientApp.gameController.b7,ClientApp.gameController.b8,ClientApp.gameController.b9}};
    String loginName;
    
    public ClientSession(String ipAddress, int portNumber){
        this.ipAddress = ipAddress;
        this.portNumber = portNumber;
    }
    public void openConnection(){
        try {
            socket = new Socket(ipAddress, portNumber);
            upLink = new ObjectOutputStream(socket.getOutputStream());
            downLink = new ObjectInputStream(socket.getInputStream());
            connected = true;
        } catch (IOException ex) {
            connected = false;
        }
    }
    public void closeConnection(){
        sendRequest(new Handler(Handler.HandType.LOGOUT));
        connected = false;
        try {
            upLink.close();
            downLink.close();
            socket.close();
        } catch (IOException ex) {
        }
    }
    public void terminateConnection(){
        closeConnection();
        Platform.runLater(() -> {
//            islam
            ClientApp.primaryStage.setScene(ClientApp.signIn);
            ClientApp.loginController.terminateConnectino();
        });
    }
    private void startCommunication(){
        new Thread(() -> {
            while(connected){
                try {
                    Handler request = (Handler) downLink.readObject();
                    requestHandle(request);
                } catch (IOException ex) {
                    connected = false;
                    break;
                } catch(ClassNotFoundException cnfex){
                }
            }
            try{
                socket.close();
                downLink.close();
                upLink.close();
            }catch(IOException ex){
            }
        }).start();
    }
    
    private void requestHandle(Handler request){
        switch(request.getType()){
            case INIT:
            case NOTIFY:
                updatePlayersList(request);
                break;
            case GAME_REQUEST:
                respondToRequest(request);
                break;
            case GAME_RESPONSE:
                handleResponse(request);
                break;
            case MOVE:
                handleMove(request);
                break;
            case GAME_OVER:
                handleGameOver(request);
                break;
            case CHAT:
                chatHandler(request);
                break;
            case TERM:
                terminateConnection();
                break;
            default:
                // unhandled request type
                break;
        }
    }
    public boolean loginToServer(String username, String password){
        Handler request = new Handler(Handler.HandType.LOGIN);
        request.setData("login_name", username);
        request.setData("password", password);
        if(connected){
            sendRequest(request);
            while(connected){
                try{
                    Handler response = (Handler)downLink.readObject();
                    if(response.getType() == Handler.HandType.LOGIN){
                        if(response.getData("signal").equals(Handler.SUCCESS)){
                            loggedin = true;
                            player = new Player();
                            player.setLoginName(response.getData("login_name"));
                            player.setFname(response.getData("first_name"));
                            player.setLname(response.getData("last_name"));
                            player.setPicPath(response.getData("pic_path"));
                            player.setTotalPoints(Integer.parseInt(response.getData("score")));
                            startCommunication();
                
                        loginName = request.getData("login_name");
                        // store notification in db
                        Vector<String> allNotifi = new Vector<String>();
                        System.out.println("bhjbhjgh");
                        if (allNotifi.indexOf(loginName) == -1 )
                        {
//                            System.out.println(allNotifi);
                            allNotifi.add(loginName);
                            String statusNotfication = loginName +" Is Online Now";
                            PlayerDB.setNotification(statusNotfication);
                        }                        
                        }
                        break;
                    }else
                        requestHandle(response);
                }catch(IOException ioex){
                }catch(ClassNotFoundException cnfex){
                }
                
            }
            
        }
        return loggedin;
    }
    public boolean playerSignup(String fname, String lname, String username, String password, String picpath) {
        boolean regResult = false;
        Handler request = new Handler(Handler.HandType.REGISTER);
        request.setData("login_name", username);
        request.setData("password", password);
        request.setData("first_name",fname);
        request.setData("last_name",lname);
        request.setData("pic_path",picpath);
        if(connected){
            sendRequest(request);
            while(connected){
                try{
                    Handler response = (Handler)downLink.readObject();
                    if(response.getType() == Handler.HandType.REGISTER){
                        if(response.getData("signal").equals(Handler.SUCCESS)){
                            regResult = true;
                        }
                        break;
                    }
                }catch(IOException ioex){
                }catch(ClassNotFoundException cnfex){
                }
            }
        }
        return regResult;
    }
    private void sendRequest(Handler request){
        try{
            upLink.writeObject(request);
        }catch(Exception ioex){
            System.out.println("open server !");
                
        }
    }
    public void updatePlayersList(Handler request){
        if(!request.getData("login_name").equals(this.player.getLoginName())){
            if(request.getType() == Handler.HandType.INIT){
                Player newPlayer = new Player();
                newPlayer.setLoginName(request.getData("login_name"));
                newPlayer.setStatus(request.getData("status"));
                newPlayer.setTotalPoints(Integer.parseInt(request.getData("score")));
                newPlayer.setPicPath(request.getData("pic_path"));
                allPlayers.put(request.getData("login_name"), newPlayer);
            }else if(request.getType() == Handler.HandType.NOTIFY){
                switch(request.getData("key")){
                    case "status":
                        allPlayers.get(request.getData("login_name")).setStatus(request.getData("value"));
                        
                        break;
                    case "score":
                        allPlayers.get(request.getData("login_name")).setTotalPoints(Integer.parseInt(request.getData("value")));
                        // store notification in dv
                        String scoreNotfication = request.getData("login_name") +" Has New Score : "+request.getData("value");
                        PlayerDB.setNotification(scoreNotfication);
                        break;
                        
                }
            }
            Platform.runLater(ClientApp.homeController::bindPlayersTable);
        }else{
            if(request.getType() == Handler.HandType.NOTIFY && request.getData("key").equals("score")){
                player.setTotalPoints(Integer.parseInt(request.getData("value")));
                Platform.runLater(ClientApp.homeController::playerInfo);
            }
        }
    }
    public void chatHandler(Handler request){
        Platform.runLater(() -> {
            String msg = "~>"+request.getData("sender")+": "+request.getData("text")+"\n";
            ClientApp.gameController.txt_area.appendText(msg);
        });
    }
    public void sendChatMessage(String text){
        if(!text.equals("")){
            Handler request = new Handler(Handler.HandType.CHAT);
            String receiver;
            if(player1 == null)
                receiver = player2;
            else
                receiver = player1;
            request.setData("sender", player.getLoginName());
            request.setData("receiver", receiver);
            request.setData("text", ClientApp.gameController.txt_field.getText());
            sendRequest(request);
        }
    }
    
    public String getOpponentName(){
        if(player2 == null)
            return player1;
        return player2;
    }
}