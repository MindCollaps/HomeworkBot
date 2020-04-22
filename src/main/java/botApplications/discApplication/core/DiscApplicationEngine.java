package botApplications.discApplication.core;

import botApplications.discApplication.commands.DiscCmdAddNotificationChannel;
import botApplications.discApplication.librarys.DiscApplicationFilesHandler;
import botApplications.discApplication.librarys.DiscHomeworkConnection;
import botApplications.discApplication.listeners.ServerBotJoinListener;
import botApplications.discApplication.listeners.ServerMessageListener;
import botApplications.discApplication.utils.DiscTextUtils;
import botApplications.discApplication.utils.DiscUtilityBase;
import core.Engine;
import homeworkApi.librarys.Class;
import homeworkApi.librarys.Task;
import net.dv8tion.jda.core.AccountType;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.JDABuilder;
import net.dv8tion.jda.core.OnlineStatus;
import net.dv8tion.jda.core.entities.Game;

import javax.security.auth.login.LoginException;
import java.awt.*;

public class DiscApplicationEngine {

    private Engine engine;

    private final String consMsgDef = "[Discord application]";
    private boolean isRunning = false;

    private DiscTextUtils textUtils;
    private DiscApplicationFilesHandler filesHandler;
    private DiscUtilityBase utilityBase;

    private DiscCommandHandler commandHandler = new DiscCommandHandler();
    private DiscCommandParser commandParser = new DiscCommandParser();

    private JDABuilder builder;
    private JDA botJDA;

    public DiscApplicationEngine(Engine engine) {
        this.engine = engine;
    }

    public void startBotApplication(){
        if(isRunning){
            engine.getUtilityBase().printOutput(consMsgDef + " !!!Bot start failure - bot is already running!!!", false);
            return;
        }
        if(engine.getProperties().discBotApplicationToken == null){
            if(engine.getProperties().discBotApplicationToken.equalsIgnoreCase("")){
                engine.getUtilityBase().printOutput(consMsgDef + " !!!Bot start failure - token invalid!!!", false);
                return;
            }
        }
        engine.getUtilityBase().printOutput(consMsgDef + " !Bot start initialized!", false);
        isRunning = true;

        filesHandler = new DiscApplicationFilesHandler(engine);
        textUtils = new DiscTextUtils(engine);
        utilityBase = new DiscUtilityBase(engine);

        builder = new JDABuilder(AccountType.BOT);
        builder.setToken(engine.getProperties().discBotApplicationToken);
        builder.setAutoReconnect(true);
        builder.setStatus(OnlineStatus.ONLINE);
        setBotApplicationGame(null, Game.GameType.DEFAULT);
        addBotCommands();
        addBotListeners();
        try {
            botJDA = builder.build();
        } catch (LoginException e) {
            if(engine.getProperties().debug){e.printStackTrace();}
            engine.getUtilityBase().printOutput(consMsgDef + " !!!Bot start failure - maybe token invalid!!!", false);
            isRunning = false;
            return;
        }
        engine.getUtilityBase().printOutput(consMsgDef + " !Bot successfully started!", false);
    }

    private void setBotApplicationGame(String game, Game.GameType type) {
        builder.setGame(new Game("") {
            @Override
            public String getName() {
                if (game != null) {
                    return game;
                } else {
                    return engine.getProperties().discBotApplicationGame;
                }
            }

            @Override
            public String getUrl() {
                return null;
            }

            @Override
            public Game.GameType getType() {
                return type;
            }
        });
    }

    private void addBotCommands(){
        engine.getUtilityBase().printOutput(consMsgDef + " !Add commands!", false);
        commandHandler.createNewCommand("notification", new DiscCmdAddNotificationChannel());
    }

    private void addBotListeners(){
        engine.getUtilityBase().printOutput(consMsgDef + " !Add listeners!", false);
        builder.addEventListener(new ServerMessageListener(engine));
        builder.addEventListener(new ServerBotJoinListener(engine));
    }

