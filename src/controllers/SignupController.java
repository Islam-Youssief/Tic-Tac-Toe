package controllers;

import java.io.File;
import main.ClientApp;
import network.ClientSession;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import static main.ClientApp.primaryStage;

/**
 *
 * @author ISLAM
 */
public class SignupController implements Initializable {
    
    @FXML private Label label=new Label();
    @FXML private Text massage = new Text();
    @FXML private TextField userName = new TextField();
    @FXML private TextField userPassword = new TextField();
    @FXML private TextField confirmPassword = new TextField();
    @FXML private TextField firstName = new TextField();
    @FXML private TextField lastName = new TextField();
    @FXML private ImageView userPic = new ImageView();
   // @FXML ListView<ImageView> imglist = new ListView<>();
    @FXML private ImageView v1 = new ImageView();
    private ObservableList list = FXCollections.observableArrayList();
    String avatar;
    
    private final FileChooser fileChooser = new FileChooser();
    @Override
    public void initialize(URL url, ResourceBundle rb) {
    
    fileChooser.setInitialDirectory(new File(System.getProperty("user.home")));
        fileChooser
                .getExtensionFilters()
                .addAll(
                        new FileChooser.ExtensionFilter("Images", "*.png"),
                        new FileChooser.ExtensionFilter("All Files", "*.*"));
    }
    
     @FXML private void browseImage(ActionEvent event) {
      try{
         fileChooser.setTitle("Open File");
        File file = fileChooser.showOpenDialog(primaryStage);
//         System.out.println(file.getAbsolutePath());
         avatar = file.getAbsolutePath();
     avatar = avatar.replace("\\", "/");
      }
      catch(Exception e )
      {
        Alert alert = new Alert(AlertType.ERROR);
        alert.setTitle("Registration error!");
        alert.setContentText("Please Choose Any Image !");
        alert.showAndWait();
      }
    }
    
    
    
    @FXML private void handleButtonAction(ActionEvent event) {
        Alert alert = new Alert(AlertType.ERROR);
        alert.setTitle("Registration error!");
        if( userName.getText().equals("") || 
            userPassword.getText().equals("") ||
            confirmPassword.getText().equals("") || 
            firstName.getText().equals("") ||
            lastName.getText().equals("") ){
            alert.setContentText("Please complete all your information!");
            alert.showAndWait();
        }else if(!userPassword.getText().equals(confirmPassword.getText())){
            alert.setContentText("Password doesn't match the confirmation!");
            alert.showAndWait();
        }else if(avatar.length()<0)
        {
//                imglist.getSelectionModel().getSelectedIndex()<0){
             
//            islam
            
            alert.setContentText("Please select your profile picture!");
            alert.showAndWait();
       
        }else{
            if(ClientApp.session == null){
                ClientApp.session = new ClientSession("127.0.0.1", 5555);
            }
            ClientApp.session.openConnection();
            if(ClientApp.session.connected){
                boolean regResult = ClientApp.session.playerSignup(firstName.getText(), lastName.getText(), userName.getText(), userPassword.getText(), avatar);
                if(regResult){
                    Alert success = new Alert(AlertType.INFORMATION);
                    success.setTitle("Registration succeded!");
                    success.setContentText("Congratulations! you've registered successfully!\nYou will be redirected to login page");
                    success.showAndWait();
                    ClientApp.primaryStage.hide();
                    ClientApp.primaryStage.setScene(main.ClientApp.signIn);
                    ClientApp.primaryStage.show();
                    primaryStage.getIcons().add(new Image(getClass().getResource("/resources/images/icon.png").toString()));
                }else{
                    alert.setContentText("Registration failed! username already existed!");
                    alert.showAndWait();
                }
            }else{
                alert.setContentText("Cannot establish connection with server");
                alert.showAndWait();
            }
            ClientApp.session.closeConnection();
        }
    }
    @FXML private void handleButton_back_Action(ActionEvent event) {
        ClientApp.primaryStage.hide();
        ClientApp.primaryStage.setScene(ClientApp.signIn);
        ClientApp.primaryStage.show();
        primaryStage.getIcons().add(new Image(getClass().getResource("/resources/images/icon.png").toString()));
    }
   
}
