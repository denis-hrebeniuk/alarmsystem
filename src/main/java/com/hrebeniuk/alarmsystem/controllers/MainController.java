package com.hrebeniuk.alarmsystem.controllers;

import com.hrebeniuk.alarmsystem.config.Config;
import com.hrebeniuk.alarmsystem.service.AlarmService;
import com.hrebeniuk.alarmsystem.service.UtilitiesService;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ChoiceBox;
import javafx.scene.image.ImageView;
import javafx.scene.text.Text;

import java.awt.*;
import java.net.URI;

public class MainController {
    @FXML
    public Text state;

    @FXML
    public ChoiceBox<String> statesBox;

    @FXML
    public ImageView statusImage;

    @FXML
    public Text statusText;

    @FXML
    public Text statusDescriptionText;

    @FXML
    public Text alarmDateText;

    @FXML
    public Text versionText;

    @FXML
    public ChoiceBox<String> voicesBox;

    @FXML
    public void onStatesBoxSelected(ActionEvent actionEvent) {
        String stateString = statesBox.getValue();

        if (stateString != null) {
            UtilitiesService.prefs.put("state", stateString);
            state.setText(stateString);
            statesBox.setValue(stateString);
            statesBox.setVisible(false);
            AlarmService.setUpState(stateString);
        }
    }

    @FXML
    public void onChooseRegionPressed() {
        statesBox.setVisible(!statesBox.isVisible());
    }

    @FXML
    public void onStopSoundPressed() {
        UtilitiesService.clip.stop();
    }

    @FXML
    public void onGitHubPressed() {
        Desktop desktop = Desktop.isDesktopSupported() ? Desktop.getDesktop() : null;
        if (desktop != null && desktop.isSupported(Desktop.Action.BROWSE)) {
            try {
                desktop.browse(URI.create(Config.GITHUB));
            } catch (Exception e) {
                UtilitiesService.showError("Не вдалося відкрити посилання на GitHub, напишіть про цю помилку розробнику.");
            }
        }
    }

    @FXML
    public void onVoicesBoxSelected(ActionEvent actionEvent) {
        String voiceString = voicesBox.getValue();

        if (voiceString != null) {
            UtilitiesService.prefs.put("voice", voiceString);
            voicesBox.setValue(voiceString);
            AlarmService.alarmVoice = voiceString;
        }
    }
}