package com.hrebeniuk.alarmsystem.service;

import com.hrebeniuk.alarmsystem.MainApplication;
import com.hrebeniuk.alarmsystem.config.Config;
import com.hrebeniuk.alarmsystem.controllers.MainController;
import javafx.scene.image.Image;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAccessor;
import java.util.Date;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

public class AlarmService {
    private static Timer checkAlarm;
    private static Boolean isTimerWorking = false;
    private static Date lastAlarmStartDate = new Date(0);
    private static Date lastAlarmEndDate = new Date(0);
    private static Boolean firstInit = true;
    private static Boolean stateChosen = false;
    public static String alarmVoice = UtilitiesService.prefs.get("voice", "Жіночий голос");

    public static void setUpState(final String state) {
        if (state.equals("Оберіть Вашу область")) {
            return;
        }

        if (isTimerWorking) {
            lastAlarmStartDate = new Date(0);
            lastAlarmEndDate = new Date(0);
            checkAlarm.cancel();
            stateChosen = true;
            isTimerWorking = false;
        }

        checkAlarm = new Timer();
        checkAlarm.schedule(new TimerTask() {
            @Override
            public void run() {
                isTimerWorking = true;

                JSONObject mapDataObject = new JSONObject(UtilitiesService.getUrlContent(Config.ALARM_INFORMATION_URL));
                JSONObject statesObject = mapDataObject.getJSONObject("states");
                Boolean alarm = statesObject.getJSONObject(state).getBoolean("enabled");

                MainController mainController = MainApplication.fxmlLoader.getController();

                TemporalAccessor temporalAccessor;

                if (alarm) {
                    temporalAccessor = DateTimeFormatter.ISO_INSTANT.parse(statesObject.getJSONObject(state).getString("enabled_at"));
                    Instant instant = Instant.from(temporalAccessor);
                    Date date = Date.from(instant);
                    String enabledAt = new SimpleDateFormat("d MMMM yyyy р. в HH:mm", new Locale("uk")).format(date);

                    if (stateChosen || firstInit) {
                        lastAlarmStartDate = date;
                    }

                    mainController.statusImage.setImage(new Image(MainApplication.class.getResource("images/alarm.png").toString()));
                    mainController.statusText.setText("Повітряна тривога");
                    mainController.statusDescriptionText.setText("Пройдіть до найближчого укриття, віримо в ЗСУ!");
                    mainController.alarmDateText.setText("Почалася: " + enabledAt);

                    if (!stateChosen && !firstInit && date.after(lastAlarmStartDate)) {
                        WindowsTrayService.sendNotification("WARNING", "Повітряна тривога! Пройдіть до найближчого укриття!");
                        UtilitiesService.playSound("alarm.wav");
                        lastAlarmStartDate = date;
                    }
                } else {
                    temporalAccessor = DateTimeFormatter.ISO_INSTANT.parse(statesObject.getJSONObject(state).getString("disabled_at"));
                    Instant instant = Instant.from(temporalAccessor);
                    Date date = Date.from(instant);

                    if (stateChosen || firstInit) {
                        lastAlarmEndDate = date;
                    }

                    mainController.statusImage.setImage(new Image(MainApplication.class.getResource("images/no-alarm.png").toString()));
                    mainController.statusText.setText("Немає тривоги");
                    mainController.statusDescriptionText.setText("Відпочиваємо, допомагаємо та чекаємо на перемогу!");
                    mainController.alarmDateText.setText("");

                    if (!stateChosen && !firstInit && date.after(lastAlarmEndDate)) {
                        WindowsTrayService.sendNotification("INFORMATION", "Відбій повітряної тривоги!");
                        switch (alarmVoice) {
                            case "Повна фраза Арестовича":
                                UtilitiesService.playSound("arestovich-alarm-off.wav");
                                break;
                            case "Жіночий голос":
                                UtilitiesService.playSound("woman-alarm-off.wav");
                                break;
                        }
                        lastAlarmEndDate = date;
                    }
                }
                firstInit = false;
                stateChosen = false;
            }
        }, 0, Config.ALARM_CHECK_INTERVAL);
    }
}
