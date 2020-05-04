package utils;

import botApplications.discApplication.librarys.DiscHomeworkConnection;
import botApplications.telApplication.librarys.TeleHomeworkConnection;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;

public class Properties implements Serializable {

    public static final long serialVersionUID = 42L;

    public int apiPort = 0;

    //Discord BotApplication stuff
    public String discBotApplicationToken = "";
    public String discBotApplicationGame = "-notification help - to see help";
    public String discBotApplicationPrefix = "-";

    //telegram BotApplication stuff
    public String telBotApplicationToken = "";
    public String telBotApplicationName;

    //BotMessage times
    public final int veryLongTime = 60000;
    public final int longTime = 10000;
    public final int middleTime = 6000;
    public final int shortTime = 4000;

    //Engine stuff
    public boolean debug = false;
    public boolean showTime = false;
    public int saveSpeed = 10;

    //Homework Api stuff
    public String hApiLatestPostedTaskId = "";
    public Date hApiLatestPostedTaskDate;
    public Date hApiLatestPostedTaskRemindDate;
    public ArrayList<DiscHomeworkConnection> hApiDiscConnection = new ArrayList<>();
    public ArrayList<TeleHomeworkConnection> hApiTeleConnection = new ArrayList<>();
    public int hApiReminderUpdateSpeed = 60;
}
