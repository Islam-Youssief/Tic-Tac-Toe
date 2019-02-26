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
    public void requestGame(String secondPlayerName){
        //**ALERT** waiting for other player response with cancel button
        Handler request=new Handler(Handler.HandType.GAME_REQUEST,"destination",secondPlayerName);
        sendRequest(request);
    }
    public void respondToRequest(Handler incoming){
        //**Alert** with the request from **playerRequestingGame** returns boolean **accept**
        player1=incoming.getData("source");
        Platform.runLater(() -> {
            ClientApp.homeController.showAlert(player1);
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
    public void playWithAI(){
        ClientApp.gameController.resetScene();
        sendRequest(new Handler(Handler.HandType.AIGAME_REQUEST));
        player1 = player.getLoginName();
        player2 = "computer";
        IAmX = true;
        myTurn = true;
        ClientApp.gameController.img = new Image(getClass().getResourceAsStream("/resources/images/x.png"));
    }
    public void makeAMove(String x,String y) {
        myTurn = false;
        Handler request=new Handler(Handler.HandType.MOVE);
        request.setData("x", x);
        request.setData("y", y);
        request.setData("target", player2);
        sendRequest(request);
    }
    private void handleMove(Handler request) {
        myTurn = true;
        Platform.runLater(() -> {
            btns[Integer.parseInt(request.getData("x"))][Integer.parseInt(request.getData("y"))].setGraphic(new ImageView(IAmX?"/resources/images/o.png":"/resources/images/x.png"));
            if(Integer.parseInt(request.getData("x")) == 0){
                if(Integer.parseInt(request.getData("y")) == 0){
                    ClientApp.gameController.flag1 = 1;
                }else if(Integer.parseInt(request.getData("y")) == 1){
                    ClientApp.gameController.flag2 = 1;
                }
                else{
                    ClientApp.gameController.flag3 = 1;
                }
            }else if(Integer.parseInt(request.getData("x")) == 1){
                if(Integer.parseInt(request.getData("y")) == 0){
                    ClientApp.gameController.flag4 = 1;
                }
                else if(Integer.parseInt(request.getData("y")) == 1){
                    ClientApp.gameController.flag5 = 1;
                }
                else{
                    ClientApp.gameController.flag6 = 1;
                }
            }else{
                if(Integer.parseInt(request.getData("y")) == 0){
                    ClientApp.gameController.flag7 = 1;
                }
                else if(Integer.parseInt(request.getData("y"))==1){
                    ClientApp.gameController.flag1 = 8;
                }
                else{
                    ClientApp.gameController.flag9=1;
                }
            }
        });
    }
    private void handleGameOver(Handler request) {
        //****win msg **play again(GAME_REQ) **home scene.
        Platform.runLater(() -> {
            if(request.getData("line").equals("You lose !")||request.getData("line").equals("Draw !")){
                btns[Integer.parseInt(request.getData("x"))][Integer.parseInt(request.getData("y"))].setGraphic(new ImageView(IAmX?"/resources/images/o.png":"/resources/images/x.png"));
            }
        });
        String msg = request.getData("line");
        Platform.runLater(() -> {
            if(player2!=null&&player2.equals("computer")){
                Alert alert = new Alert(AlertType.CONFIRMATION, msg, new ButtonType("Play again", ButtonData.OK_DONE), new ButtonType("cancel", ButtonData.NO));
                alert.setTitle("Game over");
                alert.showAndWait();
                if (alert.getResult().getButtonData() == ButtonData.OK_DONE) {
                    for(int i=0;i<3;i++){
                        for(int j=0;j<3;j++){
                            btns[i][j].setGraphic(new ImageView("/resources/images/empty.png"));
                        }
                    }
                    playWithAI();
                }else{
                    for(int i=0;i<3;i++){
                        for(int j=0;j<3;j++){
                            btns[i][j].setGraphic(new ImageView("/resources/images/empty.png"));
                        }
                    }
                    ClientApp.primaryStage.hide();
                    ClientApp.primaryStage.setScene(main.ClientApp.home);
                    ClientApp.primaryStage.show();
                            primaryStage.getIcons().add(new Image(getClass().getResource("/resources/images/icon.png").toString()));
                }
            }else{
                Alert alert = new Alert(AlertType.INFORMATION, msg, new ButtonType("GO To Home page", ButtonData.OK_DONE));
                alert.setTitle("Game over");
                alert.setHeaderText("Game over");
                alert.setContentText(msg);
                alert.showAndWait();
                if (alert.getResult().getButtonData() == ButtonData.OK_DONE){
                    for(int i=0;i<3;i++){
                        for(int j=0;j<3;j++){
                            btns[i][j].setGraphic(new ImageView("/resources/images/empty.png"));
                        }
                    }
                }
                ClientApp.primaryStage.hide();
                    ClientApp.primaryStage.setScene(main.ClientApp.home);
                    ClientApp.primaryStage.show();
            }
        });
        myTurn=false;
    }
    
    
    
    
    
    
    
    public String getOpponentName(){
        if(player2 == null)
            return player1;
        return player2;
    }
}