package core;

import botApplications.discApplication.core.DiscApplicationEngine;
import botApplications.response.ResponseHandler;
import botApplications.telApplication.core.TeleApplicationEngine;
import botRequestApi.api.BotRequestApi;
import homeworkApi.core.HomeworkApiEngine;
import utils.FileUtils;
import utils.Properties;
import utils.UtilityBase;

public class Engine {

    private final String consMsgDef = "[Engine]";

    private FileUtils fileUtils = new FileUtils(this);
    private UtilityBase utilityBase = new UtilityBase(this);
    private Properties properties;
    private HomeworkApiEngine homeworkApiEngine = new HomeworkApiEngine(this);
    private BotRequestApi botRequestApi = new BotRequestApi(this);

    private DiscApplicationEngine discApplicationEngine = new DiscApplicationEngine(this);
    private TeleApplicationEngine teleApplicationEngine = new TeleApplicationEngine(this);
    private ResponseHandler responseHandler = new ResponseHandler(this);
    private ConsoleCommandHandler consoleCommandHandler;

    public void boot(String[] args) {
        consoleCommandHandler = new ConsoleCommandHandler(this);
        loadProperties();
        handleArgs(args);
        new Thread(new SaveThread(this)).start();
    }

    private void handleArgs(String[] args) {
        if (args.length > 0) {
            switch (args[0]) {
                case "test":
                    botRequestApi.boot(true);
                    break;
                case "start":
                    botRequestApi.boot(false);
                    discApplicationEngine.startBotApplication();
                    teleApplicationEngine.startBotApplication();
                    homeworkApiEngine.boot();
                    break;

                default:
                    botRequestApi.boot(false);
                    break;
            }
        } else {
            botRequestApi.boot(false);
        }
    }

    public void loadProperties() {
        utilityBase.printOutput(consMsgDef + " !Loading properties!", false);
        try {
            properties = (Properties) fileUtils.loadObject(fileUtils.home + "/properties.prop");
        } catch (Exception e) {
            e.printStackTrace();
            utilityBase.printOutput(consMsgDef + " !!!Error while loading properties - maybe never created -> creating new file!!!",false);
            properties = new Properties();
        }
    }

    public void saveProperties() {
        utilityBase.printOutput(consMsgDef + " !Saving properties!",false);
        try {
            fileUtils.saveObject(fileUtils.home + "/properties.prop", properties);
        } catch (Exception e) {
            if (properties.debug) {
                e.printStackTrace();
            }
            utilityBase.printOutput(consMsgDef + " !!!Error while saving properties - maybe no permission!!!",false);
        }
    }

    public void shutdown() {
        saveProperties();
        System.exit(0);
    }

    public FileUtils getFileUtils() {
        return fileUtils;
    }

    public UtilityBase getUtilityBase() {
        return utilityBase;
    }

    public Properties getProperties() {
        return properties;
    }

    public DiscApplicationEngine getDiscEngine() {
        return discApplicationEngine;
    }

    public DiscApplicationEngine getDiscApplicationEngine() {
        return discApplicationEngine;
    }

    public TeleApplicationEngine getTeleApplicationEngine() {
        return teleApplicationEngine;
    }

    public HomeworkApiEngine getHomeworkApiEngine() {
        return homeworkApiEngine;
    }

    public ResponseHandler getResponseHandler() {
        return responseHandler;
    }

    public BotRequestApi getBotRequestApi() {
        return botRequestApi;
    }

    public ConsoleCommandHandler getConsoleCommandHandler() {
        return consoleCommandHandler;
    }
}
