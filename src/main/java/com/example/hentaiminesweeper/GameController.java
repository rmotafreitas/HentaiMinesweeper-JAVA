package com.example.hentaiminesweeper;

import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.Background;
import javafx.scene.layout.Pane;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.util.Duration;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.event.*;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.effect.DropShadow;
import javafx.scene.effect.GaussianBlur;
import javafx.scene.Node;
import javafx.scene.input.MouseEvent;
import javafx.scene.image.Image;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.ResourceBundle;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.example.hentaiminesweeper.DatabaseConnection.SynchronousQueryCompletionListener;
import com.example.hentaiminesweeper.structs.Record;

import javafx.fxml.Initializable;
import javafx.geometry.Insets;

public class GameController implements Initializable {

    private Image flag, bomb;
    private boolean firstClick = false;
    private int flagCount = 0;
    public String apiIMG;

    private Timeline timer;
    public int timeElapsed = 0;

    @FXML
    private ImageView hentai, icon;

    @FXML
    private Pane pain, field, reward;

    @FXML
    private Label timerLabel, difficulty, newBack, newTime, rewardLabel;

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        reward.setVisible(false);

        // butao.setLayoutX(10);
        timerLabel.setVisible(false);
        timerLabel.setFont(new Font("Comic Sans MS", 20));

        // Start timer
        timer = new Timeline(
                new KeyFrame(Duration.seconds(1),
                        new EventHandler<ActionEvent>() {

                            @Override
                            public void handle(ActionEvent event) {

                                timeElapsed++;

                                long MM = (timeElapsed % 3600) / 60;
                                long SS = timeElapsed % 60;
                                String timeInMMSS = String.format("%02d:%02d", MM, SS);

                                timerLabel.setText(timeInMMSS);
                            }
                        }));

        timer.setCycleCount(Timeline.INDEFINITE);

        //Define tile size
        int tilesize = 517 / Window.tiles;
        Window.tilesize = tilesize;

