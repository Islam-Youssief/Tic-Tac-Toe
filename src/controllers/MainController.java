package controllers;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

/**
 *
 * @author Aya
 */
public class MainController implements Initializable {

    Parent root = null;
    Node source = null;
    Stage stage = null;
    Scene scene;
    String newFXML;

    @FXML
    private TextField firstName;
    @FXML
    private TextField lastName;
    @FXML
    private TextField loginName;
    @FXML
    private TextField password;
    @FXML
    private TextField faceBook;
    @FXML
    private Label message;

    @FXML
    private void handleButtonAction(ActionEvent event) {
        source = (Node) event.getSource();
        stage = (Stage) source.getScene().getWindow();
        newFXML = "../views/SignUp.fxml";
        String path = "signUp.css";
        switchStage(stage, newFXML, path);
    }

    @FXML
    private void openLoginWindow(ActionEvent event) {
        source = (Node) event.getSource();
        stage = (Stage) source.getScene().getWindow();
        newFXML = "../views/LogIn.fxml";
        String path = "logIn.css";
        switchStage(stage, newFXML, path);
    }

    @FXML
    private void openGameTutorial(ActionEvent event) {
        source = (Node) event.getSource();
        stage = (Stage) source.getScene().getWindow();
        newFXML = "../views/GameTutorial.fxml";
        String path = "gameTutorial.css";
        switchStage(stage, newFXML, path);
    }

    @FXML
    private void openAboutWindow(ActionEvent event) {
        source = (Node) event.getSource();
        stage = (Stage) source.getScene().getWindow();
        newFXML = "../views/About.fxml";
        String path = "about.css";
        switchStage(stage, newFXML, path);
    }

    @FXML
    private void BackTo(ActionEvent event) {
        source = (Node) event.getSource();
        stage = (Stage) source.getScene().getWindow();
        newFXML = "../views/MainGui.fxml";
        String path = "MainGui.css";
        switchStage(stage, newFXML, path);
    }

    @FXML
    private void backInLogin(ActionEvent event) {
        source = (Node) event.getSource();
        stage = (Stage) source.getScene().getWindow();
        newFXML = "../views/MainGui.fxml";
        String path = "MainGui.css";
        switchStage(stage, newFXML, path);
    }

   

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
    }

    public void switchStage(Stage stage, String fxml, String path) {
        try {
            root = FXMLLoader.load(getClass().getResource(fxml));

        } catch (IOException ex) {
            Logger.getLogger(MainController.class.getName()).log(Level.SEVERE, null, ex);
        }
        stage.close();

        scene = new Scene(root);
        scene.getStylesheets().add(getClass().getResource(path).toExternalForm());

        stage.setScene(scene);
        stage.show();
    }
    public void terminateConnectino(){
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Connection lost");
        alert.setHeaderText("Server disconnected!");
        alert.setContentText("Opps! you've lost the connection with server, try reconnecting later");
        alert.showAndWait();
    }
}
