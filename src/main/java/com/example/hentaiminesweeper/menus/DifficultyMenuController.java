package com.example.hentaiminesweeper.menus;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import com.example.hentaiminesweeper.Window;
import com.example.hentaiminesweeper.structs.GameDifficulty;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.Slider;
import javafx.scene.control.ToggleGroup;

public class DifficultyMenuController implements Initializable{

    public GameDifficulty selectedDifficulty = GameDifficulty.EASY;

    @FXML
    private RadioButton easy, normal, hard, lunatic;

    @FXML
    private Label config;

    @FXML
    private Slider tiles, bombs;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        
        ToggleGroup toggleGroup = new ToggleGroup();
        easy.setToggleGroup(toggleGroup);
        normal.setToggleGroup(toggleGroup);
        hard.setToggleGroup(toggleGroup);
        lunatic.setToggleGroup(toggleGroup);

        easy.setSelected(true);

        toggleGroup.selectedToggleProperty().addListener((observable, oldVal, newVal) -> 
            {
                String id = ((Node) newVal).getId();
                selectedDifficulty = GameDifficulty.convert(id);

                tiles.setValue(selectedDifficulty.size);
                bombs.setValue(selectedDifficulty.mines);
            }
        );

        tiles.valueProperty().addListener((observable, oldValue, newValue) -> {

            //Tiles limit bomb options
            Double value = newValue.doubleValue();
            double bombsMax = Math.pow(value, 2) - 1;
            
            bombs.setMax(bombsMax);
            tiles.setValue(Math.round(value));

            selectedDifficulty.size = (int) tiles.getValue();

            updateConfiguration();
        });

        bombs.valueProperty().addListener((observable, oldValue, newValue) -> {

            Double value = newValue.doubleValue();
            bombs.setValue(Math.round(value));

            selectedDifficulty.mines = (int) bombs.getValue();

            updateConfiguration();
        });

        updateConfiguration();
    }

    @FXML
    protected void startGame(){

        try {
            
            Window.updateDifficulty(selectedDifficulty);
            Window.changeScene(config.getScene(), "game.fxml");
            
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void updateConfiguration(){

        config.setText("Configuration: " + String.valueOf((int)tiles.getValue()) + 
            "x" + String.valueOf((int)tiles.getValue()) + 
            ", " + String.valueOf((int)bombs.getValue()) + " mines"
        );
    }
}
