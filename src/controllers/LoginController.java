package controllers;

import java.io.FileNotFoundException;
import network.ClientSession;
import main.ClientApp;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import model.Player;
/**
 *
 * @author AYA
 */
public class LoginController implements Initializable {
    @FXML private Text actiontarget;
    @FXML private TextField txtf_password;
    @FXML private TextField txtf_userName;
    private Stage primaryStage;
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        primaryStage = ClientApp.primaryStage;
//        primaryStage.hide();
//        primaryStage.setScene(main.ClientApp.mainGUI);
//        primaryStage.show();
        
//        primaryStage = ClientApp.primaryStage;
    }
    
    
    private ObservableList<Player> playersData = FXCollections.observableArrayList();
       
    
//    public void getOnlinePlayers(){
//    playersData.clear(); 
//        ClientSession.allPlayers.entrySet().forEach((player) -> {
//          
////            if (player.getValue().getStatus() !="Offline")
//            playersData.add(player.getValue());
//            System.out.println(playersData);
//        });
//        HomeController.allPlayersTable.setItems(playersData);
//    }
//       
    @FXML protected void handleSignInButtonAction(ActionEvent event) {
        if(ClientApp.session == null){
            ClientApp.session = new ClientSession("127.0.0.1", 5555);
        }
        ClientApp.session.openConnection();
        if(ClientApp.session.connected){
            if(ClientApp.session.loginToServer(txtf_userName.getText(), txtf_password.getText())){
                primaryStage.hide();
                primaryStage.setScene(main.ClientApp.home);
                primaryStage.show();
                ClientApp.homeController.bindPlayersTable();
//                getOnlinePlayers();
                ClientApp.homeController.playerInfo();
                
            }else{
                Alert alert = new Alert(AlertType.ERROR);
                alert.setTitle("TicTacToe");
                alert.setHeaderText("Login failure");
                alert.setContentText("Invalid username or password!");
                alert.showAndWait();
            }
        }else{
            Alert alert = new Alert(AlertType.ERROR);
            alert.setTitle("TicTacToe");
            alert.setHeaderText("Connection failure");
            alert.setContentText("Cannot establish connection with server!");
            alert.showAndWait();
        }
    }
    @FXML protected void handleSignUpButtonAction(ActionEvent event) {
        primaryStage.hide();
        primaryStage.setScene(main.ClientApp.signUp);
        primaryStage.show();
        primaryStage.getIcons().add(new Image(getClass().getResource("/resources/images/icon.png").toString()));
    }
    public void terminateConnectino(){
        Alert alert = new Alert(AlertType.ERROR);
        alert.setTitle("Connection lost");
        alert.setHeaderText("Server disconnected!");
        alert.setContentText("Opps! you've lost the connection with server, try reconnecting later");
        alert.showAndWait();
    }
}
