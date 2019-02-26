package controllers;

import main.ClientApp;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

/**
 * FXML Controller class
 *
 * @author AYA
 */
public class GameController implements Initializable {

    /**
     * Initializes the controller class.
     */
    @FXML public Button b1,b2,b3,b4,b5,b6,b7,b8,b9,send,surrend;
    @FXML public TextField txt_field;
    @FXML public int flag1=0,flag2=0,flag3=0,flag4=0,flag5=0,flag6=0,flag7=0,flag8=0,flag9=0;
    @FXML public String src;
    @FXML public Image img;
    @FXML private Label player1Name,player2Name,massge,time;
    @FXML public TextArea txt_area;
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        txt_area.setEditable(false);
        txt_area.setWrapText(true);
    }   
    public void resetScene(){
        player1Name.setText(ClientApp.session.player.getLoginName());
        player2Name.setText(ClientApp.session.getOpponentName());
        flag1=flag2=flag3=flag4=flag5=flag6=flag7=flag8=flag9=0;
    }
    @FXML protected void handleButton_send_Action(ActionEvent event) {
        ClientApp.session.sendChatMessage(txt_field.getText());
        txt_field.setText("");
    }
    @FXML protected void handleButton1Action(ActionEvent event) {
        if(flag1==0 && ClientApp.session.myTurn){
            ClientApp.session.makeAMove("0", "0");
            b1.setGraphic(new ImageView(img));
            flag1=1;
        }
    }
    @FXML protected void handleButton2Action(ActionEvent event) {
        if(flag2==0 && ClientApp.session.myTurn){
            ClientApp.session.makeAMove("0", "1");        
            b2.setGraphic(new ImageView(img));
            flag2=1;
        }
    }
    @FXML protected void handleButton3Action(ActionEvent event) {
        if(flag3==0 && ClientApp.session.myTurn){
            ClientApp.session.makeAMove("0", "2");
            b3.setGraphic(new ImageView(img));
            flag3=1;
        }
    }
    @FXML protected void handleButton4Action(ActionEvent event) {
        if(flag4==0 && ClientApp.session.myTurn){
            ClientApp.session.makeAMove("1", "0");
            b4.setGraphic(new ImageView(img));
            flag4=1;
        }
    }
    @FXML protected void handleButton5Action(ActionEvent event) {
        if(flag5==0 && ClientApp.session.myTurn){
            ClientApp.session.makeAMove("1", "1");
            b5.setGraphic(new ImageView(img));
            flag5=1;
        }
    }
    @FXML protected void handleButton6Action(ActionEvent event) {
        if(flag6==0 && ClientApp.session.myTurn){
            ClientApp.session.makeAMove("1", "2");
            b6.setGraphic(new ImageView(img));
            flag6=1;
        }
    }
    @FXML protected void handleButton7Action(ActionEvent event) {
       if(flag7==0 && ClientApp.session.myTurn){
            ClientApp.session.makeAMove("2", "0");
            b7.setGraphic(new ImageView(img));
            flag7=1;
        }
    }
    @FXML protected void handleButton8Action(ActionEvent event) {
       if(flag8==0 && ClientApp.session.myTurn){
            ClientApp.session.makeAMove("2", "1");
            b8.setGraphic(new ImageView(img));
            flag8=1;
        }
    }
    @FXML protected void handleButton9Action(ActionEvent event) {
        if(flag9==0 && ClientApp.session.myTurn){
            ClientApp.session.makeAMove("2", "2");
            b9.setGraphic(new ImageView(img));
            flag9=1;
        }
    }
}
