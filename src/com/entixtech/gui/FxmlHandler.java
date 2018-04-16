package com.entixtech.gui;

import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.Observable;
import java.util.ResourceBundle;

public class FxmlHandler extends Observable implements Initializable{

    ObservableList<String> playAsList = FXCollections.observableArrayList("Ai", "Player", "Tournament");

    private int challengeNumber;

    private List playerList;
    @FXML
    private Button game1;

    @FXML
    private Button game2;

    @FXML
    private Button createGame;

    @FXML
    private TextField selectedUser;

    @FXML
    private TextField selectedGame;

    @FXML
    private TextField challengeUserField;

    @FXML
    private TextField challengeGameField;

    @FXML
    private Button challengePlayer;

    //handle mouseclick events WIP
    @FXML
    Text Title;

    @FXML
    private ListView listView;

    @FXML
    private ChoiceBox playAs;

    @FXML
    void openHelpScreen(ActionEvent event) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("HelpScreen.fxml"));
            Stage stage2 = new Stage();
            stage2.setResizable(false);
            stage2.setTitle("Help Screen"); // Set the stage title
            stage2.setScene(new Scene(root)); // Place the scene in the stage
            stage2.show(); // Display the stage
        }
        catch (IOException e) {
            e.printStackTrace();
            System.out.println("Can't load new window");
        }
    }

    @FXML
    public void acceptOrDeclineChallenge(String challenger, String gameType, int challengeNumber){
        this.challengeNumber = challengeNumber;
        Platform.runLater(()-> {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(View.class.getResource("ChallengedScreen.fxml"));
            Parent challengeView = null;
            try {
                challengeView = loader.load();
            } catch (IOException e) {
                e.printStackTrace();
            }
            Stage challengeViewStage = new Stage();
            challengeViewStage.setTitle("Challenged!"); // Set the stage title
            challengeViewStage.setScene(new Scene(challengeView)); // Place the scene in the stage
            challengeViewStage.show(); // Display the stage
//            challengeUserField.setText(challenger);
//             challengeGameField.setText(gameType);
        });
    }

    @FXML
    void acceptChallenge(ActionEvent event){
        setChanged();
        notifyObservers(new ArgumentPasser("acceptChallenge", null, null,challengeNumber ));
    }


    @FXML
    public void openGameView(){
        Platform.runLater(()->{
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(View.class.getResource("gameView.fxml"));
            Parent gameView = null;
            try {
                gameView = loader.load();
            } catch (IOException e) {
                e.printStackTrace();
            }
            GameHandler controller = loader.getController();
            Stage gameViewStage = new Stage();
            gameViewStage.setTitle("Game: " + selectedGame.getText()); // Set the stage title
            gameViewStage.setScene(new Scene(gameView)); // Place the scene in the stage
            gameViewStage.show(); // Display the stage

            gameViewStage.setOnCloseRequest( event ->
            {
                System.out.println("CLOSING");
                gameViewStage.close();
            });

            setChanged();
            notifyObservers(new ArgumentPasser("gameStarted",null,null, controller));});
    }



    @FXML
    void playTournament(ActionEvent event) {}

    @FXML
    void startGame(ActionEvent event) {}

    //getter for field
    public TextField getTextField() {
        return selectedGame;
    }

    //buttonhandlers to set appropriate game in textfield
    @FXML
    void btn1handle(ActionEvent event) {
        selectedGame.setText(game1.getText());
    }

    @FXML
    void btn2handle(ActionEvent event) {
        selectedGame.setText(game2.getText());
    }

    @FXML
    void playAgainstAi(ActionEvent actionEvent){
        setChanged();
        notifyObservers(new ArgumentPasser("playWithGui", selectedGame.getText(), null, null));
    }


    @FXML
    void sendAndChallengePlayer(ActionEvent event){
        String player = selectedUser.getText();
        String game = selectedGame.getText();
        String type = playAs.getValue().toString();
        String id = "challenge";
        setChanged();
        notifyObservers(new ArgumentPasser(id,player,game,type));
    }

    public void setPlayerList(List<String> playerList){
        this.playerList = playerList;
        if (playerList != null) {
            Platform.runLater(() -> listView.setItems(FXCollections.observableList(playerList)));
        }
    }

    @FXML
    void changeSelectedUser() {
        listView.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                Platform.runLater(()->{
                    selectedUser.clear();
                    selectedUser.setText(newValue);
                });
            }
        });
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        playAs.setValue("Ai");
        playAs.setItems(playAsList);
    }
}