    public void shutdownBotApplication() {
        if(!isRunning){
            engine.getUtilityBase().printOutput(consMsgDef + " ~The bot is already offline!", false);
            return;
        }
        engine.getUtilityBase().printOutput(consMsgDef + " ~Bot shutting down!",false);
        try {
            botJDA.shutdownNow();
        } catch (Exception e) {
            engine.getUtilityBase().printOutput(consMsgDef + " ~Bot cant shutdownBotApplication, eventually never starts?", false);
        }
        isRunning = false;
    }

    public void makeNewTaskAnnouncement(Task task){
        if(!isRunning)
            return;

        String fileUrl;
        if(task.getHomeworkFiles().size() != 0){
            fileUrl = task.getHomeworkFiles().get(0).getFileUrl();
        } else {
            fileUrl = "Keine Datein!";
        }
        for (int i = 0; i < engine.getProperties().hApiDiscConnection.size(); i++) {
            DiscHomeworkConnection hwCon = engine.getProperties().hApiDiscConnection.get(i);
            for (Class c:hwCon.getClasses()) {
                for (Class tCId:task.getClasses()) {
                    if(c.getId().equals(tCId.getId())){
                        textUtils.sendCustomMessage("Klasse: `" + tCId.getName() + "`\n\nFach: `" + task.getSubject() + "`\n\nAbgabedatum: `" + engine.getUtilityBase().convertDateToString(task.getSubmissionDate(), false) + "`\n\nBeschreibung: `" + task.getText() + "`" + "\n\nFiles: " + fileUrl + "", botJDA.getGuildById(hwCon.getGuildId()).getTextChannelById(hwCon.getChannelId()), "Es wurde eine neue Aufgabe ersellt!", Color.blue);
                    }
                }
            }
        }
    }

    public void makeReminderTaskAnnouncement(Task task){
        if(!isRunning)
            return;

        String fileUrl;
        if(task.getHomeworkFiles().size() != 0){
            fileUrl = task.getHomeworkFiles().get(0).getFileUrl();
        } else {
            fileUrl = "Keine Datein!";
        }
        for (int i = 0; i < engine.getProperties().hApiDiscConnection.size(); i++) {
            DiscHomeworkConnection hwCon = engine.getProperties().hApiDiscConnection.get(i);
            for (Class c:hwCon.getClasses()) {
                for (Class tCId:task.getClasses()) {
                    if(c.getId().equals(tCId.getId())){
                        textUtils.sendCustomMessage("Klasse: `" + task.getClasses().get(0).getName() + "`\n\nFach: `" + task.getSubject() + "`\n\nAbgabedatum: `" + engine.getUtilityBase().convertDateToString(task.getSubmissionDate(), false) + "`\n\nBeschreibung: `" + task.getText() + "`" + "\n\nFiles: " + fileUrl + "", botJDA.getGuildById(hwCon.getGuildId()).getTextChannelById(hwCon.getChannelId()), "Abgabedatum steht kurzbevor!", Color.cyan);
                    }
                }
            }
        }
    }

    public void sendToAllClasses(String message){
        if(!isRunning)
            return;
        for (DiscHomeworkConnection c: engine.getProperties().hApiDiscConnection) {
            textUtils.sendCustomMessage(message, botJDA.getGuildById(c.getGuildId()).getTextChannelById(c.getChannelId()), "", Color.blue);
        }
    }

    public void sendToSpecificClass(String message, String classId){
        if(!isRunning)
            return;
        for(DiscHomeworkConnection c: engine.getProperties().hApiDiscConnection){
            for (Class ca:c.getClasses()) {
                if(classId.equals(ca.getId())){
                    textUtils.sendCustomMessage(message, botJDA.getGuildById(c.getGuildId()).getTextChannelById(c.getChannelId()), "", Color.blue);
                }
            }
        }

    }

    public DiscTextUtils getTextUtils() {
        return textUtils;
    }

    public DiscApplicationFilesHandler getFilesHandler() {
        return filesHandler;
    }

    public DiscCommandHandler getCommandHandler() {
        return commandHandler;
    }

    public DiscCommandParser getCommandParser() {
        return commandParser;
    }

    public DiscUtilityBase getUtilityBase() {
        return utilityBase;
    }
}
