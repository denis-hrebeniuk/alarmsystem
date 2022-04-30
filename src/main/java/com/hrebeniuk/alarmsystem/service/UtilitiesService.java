package com.hrebeniuk.alarmsystem.service;

import com.hrebeniuk.alarmsystem.MainApplication;
import com.hrebeniuk.alarmsystem.config.Config;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.Optional;
import java.util.prefs.Preferences;

public class UtilitiesService {
    public static Preferences prefs = Preferences.userNodeForPackage(UtilitiesService.class);
    public static Clip clip;

    public static void showError(final String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(Config.APP_NAME);

        alert.setHeaderText(null);
        alert.setContentText(message);

        Optional<ButtonType> option = alert.showAndWait();

        if (option.get() == null) {
            System.exit(0);
        } else {
            System.exit(0);
        }
    }

    public static void showAlert(final String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(Config.APP_NAME);

        alert.setHeaderText(null);
        alert.setContentText(message);

        alert.showAndWait();
    }

    public static String getUrlContent(final String urlToGetContent) {
        StringBuilder content = new StringBuilder();

        try {
            URL url = new URL(urlToGetContent);
            URLConnection urlConnection = url.openConnection();
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));

            String line;
            while ((line = bufferedReader.readLine()) != null) {
                content.append(line + "\n");
            }
            bufferedReader.close();
        } catch (Exception e) {
            showError("Не вдалось отримати інформацію від потрібних джерел, неможилово продовжити роботу. Будь ласка, напишіть про цю помилку розробнику.");
        }
        return content.toString();
    }

    public static synchronized void playSound(final String name) {
        new Thread(new Runnable() {
            public void run() {
                try {
                    clip = AudioSystem.getClip();
                    InputStream audioSource = MainApplication.class.getResourceAsStream("sounds/" + name);
                    InputStream bufferedIn = new BufferedInputStream(audioSource);
                    AudioInputStream inputStream = AudioSystem.getAudioInputStream(bufferedIn);
                    clip.open(inputStream);
                    clip.start();
                } catch (Exception e) {
                    showError("Не вдалось програти аудіо, напишіть про цю помилку розробнику.");
                }
            }
        }).start();
    }
}
