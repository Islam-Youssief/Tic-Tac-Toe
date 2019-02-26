package controllers;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import network.Handler;
import main.ClientApp;
import model.Player;
import network.ClientSession;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import model.PlayerDB;
import play_with_computer.PlayWithComputer;

/**
 * FXML Controller class
 *
 * @author AYA
 */

public class HomeController implements Initializable {

    /**
     * Initializes the controller class.
     */
    
    @FXML private Button invite,logout; 
    @FXML private Label opponentName,opponentScore;
    @FXML private Label playerName,playerScore;
    @FXML private TableView<Player> allPlayersTable;
    @FXML private TableColumn colUsername;
    @FXML private TableColumn colScore;
    @FXML private TableColumn colStatus;
    @FXML private ImageView imgView;
    @FXML public ImageView playerImgView;
    @FXML public ImageView opponentImgView;
    @FXML public Image playerImg;
    @FXML public Image opponentImg;
    private ObservableList<Player> playersData = FXCollections.observableArrayList();
    private Stage primaryStage;
    private String opponent;
      
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        colUsername.setCellValueFactory(
            new PropertyValueFactory<>("LoginName")
        );
        colScore.setCellValueFactory(
            new PropertyValueFactory<>("TotalPoints")
        );
        colStatus.setCellValueFactory(
            new PropertyValueFactory<>("status")
        );
        primaryStage = ClientApp.primaryStage;
        allPlayersTable.getSelectionModel().selectedIndexProperty().addListener(new RowSelectChangeListener());        
    }   
    private class RowSelectChangeListener implements ChangeListener {
        @Override
        public void changed(ObservableValue observable, Object oldValue, Object newValue) {
        }
    };
    @FXML protected void handleButton_invite_Action(ActionEvent event) {
        if(allPlayersTable.getSelectionModel().getSelectedItem()!= null){
            if(allPlayersTable.getSelectionModel().getSelectedItem().getStatus().equals(Handler.ONLINESTATUS)){
                ClientApp.session.requestGame(allPlayersTable.getSelectionModel().getSelectedItem().getLoginName());
                ClientApp.gameController.txt_area.setText("");
            }else{
                Alert alert = new Alert(AlertType.WARNING);
                alert.setTitle("Player not available");
                alert.setHeaderText("Player not available");
                alert.setContentText(allPlayersTable.getSelectionModel().getSelectedItem().getLoginName()+" not available");
                alert.showAndWait();      
            }
        }
    };
    
    @FXML protected void handleButton_notcton_Action(ActionEvent event){
        PlayerDB.getNotification();
                Alert alert = new Alert(AlertType.WARNING);
                alert.setTitle("Notification");
                alert.setHeaderText("These Notifications are about score and online players ..");
                alert.setContentText(PlayerDB.getNotification().toString());
                alert.showAndWait();      
        
    }
    @FXML protected void handleButton_logout_Action(ActionEvent event) {
        ClientApp.session.closeConnection();
        primaryStage.setScene(main.ClientApp.signIn);
    }
    
    /***
     * Handle playing with computer
     * @param event 
     */
    @FXML protected void handleButton_arcade_Action(ActionEvent event) {
                PlayWithComputer AI = new PlayWithComputer();
                AI.start(primaryStage);
               
    }
    @FXML public void playerInfo() {
        try{
        playerName.setText(ClientApp.session.player.getLoginName());
        playerScore.setText(Integer.toString(ClientApp.session.player.getTotalPoints()));
         playerImg = new Image(new FileInputStream(ClientApp.session.player.getPicPath()));
        }
        catch(Exception e)
        {System.out.println("Error showing player info !");}
        ClientApp.homeController.playerImgView.setImage(playerImg);
        allPlayersTable.getSelectionModel().selectFirst();
    }
    public void bindPlayersTable(){
        playersData.clear(); 
        ClientSession.allPlayers.entrySet().forEach((player) -> {
            //islam
            String x = player.getValue().getStatus();
        if (!x.equals("Offline") )
        {
                System.err.println("dsf");
        playersData.add(player.getValue());}
         
//        playersData.add(player.getValue());
        });
        allPlayersTable.setItems(playersData);
    }
    public void showAlert(String playerName){
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION, playerName+" wants to play with you", ButtonType.NO, ButtonType.YES);
        if (alert.showAndWait().get() == ButtonType.YES) {
            ClientApp.session.sendResponse(true);
            ClientApp.gameController.resetScene();
            ClientApp.primaryStage.setScene(main.ClientApp.game);
            System.out.println("play again");
            ClientApp.gameController.img = new Image(getClass().getResourceAsStream("/resources/images/o.png"));
        }else{
            ClientApp.session.sendResponse(false);
        }
    }
}
