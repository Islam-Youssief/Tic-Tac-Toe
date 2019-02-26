package main;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import controllers.ServerController;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import network.Server;

/**
 *
 * @author ISLAM
 */
public class ServerApp extends Application {
    public static Server server = new Server();
    public static Stage primaryStage ;
    public static Scene serverScene;
    public static ServerController serverController;
    private MediaPlayer loginMediaPlayer;
    @Override
    public void start(Stage stage) throws Exception {
        primaryStage = stage;
        FXMLLoader serverLoader = new FXMLLoader();
        serverLoader.setLocation(getClass().getResource("/resources/views/ServerView.fxml"));
        Parent serverParent = serverLoader.load();
        serverScene = new Scene(serverParent);
        serverController = (ServerController)serverLoader.getController();
        stage.setTitle("TicTacToe Server");
        stage.setScene(serverScene);
        stage.show();
        
//         loginMediaPlayer = new MediaPlayer(new Media(getClass().getResource("../resources/music/Island_Fever.mp3").toString()));
//        loginMediaPlayer.setCycleCount(MediaPlayer.INDEFINITE);
//        loginMediaPlayer.play();
        primaryStage.setOnCloseRequest((event) -> {
            if(server.running)
                server.stopServer();
            
        });
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }
    
}
