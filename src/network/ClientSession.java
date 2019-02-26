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
    private void sendRequest(Handler request){
        try{
            upLink.writeObject(request);
        }catch(Exception ioex){
            System.out.println("open server !");
                
        }
    }
    public void chatHandler(Handler request){
        Platform.runLater(() -> {
            String msg = "~>"+request.getData("sender")+": "+request.getData("text")+"\n";
            ClientApp.gameController.txt_area.appendText(msg);
        });
    }
    
    public void sendResponse(boolean response){
        IAmX=false;
        Handler outgoing=new Handler(Handler.HandType.GAME_RESPONSE,"destination",player1);
        outgoing.setData("response",response?"accept":"deny");
        sendRequest(outgoing);
    }
    public void handleResponse(Handler incoming){
        if(incoming.getData("response").equals("accept")){
            IAmX = true;
            myTurn = true;
            player2 = incoming.getData("source");
            Platform.runLater(() -> {
                ClientApp.primaryStage.setScene(main.ClientApp.game);
                ClientApp.gameController.resetScene();
                ClientApp.gameController.img = new Image(ClientSession.this.getClass().getResourceAsStream("/resources/images/x.png"));
            });
        }else{
            //other player rejected request
        }
    }
    
    public String getOpponentName(){
        if(player2 == null)
            return player1;
        return player2;
    }
}