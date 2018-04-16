package com.entixtech.gui;

import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.RowConstraints;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Text;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.ResourceBundle;
import java.util.stream.IntStream;

public class GameHandler extends Observable implements Initializable {

    private Image imageX;
    private Image imageO;
    private Image imageNothing;
    private String gamePlay;
    private DropShadow dropShadow = new DropShadow();

    @FXML
    private Text gameTitle;

    @FXML
    private Label player;

    @FXML
    private Label opponent;

    @FXML
    private TextField playNumTiles;

    @FXML
    private TextField oppNumTiles;

    @FXML
    private TextField playerTurn;

    @FXML
    private GridPane gameGrid;

    private String currentGame;
    private int gridSize;

    public void switchWindow(String game, String gamePlay){
        this.gamePlay = gamePlay;
        // Identify game
        switch (game){
            case "Tic-tac-toe":
                currentGame = game;
                gridSize = 3;
                break;
            case "Reversi":
                currentGame = game;
                gridSize = 8;
                break;
        }

        // Title
        gameTitle.setText(game);
        // Load users
        // Grid function
        updateGrid(gridSize);
        // player names

    }

    public void paintLocation(int location){
        //ImageView image = new ImageView("");
    }

    ArrayList<Button> buttonList = new ArrayList<Button>();

    public void updateGrid(int size){

        gameGrid.setHgap(2);
        gameGrid.setVgap(2);
        List<RowConstraints> rowList = gameGrid.getRowConstraints();
        rowList.clear();
        List<ColumnConstraints> columnList = gameGrid.getColumnConstraints();
        columnList.clear();
        if (size > 0) {
            RowConstraints rowConstraints = new RowConstraints();
            rowConstraints.setPercentHeight(100d / size);
            rowConstraints.setMaxHeight(30);
            ColumnConstraints columnConstraints = new ColumnConstraints();
            columnConstraints.setPercentWidth(100d / size);
            columnConstraints.setMaxWidth(30);
            for (int i = 0; i < size; i++) {
                rowList.add(rowConstraints);
                columnList.add(columnConstraints);
            }
        }


        for(int j = 0; j < size*size;j++){
            Button tempButton = new Button(); //"button_"+j // removed text from buttons.
            tempButton.setMaxWidth(Double.MAX_VALUE);
            tempButton.setMaxHeight(Double.MAX_VALUE);
            tempButton.setPrefHeight(95);
            tempButton.setPrefWidth(95);
            tempButton.setId(""+j);
//            tempButton.setStyle("-fx-background-color: rgb(5,99,43)");
            tempButton.setOnAction(new EventHandler<javafx.event.ActionEvent>(){
                        @Override
                        public void handle(javafx.event.ActionEvent event) {
                            setChanged();
                            notifyObservers(new ArgumentPasser("move", tempButton.getId(),null,null));
                        }
                    });
            buttonList.add(tempButton);
        }

        int x = 0;
        int y = 0;
        for(Button button: buttonList){
            if(y==size){
                y = 0;
                x++;
            }
            gameGrid.add(button,y,x);

            y++;
        }
    }

    @FXML
    public void printPlayersonBoard(String other, String username){
        Platform.runLater(()->{
            player.setText(username);
            opponent.setText(other);
        });
    }

    public void updateScore(int playerTiles, int opponentTiles){
        Platform.runLater(()->{
            playNumTiles.setText(String.valueOf(playerTiles));
            oppNumTiles.setText(String.valueOf(opponentTiles));
        });
    }

    public void whosTurn(String username){
        Platform.runLater(()->{
            playerTurn.setText(username);
        });
    }


    @FXML
    public void printBoardOnView(int[][] board, int[] possibleMoves){
            Platform.runLater(() -> {
                int i=0;
                for (int[] aBoard : board) {
                    for (int onABoard : aBoard) {
                        Button tempButton = buttonList.get(i++);
                        if (onABoard == 0){
                            Circle circle = new Circle();
                            circle.setEffect(dropShadow);
                            circle.setFill(Color.WHITE);
                            circle.radiusProperty().bind(tempButton.widthProperty().divide(2).subtract(8));
                            tempButton.setGraphic(circle);
                            tempButton.setStyle("-fx-background-color: rgb(5,99,43)");
                            tempButton.setDisable(true);
                        } else if (onABoard == 1) {
                            Circle circle = new Circle();
                            circle.setEffect(dropShadow);
                            circle.setFill(Color.BLACK);
                            circle.radiusProperty().bind(tempButton.widthProperty().divide(2).subtract(8));
                            tempButton.setGraphic(circle);
                            tempButton.setStyle("-fx-background-color: rgb(5,99,43)");
                            tempButton.setDisable(true);

                        } else if (IntStream.of(possibleMoves).anyMatch(x -> x == Integer.parseInt(tempButton.getId()))){
                            tempButton.setStyle("-fx-background-color: rgba(153,255,204,0.54)");
                            if (!gamePlay.equals("aivsai")) {
                                tempButton.setDisable(false);
                            } else {
                                tempButton.setDisable(true);
                            }
                        } else{
                            tempButton.setStyle("-fx-background-color: rgb(5,99,43)");
                            if (gridSize > 3) {
                                tempButton.setDisable(true);
                            }
                        }
                    }
                }
            });
    }

    public void setGameTitle(String Text){
        gameTitle.setText(Text);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }
}