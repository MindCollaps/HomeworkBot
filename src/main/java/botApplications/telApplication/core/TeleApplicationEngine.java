package botApplications.telApplication.core;

import botApplications.telApplication.commands.TeleCmdAddNotificationChannel;
import botApplications.telApplication.commands.TeleCmdRemoveNotificationChannel;
import botApplications.telApplication.commands.TeleCmdStart;
import botApplications.telApplication.librarys.TeleHomeworkConnection;
import botApplications.telApplication.utils.TeleBotUtils;
import botApplications.telApplication.utils.TeleTextUtils;
import com.pengrad.telegrambot.TelegramBot;
import core.Engine;
import homeworkApi.librarys.Class;
import homeworkApi.librarys.Task;

public class TeleApplicationEngine {

    private Engine engine;

    private final String consMsgDef = "[Telegram application]";
    private boolean isRunning = false;

    private TelegramBot bot;

    private TeleCommandHandler commandHandler = new TeleCommandHandler();
    private TeleCommandParser commandParser = new TeleCommandParser();

    private TeleBotUtils botUtils;
    private TeleTextUtils textUtils;

    public TeleApplicationEngine(Engine engine) {
        this.engine = engine;
    }

    public void startBotApplication() {
        if (isRunning) {
            engine.getUtilityBase().printOutput(consMsgDef + " !!!Bot start failure - bot is already running!!!", false);
            return;
        }
        if (engine.getProperties().telBotApplicationToken.equalsIgnoreCase("") || engine.getProperties().telBotApplicationToken == null) {
            engine.getUtilityBase().printOutput(consMsgDef + " !!!Bot start failure - token invalid!!!",false);
            return;
        }
        if(engine.getProperties().telBotApplicationName == "" || engine.getProperties().telBotApplicationName == null){
            engine.getUtilityBase().printOutput(consMsgDef + " !!!Bot start failure - name not set -! type telename <name> to set and retry!!!",false);
            return;
        }
        engine.getUtilityBase().printOutput(consMsgDef + " !Bot start initialized!", false);
        isRunning = true;

        botUtils = new TeleBotUtils(engine);
        textUtils = new TeleTextUtils(engine);

        try {
            bot = new TelegramBot(engine.getProperties().telBotApplicationToken);
        } catch (Exception e){
            engine.getUtilityBase().printOutput(consMsgDef + " !!!Bot start failure - maybe token invalid!!!", false);
            isRunning = false;
            return;
        }
        addCommands();
        bot.setUpdatesListener(new MessageListener(engine));
        engine.getUtilityBase().printOutput(consMsgDef + " !Bot successfully started!",false);
    }

    public void shutdown() {
        if(!isRunning){
            engine.getUtilityBase().printOutput(consMsgDef + " !!!Can't shutdown bot - never starts!!!",false);
            return;
        }
        engine.getUtilityBase().printOutput(consMsgDef + " !Bot is shutting down!", false);
        isRunning = false;
        bot.removeGetUpdatesListener();
    }

    private void addCommands(){
        engine.getUtilityBase().printOutput(consMsgDef + " !Add commands!", false);
        commandHandler.createNewCommand("notification", new TeleCmdAddNotificationChannel());
        commandHandler.createNewCommand("delnotification", new TeleCmdRemoveNotificationChannel());
        commandHandler.createNewCommand("start", new TeleCmdStart());
    }

    public void makeNewTaskAnnouncement(Task task){
        for (int i = 0; i < engine.getProperties().hApiTeleConnection.size(); i++) {
            TeleHomeworkConnection hwCon = engine.getProperties().hApiTeleConnection.get(i);
            if(hasSameClassId(hwCon, task)){
                try{
                    makeTaskAnnouncement(hwCon, task, "Es wurde eine neue Aufgabe ersellt!");
                } catch (Exception e){
                    engine.getProperties().hApiTeleConnection.remove(hwCon);
                }
            }
        }
    }

    private boolean hasSameClassId(TeleHomeworkConnection hwCon, Task task){
        for (Class c:hwCon.getClasses()) {
            for (Class id2:task.getClasses()) {
                if(c.getId().equals(id2.getId())){
                    return true;
                }
            }
        }
        return false;
    }

    public void makeReminderTaskAnnouncement(Task task){
        if(!isRunning)
            return;
        for (int i = 0; i < engine.getProperties().hApiTeleConnection.size(); i++) {
            TeleHomeworkConnection hwCon = engine.getProperties().hApiTeleConnection.get(i);
            if(hasSameClassId(hwCon, task)){
                makeTaskAnnouncement(hwCon, task, "Abgedatum steht kurz bevor!");
            }
        }
    }

    private void makeTaskAnnouncement(TeleHomeworkConnection hwCon, Task task, String headLine){
        if(!isRunning)
            return;
        String fileUrl;
        if(task.getHomeworkFiles().size() != 0){
            fileUrl = task.getHomeworkFiles().get(0).getFileUrl();
        } else {
            fileUrl = "Keine Datein!";
        }
        textUtils.sendMessage(hwCon.getChatId(), "**" + headLine + "** \n\n\nKlasse: " + task.getClasses().get(0).getName() + "\n\nFach: " + task.getSubject() + "\n\nAbgabedatum: " + engine.getUtilityBase().convertDateToString(task.getSubmissionDate(), false) + "\n\nBeschreibung: " + task.getText() + "\n\nFiles: " + fileUrl);
    }

    public void sendToAllClasses(String message){
        if(!isRunning)
            return;
        for (TeleHomeworkConnection c: engine.getProperties().hApiTeleConnection) {
            textUtils.sendMessage(c.getChatId(),message);
        }
    }

    public void sendToSpecificClass(String message, String classId){
        if(!isRunning)
            return;
        for(TeleHomeworkConnection c: engine.getProperties().hApiTeleConnection){
            for (Class ca:c.getClasses()) {
                if(classId.equals(ca.getId())){
                    textUtils.sendMessage(c.getChatId(),message);
                }
            }
        }
    }

    public TelegramBot getBot() {
        return bot;
    }

    public TeleCommandHandler getCommandHandler() {
        return commandHandler;
    }

    public TeleCommandParser getCommandParser() {
        return commandParser;
    }

    public TeleBotUtils getBotUtils() {
        return botUtils;
    }

    public TeleTextUtils getTextUtils() {
        return textUtils;
    }
}
