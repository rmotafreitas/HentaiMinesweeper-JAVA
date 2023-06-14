package com.example.hentaiminesweeper;

import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.Background;
import javafx.scene.layout.Pane;
import javafx.scene.text.Font;
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

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.ResourceBundle;
import java.util.Timer;
import java.util.TimerTask;
import javafx.fxml.Initializable;

public class HelloController implements Initializable {

    private Image flag, bomb;
    private boolean firstClick = false;
    private int flagCount = 0;
    public String apiIMG;

    private Timeline timer;
    public int timeElapsed = 0;

    @FXML
    private ImageView hentai;

    @FXML
    private Button butao;

    @FXML
    private Pane pain;

    @FXML
    private Label timerLabel;

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        // butao.setLayoutX(10);
        timerLabel.setVisible(false);
        timerLabel.setTranslateY(HelloApplication.size - timerLabel.getLayoutY() + 5);

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
    }

    @FXML
    protected void onHelloButtonClick() {

        timer.playFromStart();
        timeElapsed = 0;
        timerLabel.setVisible(true);
        timerLabel.setText(String.valueOf(timeElapsed));
        HelloApplication.gamer = false;

        bombas = gerarBombas();

        flag = new Image(
                "https://media.discordapp.net/attachments/1099068976216154182/1117217972558237786/Sprite-0003.png",
                25,
                25, false, false);
        bomb = new Image(
                "https://media.discordapp.net/attachments/1099068976216154182/1117217972826669167/Sprite-0001.png",
                25,
                25, false, false);

        try {

            apiIMG = HelloApplication.getIMagemUwU();
            while (apiIMG.equals(null))
                apiIMG = HelloApplication.getIMagemUwU();
            Image image = new Image(apiIMG, HelloApplication.size, HelloApplication.size,
                    false, false);

            hentai.setFitHeight(HelloApplication.size);
            hentai.setFitWidth(HelloApplication.size);

            GaussianBlur blurEffect = new GaussianBlur(20);
            hentai.setEffect(blurEffect);

            hentai.setImage(image);

            butao.setVisible(false);
            butao.setTranslateY(HelloApplication.size - butao.getLayoutY() + 5);

            desenharCoisaQuadradão();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    protected int[][] gerarMatriz() {
        int[][] matriz = new int[(int) (HelloApplication.size / HelloApplication.tilesize)][(int) (HelloApplication.size
                / HelloApplication.tilesize)];

        for (int i = 0; i < (int) (HelloApplication.size / HelloApplication.tilesize); i++) {
            for (int ii = 0; ii < (int) (HelloApplication.size / HelloApplication.tilesize); ii++) {

                matriz[i][ii] = 0;
            }
        }

        return matriz;
    }

    protected int[][] bombas = gerarBombas();

    protected int[][] gerarBombas() {

        int[][] matriz = gerarMatriz();
        int numberMaxBombas = HelloApplication.bombCount;
        for (int i = 0; i < numberMaxBombas; i++) {
            int x = (int) (Math.random() * (HelloApplication.size / HelloApplication.tilesize));
            int y = (int) (Math.random() * (HelloApplication.size / HelloApplication.tilesize));

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

        for (int i = 0; i < (int) (HelloApplication.size / HelloApplication.tilesize); i++) {
            for (int ii = 0; ii < (int) (HelloApplication.size / HelloApplication.tilesize); ii++) {

                Button b = new Button();

                b.setTranslateX(i * HelloApplication.tilesize);
                b.setTranslateY(ii * HelloApplication.tilesize);

                b.setMinWidth(HelloApplication.tilesize);
                b.setMaxHeight(HelloApplication.tilesize);
                b.setMinHeight(HelloApplication.tilesize);
                b.setMaxWidth(HelloApplication.tilesize);

                b.setOnMouseClicked(new EventHandler<MouseEvent>() {

                    @Override
                    public void handle(MouseEvent event) {

                        if (HelloApplication.gamer)
                            return;

                        if (!firstClick)
                            firstClick = true;

                        MouseButton button = event.getButton();

                        int iii = (int) (b.getTranslateX() / HelloApplication.tilesize);
                        int iiii = (int) (b.getTranslateY() / HelloApplication.tilesize);

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

            if (button instanceof Button && !(button.getId() != null && button.getId().equals("butao"))) {

                Button b = (Button) button;

                int value = bombas[(int) (b.getTranslateX() / HelloApplication.tilesize)][(int) (b.getTranslateY()
                        / HelloApplication.tilesize)];

                if ((value == -420 || value == -2 || value == -69) && HelloApplication.gamer) {

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
                    shadow.setRadius(HelloApplication.tilesize / 2);

                    b.setEffect(shadow);
                    b.setStyle("-fx-font-weight: bold");
                    b.setStyle("-fx-font-size: 12px");

                    b.setStyle(
                            "-fx-text-fill: rgb(" + corAleatórya() + "," + corAleatórya() + "," + corAleatórya() + ")");

                } else if (value == 0 || value == -69) {

                    Button newButton = new Button();
                    newButton.setTranslateX(b.getTranslateX());
                    newButton.setTranslateY(b.getTranslateY());
                    newButton.setMinWidth(HelloApplication.tilesize);

                    newButton.setMaxHeight(HelloApplication.tilesize);
                    newButton.setMinHeight(HelloApplication.tilesize);
                    newButton.setMaxWidth(HelloApplication.tilesize);
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

        for (int i = 0; i < (int) (HelloApplication.size / HelloApplication.tilesize); i++) {
            for (int ii = 0; ii < (int) (HelloApplication.size / HelloApplication.tilesize); ii++) {
                if (bombas[i][ii] == -2 || bombas[i][ii] == 0) {
                    return false;
                }
            }
        }

        return true;
    }

    private void endPlay(boolean win) {

        butao.setVisible(true);
        timer.stop();

        if (win) {

            hentai.setEffect(null);

            for (Node button : pain.getChildren()) {

                if (button instanceof Button && !(button.getId() != null && button.getId().equals("butao"))) {
                    Button b = (Button) button;
                    b.setBackground(null);
                    b.setText("");
                    b.setGraphic(null);
                }
            }

            DatabaseConnection.addUserRecord(timeElapsed, (int) (HelloApplication.size / HelloApplication.tilesize), apiIMG);
            return;
        }

        HelloApplication.gamer = true;
    }
}