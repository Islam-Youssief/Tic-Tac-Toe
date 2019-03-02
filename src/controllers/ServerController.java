package controllers;

import java.io.IOException;
import main.ServerApp;
import model.*;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

/**
 * FXML Controller class
 *
 * @author Aya
 */
public class ServerController implements Initializable {
    
    private int mainPort = 5555;
    @FXML private TableView<Player> tableView;
    @FXML private TableColumn fNameColumn;
    @FXML private TableColumn lNameColumn;
    @FXML private TableColumn loginColumn;
    @FXML private TableColumn scoreColumn;
    @FXML private TableColumn statusColumn;
    @FXML private TableColumn picColumn;
    @FXML private Button key;
    @FXML  Image switchOn = new Image(getClass().getResourceAsStream("/resources/images/swithon.png"));
    @FXML  Image switchOff = new Image(getClass().getResourceAsStream("/resources/images/swithoff.png"));
    
    private ObservableList<Player> playersList = FXCollections.observableArrayList();

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        fNameColumn.setCellValueFactory(
            new PropertyValueFactory<>("fname")
        );
        lNameColumn.setCellValueFactory(
            new PropertyValueFactory<>("lname")
        );
        loginColumn.setCellValueFactory(
            new PropertyValueFactory<>("LoginName")
        );
        scoreColumn.setCellValueFactory(
            new PropertyValueFactory<>("TotalPoints")
        );
        statusColumn.setCellValueFactory(
            new PropertyValueFactory<>("status")
        );
        picColumn.setCellValueFactory(
            new PropertyValueFactory<>("PicPath")
        );
    }

    @FXML protected void handleToggleOnAction(ActionEvent t) throws IOException {
        if(!ServerApp.server.running)
        {
            if(ServerApp.server.startServer(mainPort)){
                key.setGraphic(new ImageView(switchOn));
                refreshPlayersTable();
                // delete all the notifications from db
                PlayerDB.ClearNotification();
            }
            else{
                Alert alert = new Alert(AlertType.ERROR);
                alert.setTitle("Error starting the server");
                alert.setContentText("Port "+mainPort+ " is used ,Try another port number !");
                alert.showAndWait();
            }
        }
        
        else{
            ServerApp.server.stopServer();
            freePlayersTable();
            key.setGraphic(new ImageView(switchOff));
        }
    }

    public void refreshPlayersTable(){
        playersList.clear();
        ServerApp.server.allPlayers.entrySet().forEach((player) -> {
            playersList.add(player.getValue());
        });
        tableView.setItems(playersList);
    }
    
    /***
     * Used to clear the table when shutting down the server
     */
    public void freePlayersTable(){
	    playersList.clear();
	    tableView.setItems(playersList);
    }
}
