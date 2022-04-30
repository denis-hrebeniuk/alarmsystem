package com.hrebeniuk.alarmsystem;

import com.hrebeniuk.alarmsystem.config.Config;
import com.hrebeniuk.alarmsystem.controllers.MainController;
import com.hrebeniuk.alarmsystem.service.AlarmService;
import com.hrebeniuk.alarmsystem.service.UtilitiesService;
import com.hrebeniuk.alarmsystem.service.WindowsTrayService;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import org.json.JSONObject;

import java.awt.*;
import java.io.IOException;
import java.util.ArrayList;

public class MainApplication extends Application {
    public static FXMLLoader fxmlLoader;

    @Override
    public void start(Stage stage) throws IOException, AWTException {
        fxmlLoader = new FXMLLoader(MainApplication.class.getResource("view/main-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 900, 600);

        WindowsTrayService.setUpTray(stage);
        setUpView();

        stage.setTitle(Config.APP_NAME);
        stage.getIcons().add(new Image(MainApplication.class.getResource("images/icon.png").toString()));
        stage.setResizable(false);
        stage.setScene(scene);
        stage.show();
    }

    public void setUpView() {
        setUpStates();
        setUpVoices();

        MainController mainController = fxmlLoader.getController();
        mainController.versionText.setText(Config.APP_VERSION);
    }

    public void setUpStates() {
        MainController mainController = fxmlLoader.getController();

        ObservableList<String> statesList = FXCollections.observableArrayList(new ArrayList<String>());
        JSONObject mapDataObject = new JSONObject(UtilitiesService.getUrlContent(Config.ALARM_INFORMATION_URL));
        JSONObject statesObject = mapDataObject.getJSONObject("states");

        for (String state : statesObject.keySet()) {
            statesList.add(state);
        }

        statesList.sort(null);
        mainController.statesBox.setItems(statesList);

        String state = UtilitiesService.prefs.get("state", "Оберіть Вашу область");
        mainController.state.setText(state);
        AlarmService.setUpState(state);
    }

    public void setUpVoices() {
        MainController mainController = fxmlLoader.getController();

        ObservableList<String> voicesList = FXCollections.observableArrayList("Повна фраза Арестовича", "Жіночий голос");
        mainController.voicesBox.setItems(voicesList);
        mainController.voicesBox.setValue(AlarmService.alarmVoice);
    }

    public static void main(String[] args) {
        launch();
    }
}