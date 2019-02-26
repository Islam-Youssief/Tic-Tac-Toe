package main;

import network.ClientSession;
import controllers.*;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.stage.Stage;

/**
 *
 * @author ISLAM
 */
public class ClientApp extends Application {

    public static Stage primaryStage;
    public static Scene mainGUI;
    public static Scene signIn;
    public static Scene signUp;
    public static Scene home;
    public static Scene game;
    public static GameController gameController;
    public static HomeController homeController;
    public static LoginController loginController;
    public static ClientSession session;
    private MediaPlayer loginMediaPlayer;

    @Override
    public void start(Stage stage) throws Exception {
        primaryStage = stage;
       
        //sign in
        FXMLLoader signInLoader = new FXMLLoader();
        signInLoader.setLocation(getClass().getResource("/resources/views/LoginView.fxml"));
        Parent signInParent = signInLoader.load();
        signIn = new Scene(signInParent, 950, 700);
        loginController = (LoginController) signInLoader.getController();
        //sign up
        FXMLLoader signUpLoader = new FXMLLoader();
        signUpLoader.setLocation(getClass().getResource("/resources/views/SignupView.fxml"));
        Parent signUpParent = signUpLoader.load();
        signUp = new Scene(signUpParent, 700, 750);
        //home
        FXMLLoader homeLoader = new FXMLLoader();
        homeLoader.setLocation(getClass().getResource("/resources/views/HomeView.fxml"));
        Parent homeParent = homeLoader.load();
        home = new Scene(homeParent, 700, 900);
        homeController = (HomeController) homeLoader.getController();
        //game
        FXMLLoader gameLoader = new FXMLLoader();
        gameLoader.setLocation(getClass().getResource("/resources/views/GameView.fxml"));
        Parent gameParent = gameLoader.load();
        game = new Scene(gameParent, 700, 600);
        gameController = (GameController) gameLoader.getController();
       
        loginMediaPlayer = new MediaPlayer(new Media(getClass().getResource("../resources/music/lke9c-f08rg.mp3").toString()));
//        loginMediaPlayer.setCycleCount(MediaPlayer.INDEFINITE);
        loginMediaPlayer.play();
        stage.setTitle("TicTacToe Game");
        stage.setScene(signIn);
        stage.show();
//        stage.setMinWidth(800);
//        stage.setMaxWidth(800);
//        stage.setMinHeight(600);
//        stage.setMaxHeight(600);
        primaryStage.setOnCloseRequest((event) -> {
            if (session != null && session.connected) {
                session.closeConnection();
            }
        });
    }
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }

}
