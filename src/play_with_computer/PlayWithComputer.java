package play_with_computer;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.effect.Effect;
import javafx.scene.layout.*;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.stage.Stage;
import javafx.beans.property.BooleanProperty;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.effect.Glow;
import javafx.scene.effect.InnerShadow;
import javafx.scene.effect.MotionBlur;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import main.ClientApp;

/**
 *
 * @author Elsamman
 */
public class PlayWithComputer extends Application {
    
    private MediaPlayer loginMediaPlayer;
    private MediaPlayer gameMediaPlayer;
    private ComputerPlayer computerPlayer;
    private int gameDifficulty;
    private BorderPane gameBorderPane;
    private Scene gameScene;
    private Scene selectDifficultyScene;
    private Scene selectGameModeScene;
    private boolean coopGameTurn;
    private Text gameWin;
    private Text gameLose;
    private Text gameDraw;
    private Text gameCoopPlayer1Win;
    private Text gameCoopPlayer2Win;
    private Text gamePlayerText;
    private Text gameComputerText;
    private Text[][] gameBoardCellsText;
    private GridPane gameCenterGridPane;
    private boolean offlineMode;
    private boolean coopMode;
    public static int level;

    /**
     * Initializing game environment and initiating connection with the server
     */
    
    public PlayWithComputer() {
        offlineMode = false;
        gameComputerText = new Text();
        gamePlayerText = new Text();
    }
    @Override
    public  void start(Stage primaryStage) {
        loginMediaPlayer = new MediaPlayer(new Media(getClass().getResource("../resources/music/Island_Fever.mp3").toString()));
        gameMediaPlayer = new MediaPlayer(new Media(getClass().getResource("../resources/music/lke9c-f08rg.mp3").toString()));
         // Game begin
        gameBorderPane = new BorderPane();
        BorderPane gameTopBorderPane = new BorderPane();
        gameTopBorderPane.setLeft(gamePlayerText);
        gameTopBorderPane.setRight(gameComputerText);
        gameBoardCellsText = new Text[3][3];
        gameWin = new Text("You Won");
        gameLose = new Text("You Lose");
        gameDraw = new Text("Tie");
        gameCoopPlayer1Win = new Text("Player 1 Won");
        gameCoopPlayer2Win = new Text("Player 2 Won");
        gameWin.setVisible(false);
        gameLose.setVisible(false);
        gameDraw.setVisible(false);
        gameCoopPlayer1Win.setVisible(false);
        gameCoopPlayer2Win.setVisible(false);
        gameCenterGridPane = new GridPane();
        gameCenterGridPane.getStyleClass().add("gameCenter");
        
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                StackPane stackPane = new StackPane();
                stackPane.getStyleClass().add("stack"); 
                
                gameBoardCellsText[i][j] = new Text();
                gameBoardCellsText[i][j].setFont(Font.font("Lucida Calligraphy", 60));
                stackPane.getChildren().add(gameBoardCellsText[i][j]);
                final int I = i, J = j;
                stackPane.setOnMouseClicked(e -> {
                    boolean gameEnd = false;
                    if (!gameBoardCellsText[I][J].getText().isEmpty()) {
                        return;
                    }
                    if (coopMode) {
                        if(!coopGameTurn) {
                            gameBoardCellsText[I][J].setText("X");
                            gameBoardCellsText[I][J].setFill(Color.GREEN);
                            computerPlayer.playHumanMove(I, J);
                            
                            if (computerPlayer.checkHumanWin()) {
                                gameBorderPane.setEffect(new Glow(0.6));
                                gameCoopPlayer1Win.setVisible(true);
                                gameEnd = true;
                            }
                        }
                        else {
                            gameBoardCellsText[I][J].setText("O");
                            gameBoardCellsText[I][J].setFill(Color.CHOCOLATE);
                            computerPlayer.playOtherHumanMove(I, J);
                            if(computerPlayer.checkComputerWin()) {
                                computerPlayer.playNextMove();
                                gameBoardCellsText[computerPlayer.getX()][computerPlayer.getY()].setText("O");
                                if (computerPlayer.checkComputerWin()) {
                                    gameBorderPane.setEffect(new Glow(0.6));
                                    gameCoopPlayer2Win.setVisible(true);
                                    gameEnd = true;
                                }
                            }
                        }
                        
                       if (computerPlayer.checkDraw()) {
                            gameCenterGridPane.setEffect(new MotionBlur(160, 20));
                            gameDraw.setVisible(true);
                            gameEnd = true; 
                        }
                        if (gameEnd) {
                            System.out.println("End with player");
                            String winner = "\n\tCongratulations Player 2 ~> O ..  You Are A Winner !\n\n";
                            BooleanProperty ply1 = gameCoopPlayer1Win.visibleProperty();                                   
                            if(ply1.getValue())
                                winner = "\n\tCongratulations Player 1 ~> X ..  You Are A Winner !\n\n";                            
                            BooleanProperty ply2 = gameCoopPlayer2Win.visibleProperty();                                   
                            if(!ply1.getValue() && !ply2.getValue())
                                winner = "\n\t NO looser here May be next time you will be a winner .. !\n\n";
                            Alert alert =  new Alert(AlertType.INFORMATION, winner,
                                    new ButtonType("Go To Home Page",ButtonData.OK_DONE));
                            alert.setTitle("~~ Game Ended ~~");
                            alert.setHeaderText("\t\t\t\tSee You Soon");
                            alert.showAndWait();      
                            ClientApp.primaryStage.hide();
                            ClientApp.primaryStage.setScene(main.ClientApp.home);
                            ClientApp.primaryStage.show();

                         }
                        coopGameTurn = !coopGameTurn;
                    } else if (offlineMode) {
                        gameBoardCellsText[I][J].setText("X");
                        gameBoardCellsText[I][J].setFill(Color.GREEN);
                        computerPlayer.playHumanMove(I, J);
                        if (computerPlayer.checkHumanWin()) {
                            gameBorderPane.setEffect(new Glow(0.6));
                            gameWin.setVisible(true);
                            gameEnd = true;
                        } else if (computerPlayer.checkDraw()) {
                            gameCenterGridPane.setEffect(new MotionBlur(160, 20));
                            gameDraw.setVisible(true);
                            gameEnd = true;
                        } else {
                            computerPlayer.playNextMove();
                            gameBoardCellsText[computerPlayer.getX()][computerPlayer.getY()].setText("O");
                            gameBoardCellsText[computerPlayer.getX()][computerPlayer.getY()].setFill(Color.CHOCOLATE);
                            if (computerPlayer.checkComputerWin()) {
                                gameBorderPane.setEffect(new InnerShadow(300, Color.BLACK));
                                gameLose.setVisible(true);
                                gameEnd = true;
                            }
                        }

                        if (gameEnd) {
                            System.out.println("End with computer");
                              String winner = "\n\tCongratulations Player ~> X ..  You Are A Winner !\n\n";
                            BooleanProperty x = gameLose.visibleProperty();                                   
                            if(x.getValue())
                                winner = "\n\tSorry For Loosing Player ..  Hard Luck Try Another Time !\n\n";
                             BooleanProperty draww = gameDraw.visibleProperty();
                            if(draww.getValue())
                                winner = "\n\t Nice Try But Still a Tie.. Try Another Time !\n\n";
                            Alert alert =  new Alert(AlertType.INFORMATION, winner,
                                    new ButtonType("Go To Home Page",ButtonData.OK_DONE));
                            alert.setTitle("~~ Game Ended ~~");
                            alert.setHeaderText("\t\t\t\tSee You Soon");
                            alert.showAndWait();      
                            ClientApp.primaryStage.hide();
                            ClientApp.primaryStage.setScene(main.ClientApp.home);
                            ClientApp.primaryStage.show(); 
                       }
                    } 

                });

                gameCenterGridPane.add(stackPane, j, i);
            }
        }
        // showing board
        StackPane gameCenterStackPane = new StackPane();
        gameCenterStackPane.getChildren().addAll(gameCenterGridPane, gameWin, gameDraw, gameLose, gameCoopPlayer1Win, gameCoopPlayer2Win);
        gameBorderPane.setCenter(gameCenterStackPane);
        gameBorderPane.setTop(gameTopBorderPane);
        // Select Difficulty Begin
        Button selectDifficultyEasyButton = new Button("Easy");
        Button selectDifficultyMediumButton = new Button("Medium");
        Button selectDifficultyHardButton = new Button("Hard");
        VBox selectDifficultyVBox = new VBox(30);
        selectDifficultyVBox.getChildren().addAll(selectDifficultyEasyButton, selectDifficultyMediumButton, selectDifficultyHardButton);
        StackPane selectDifficultyPane = new StackPane();
        selectDifficultyPane.getChildren().add(selectDifficultyVBox);
        // Select game mode
        Button selectSingleModeButton = new Button("1 V.S COM");
        Button selectCoopModeButton = new Button("1 V.S 1");
        VBox selectGameModeVBox = new VBox(30);
        selectGameModeVBox.setAlignment(Pos.CENTER);
        selectGameModeVBox.getChildren().addAll(selectSingleModeButton, selectCoopModeButton);
        StackPane selectGameModePane = new StackPane();
        selectGameModePane.getChildren().add(selectGameModeVBox);
        gameScene = new Scene(gameBorderPane, 700, 700);
        selectDifficultyScene = new Scene(selectDifficultyPane, 700, 700);
        selectGameModeScene = new Scene(selectGameModePane, 700, 700);
        gameScene.getStylesheets().add(getClass().getResource("../resources/styles/choosingStyle.css").toString());
        selectGameModeScene.getStylesheets().add(getClass().getResource("../resources/styles/choosingStyle.css").toString());
        selectDifficultyScene.getStylesheets().add(getClass().getResource("../resources/styles/choosingStyle.css").toString());
        gameScene.getStylesheets().add(getClass().getResource("../resources/styles/gameStyle.css").toString()); 
        primaryStage.setScene(selectGameModeScene);
        primaryStage.show();
        primaryStage.requestFocus();
        primaryStage.setTitle("Login Window");
        primaryStage.getIcons().add(new Image(getClass().getResource("/resources/images/icon.png").toString()));
        primaryStage.setMinWidth(420);
        primaryStage.setMinHeight(500);
        loginMediaPlayer.play();
        // Handling game
        gameBorderPane.setPadding(new Insets(5));
        ColumnConstraints columnConstraints = new ColumnConstraints();
        columnConstraints.setPercentWidth(100 / 3.0);
        RowConstraints rowConstraints = new RowConstraints();
        rowConstraints.setPercentHeight(100 / 3.0);
        gameCenterGridPane.getRowConstraints().addAll(rowConstraints, rowConstraints, rowConstraints);
        gameCenterGridPane.getColumnConstraints().addAll(columnConstraints, columnConstraints, columnConstraints);
        gameWin.setFont(Font.font("Lucida Calligraphy", 72));
        gameWin.setFill(Color.FORESTGREEN);
        gameDraw.setFont(Font.font("Lucida Calligraphy", 72));
        gameDraw.setFill(Color.ORANGERED);
        gameLose.setFont(Font.font("Lucida Calligraphy", 72));
        gameLose.setFill(Color.CHOCOLATE);
        gameCoopPlayer1Win.setFont(Font.font("Lucida Calligraphy", 48));
        gameCoopPlayer1Win.setFill(Color.FORESTGREEN);
        gameCoopPlayer2Win.setFont(Font.font("Lucida Calligraphy", 48));
        gameCoopPlayer2Win.setFill(Color.FORESTGREEN);
        gamePlayerText.setFont(Font.font("Lucida Calligraphy", 60));
        gamePlayerText.setFill(Color.LAWNGREEN);
        gameComputerText.setFont(Font.font("Lucida Calligraphy", 60));
        gameComputerText.setFill(Color.PALEVIOLETRED);        
        // Handling Select Difficulty
        selectDifficultyEasyButton.setOnAction(e -> {
            level = 0;
            gameDifficulty = 1;
            computerPlayer = new ComputerPlayer(gameDifficulty);
            primaryStage.setScene(gameScene);
            primaryStage.setTitle("Battle window");

        });

        selectDifficultyMediumButton.setOnAction(e -> {
            level =0;
            gameDifficulty = 3;
            computerPlayer = new ComputerPlayer(gameDifficulty);
            primaryStage.setScene(gameScene);
             primaryStage.setTitle("Battle window");

        });

        selectDifficultyHardButton.setOnAction(e -> {
            gameDifficulty = 5;
            level = 1;
            computerPlayer = new ComputerPlayer(gameDifficulty);
            primaryStage.setScene(gameScene);
            primaryStage.setTitle("Battle window");

        });

        selectDifficultyVBox.setAlignment(Pos.CENTER);
        selectDifficultyEasyButton.setFont(Font.font("Lucida Calligraphy", 60));
        selectDifficultyMediumButton.setFont(Font.font("Lucida Calligraphy", 60));
        selectDifficultyHardButton.setFont(Font.font("Lucida Calligraphy", 60));
        selectSingleModeButton.setFont(Font.font("Lucida Calligraphy", 60));
        selectCoopModeButton.setFont(Font.font("Lucida Calligraphy", 60));
        selectSingleModeButton.setOnAction(e -> {
            offlineMode = true;
            primaryStage.setScene(selectDifficultyScene);
            loginMediaPlayer.stop();
            gameMediaPlayer.play();
            gamePlayerText.setText("Human");
            gameComputerText.setText("Computer");
            primaryStage.setTitle("Select difficulty window");
            
        });

        selectCoopModeButton.setOnAction(e -> {
            coopMode = true;
            coopGameTurn = false;
            computerPlayer = new ComputerPlayer(10);
            primaryStage.setScene(gameScene);
            primaryStage.setTitle("Battle window");            
            gamePlayerText.setText("Player 1");
            gameComputerText.setText("Player 2");
        });
    }
    /**
     * Add certain effect to the game
     * @param effect effect applied to the board
     */
    public void setBlurEffectOnBoard(Effect effect) {
        Platform.runLater(() -> {
            gameBorderPane.setEffect(effect);
        });

    }
}
