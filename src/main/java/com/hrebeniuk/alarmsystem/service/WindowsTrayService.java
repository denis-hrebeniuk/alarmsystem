package com.hrebeniuk.alarmsystem.service;

import com.hrebeniuk.alarmsystem.MainApplication;
import com.hrebeniuk.alarmsystem.config.Config;
import javafx.application.Platform;
import javafx.stage.Stage;

import java.awt.*;

public class WindowsTrayService {
    private static final Image trayIconImage = Toolkit.getDefaultToolkit().createImage(MainApplication.class.getResource("images/icon.png"));
    private static final TrayIcon trayIcon = new TrayIcon(trayIconImage);

    private static Boolean isTrayInitialize = false;

    public static void sendNotification(final String type, final String message) {
        if (isTrayInitialize) {
            switch (type) {
                case "WARNING":
                    trayIcon.displayMessage(Config.APP_NAME, message, TrayIcon.MessageType.WARNING);
                    break;
                case "INFORMATION":
                    trayIcon.displayMessage(Config.APP_NAME, message, TrayIcon.MessageType.INFO);
                    break;
                case "ERROR":
                    trayIcon.displayMessage(Config.APP_NAME, message, TrayIcon.MessageType.ERROR);
                    break;
                default:
                    trayIcon.displayMessage(Config.APP_NAME, message, TrayIcon.MessageType.NONE);
                    break;
            }
        } else {
            UtilitiesService.showError("Трей не ініціалізований, неможилво продовжити роботу. Будь ласка, напишіть про цю помилку розробнику.");
        }
    }

    public static void setUpTray(Stage stage) throws AWTException {
        if (!SystemTray.isSupported()) {
            UtilitiesService.showError("Система трею не підтримується. Будь ласка, напишіть про цю помилку розробнику.");
        }

        if (isTrayInitialize) {
            UtilitiesService.showError("Була зафіксована спроба повторно ініціалізувати трей. Будь ласка, напишіть про цю помилку розробнику.");
        }

        final PopupMenu popup = new PopupMenu();
        final SystemTray tray = SystemTray.getSystemTray();

        Platform.setImplicitExit(false);

        trayIcon.setImageAutoSize(true);
        trayIcon.setToolTip(Config.APP_NAME);
        tray.add(trayIcon);

        MenuItem openAppItem = new MenuItem("Відкрити застосунок");
        MenuItem exitAppItem = new MenuItem("Вихід");

        popup.add(openAppItem);
        popup.addSeparator();
        popup.add(exitAppItem);
        trayIcon.setPopupMenu(popup);

        openAppItem.addActionListener(event -> {
            Platform.runLater(() -> {
                stage.show();
            });
        });

        exitAppItem.addActionListener(event -> {
            System.exit(0);
        });

        stage.setOnCloseRequest(windowEvent -> {
            stage.hide();

            sendNotification("INFORMATION", "Ми автоматично сховали застосунок, відкрити його можна в треї.");
        });

        isTrayInitialize = true;
    }
}