        Window.size = Window.tiles * Window.tilesize;
        onHelloButtonClick();
    }

    @FXML
    protected void returnToTitle(){

        try {
            
            Window.changeScene(timerLabel.getScene(), "main-menu.fxml");

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    protected void onHelloButtonClick() {

        timer.playFromStart();
        timeElapsed = 0;
        timerLabel.setVisible(true);
        timerLabel.setText(String.valueOf(timeElapsed));
        Window.gamer = false;

        difficulty.setText("Difficulty: " + Window.tiles + "x" + Window.tiles + ", " + Window.bombCount + " bombs");

        bombas = gerarBombas();

        flag = new Image(
                "https://media.discordapp.net/attachments/1099068976216154182/1117217972558237786/Sprite-0003.png",
                Window.tilesize,
                Window.tilesize, false, false);
        bomb = new Image(
                "https://media.discordapp.net/attachments/1099068976216154182/1117217972826669167/Sprite-0001.png",
                Window.tilesize,
                Window.tilesize, false, false);

        try {

            apiIMG = Window.getIMagemUwU();
            while (apiIMG.equals(null)) {apiIMG = Window.getIMagemUwU();}
            Image image = new Image(apiIMG, 517, 517,
                    false, false);

            GaussianBlur blurEffect = new GaussianBlur(20);

            hentai.setEffect(blurEffect);
            hentai.setImage(image);

            hentai.setFitWidth(515);
            hentai.setFitHeight(515);

            desenharCoisaQuadradão();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    protected int[][] gerarMatriz() {
        int[][] matriz = new int[(int) (Window.size / Window.tilesize)][(int) (Window.size
                / Window.tilesize)];

        for (int i = 0; i < (int) (Window.size / Window.tilesize); i++) {
            for (int ii = 0; ii < (int) (Window.size / Window.tilesize); ii++) {

                matriz[i][ii] = 0;
            }
        }

        return matriz;
    }

    protected int[][] bombas = gerarBombas();

    protected int[][] gerarBombas() {

        int[][] matriz = gerarMatriz();
        int numberMaxBombas = Window.bombCount;
        for (int i = 0; i < numberMaxBombas; i++) {
            int x = (int) (Math.random() * (Window.size / Window.tilesize));
            int y = (int) (Math.random() * (Window.size / Window.tilesize));

            if (matriz[y][x] == -69) {
                i--;
            } else {
                matriz[y][x] = -69;
            }
        }

        return matriz;
    }

    protected void openEspaço(int y, int x, int[][] bombas) {
        int WIDTH = bombas[0].length;
        int HEIGHT = bombas.length;

        if (y < 0 || x < 0 || x > WIDTH - 1 || y > HEIGHT - 1 || bombas[y][x] != 0)
            return;

        int bombs = 0;
        for (int i = y - 1; i <= y + 1; i++) {
            for (int j = x - 1; j <= x + 1; j++) {
                if (!(j < 0 || i < 0 || j > WIDTH - 1 || i > HEIGHT - 1) &&
                        (bombas[i][j] == -69 || bombas[i][j] == -420))
                    bombs++;
            }
        }

        if (bombs == 0) {
            bombas[y][x] = -1983;
            for (int i = y - 1; i <= y + 1; i++) {
                for (int j = x - 1; j <= x + 1; j++) {
                    if (!(j < 0 || i < 0 || j > WIDTH - 1 || i > HEIGHT - 1)) {
                        if (i != y || j != x)
                            openEspaço(i, j, bombas);
                    }
                }
            }
        } else {
            bombas[y][x] = bombs;
        }
    }

    /*
     * 0: not played yet
     * -69: bomb
     * -1983: no bombs around
     * -420: flag on bomb
     * -2: flag not in bomb
     * numbers>0:number of bombs in 8
     */

    private void desenharCoisaQuadradão() {

        for (int i = 0; i < (int) (Window.size / Window.tilesize); i++) {
            for (int ii = 0; ii < (int) (Window.size / Window.tilesize); ii++) {

                Button b = new Button();
                b.setPadding(Insets.EMPTY);

                b.setTranslateX(i * Window.tilesize);
                b.setTranslateY(ii * Window.tilesize);

                b.setMinWidth(Window.tilesize);
                b.setMaxHeight(Window.tilesize);
                b.setMinHeight(Window.tilesize);
                b.setMaxWidth(Window.tilesize);

                b.setOnMouseClicked(new EventHandler<MouseEvent>() {

                    @Override
                    public void handle(MouseEvent event) {

                        if (Window.gamer)
                            return;

                        if (!firstClick)
                            firstClick = true;

                        MouseButton button = event.getButton();

                        int iii = (int) (b.getTranslateX() / Window.tilesize);
                        int iiii = (int) (b.getTranslateY() / Window.tilesize);

                        if (button == MouseButton.PRIMARY) {

                            int value = bombas[iii][iiii];
                            if (value == -69) {

                                // if (!firstClick) {

                                // // Change the bomb's place
                                // while(true){

                                // int x = (int) (Math.random() * (HelloApplication.size /
                                // HelloApplication.tilesize));
                                // int y = (int) (Math.random() * (HelloApplication.size /
                                // HelloApplication.tilesize));

                                // if(bombas[x][y] == -69) continue;

                                // bombas[x][y] = -69;
                                // bombas[iii][iiii] = 0;

                                // openEspaço(iii, iiii, bombas);
                                // break;
                                // }
                                // return;
                                // }

                                endPlay(false);
                            }

                            openEspaço(iii, iiii, bombas);

                        } else if (button == MouseButton.SECONDARY) {

                            int value = bombas[iii][iiii];

                            switch (value) {
                                case 0:
                                    bombas[iii][iiii] = -2;
                                    flagCount++;
                                    break;
                                case -69:
                                    bombas[iii][iiii] = -420;
                                    flagCount++;
                                    break;
                                case -2:
                                    bombas[iii][iiii] = 0;
                                    flagCount--;
                                    break;
                                case -420:
                                    bombas[iii][iiii] = -69;
                                    flagCount--;
                                    break;
                            }
                        }

                        atualizaQuadradão();
                    }
                });

                pain.getChildren().add(b);
            }
        }
    }

    private void atualizaQuadradão() {

        List<Button> add = new ArrayList<Button>();
        List<Button> remove = new ArrayList<Button>();

        for (Node button : pain.getChildren()) {

            if (button instanceof Button && !(button.getId() != null && button.getId().equals("splay"))) {

                Button b = (Button) button;

                int value = bombas[(int) (b.getTranslateX() / Window.tilesize)][(int) (b.getTranslateY()
                        / Window.tilesize)];

                if ((value == -420 || value == -2 || value == -69) && Window.gamer) {

                    b.setText("");
                    ImageView view = new ImageView(flag);
                    b.setGraphic(view);
                    b.setBackground(null);
                    continue;

                } else if (value == -1983) {

                    b.setVisible(false);
                } else if (value == -420 || value == -2) {

                    b.setText("");
                    ImageView view = new ImageView(bomb);
                    b.setGraphic(view);
                    b.setBackground(null);

                } else if (value > 0) {

                    b.setText(String.valueOf(value));
                    b.setBackground(null);

                    DropShadow shadow = new DropShadow();
                    shadow.setRadius(Window.tilesize / 2);

                    b.setEffect(shadow);
                    b.setStyle("-fx-font-weight: bold");
                    b.setStyle("-fx-font-size: 12px");
                    b.setStyle("-fx-effect: dropshadow(one-pass-box, black, 8, 0.0, 2, 0)");

                    b.setStyle(
                            "-fx-text-fill: white");

                } else if (value == 0 || value == -69) {

                    Button newButton = new Button();
                    newButton.setTranslateX(b.getTranslateX());
                    newButton.setTranslateY(b.getTranslateY());
                    newButton.setMinWidth(Window.tilesize);

                    newButton.setMaxHeight(Window.tilesize);
                    newButton.setMinHeight(Window.tilesize);
                    newButton.setMaxWidth(Window.tilesize);
                    newButton.setOnMouseClicked(b.getOnMouseClicked());

                    add.add(newButton);
                    remove.add(b);
                }
            }
        }

        pain.getChildren().removeAll(remove);
        pain.getChildren().addAll(add);

        if (verificarVictórya())
            endPlay(true);

    }

    private String corAleatórya() {
        return String.valueOf((new Random().nextInt((255 - 1) + 1) + 1));
    }

    private boolean verificarVictórya() {

        for (int i = 0; i < (int) (Window.size / Window.tilesize); i++) {
            for (int ii = 0; ii < (int) (Window.size / Window.tilesize); ii++) {
                if (bombas[i][ii] == -2 || bombas[i][ii] == 0) {
                    return false;
                }
            }
        }

        return true;
    }

    private void endPlay(boolean win) {

        timer.stop();

        if (win) {

            hentai.setEffect(null);

            for (Node button : pain.getChildren()) {

                if (button instanceof Button && !(button.getId() != null && button.getId().equals("splay"))) {
                    Button b = (Button) button;
                    b.setBackground(null);
                    b.setText("");
                    b.setGraphic(null);
                }
            }

            DatabaseConnection.addUserRecord(timeElapsed, Window.difficulty, apiIMG);
            
            showRewardsPopUp();
            return;
        }

        Window.gamer = true;
    }

    private void showRewardsPopUp(){

        if(Window.difficulty.getSize() == -1) return;

        newBack.setVisible(false);
        newTime.setVisible(false);
        reward.setVisible(true);

        int reward = Utils.calculateCurrentReward();
        rewardLabel.setText(reward + " coins!!");

        Image image = new Image(getClass().getResource("/com/example/hentaiminesweeper/images/coin.png").toString(), icon.getFitWidth(), icon.getFitHeight(), true, true);
        icon.setImage(image);

        DatabaseConnection.getUserTimes(Main.account.id, new SynchronousQueryCompletionListener() {

            @Override
            public void queryFinished(Object[] returnValue) {
                
                boolean newImage = Stream.of(returnValue).map(r -> (Record) r).filter(r -> r.image.equals(apiIMG)).collect(Collectors.toList()).size() != 0;
                
                DatabaseConnection.getUserTimesOfSize(Main.account.id, Window.difficulty.size, new SynchronousQueryCompletionListener() {
                    
                    @Override
                    public void queryFinished(Object[] returnValue) {
                        
                        boolean bestTime = ((com.example.hentaiminesweeper.structs.Record) returnValue[0]).getTime() <= timeElapsed;
                        Platform.runLater(() -> {
        
                            newTime.setVisible(bestTime);
                            newBack.setVisible(newImage);
                        });
                    }
                    
                });

            }
            
        });
    }

    @FXML
    protected void claimRewards()
    {

        reward.setVisible(false);
        DatabaseConnection.giveCoinsToUser(Utils.calculateCurrentReward());
    }
}