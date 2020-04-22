package botApplications.telApplication.utils;

import com.pengrad.telegrambot.model.Update;
import core.Engine;

public class TeleBotUtils {

    Engine engine;

    public TeleBotUtils(Engine engine) {
        this.engine = engine;
    }

    public void sendCommand(Update update) {
        engine.getTeleApplicationEngine().getCommandHandler().handleCommand(engine.getTeleApplicationEngine().getCommandParser().parseCommand(update.message().text(), engine, update));
    }

    public void sendOwnCommand(Update update, String own) {
        engine.getTeleApplicationEngine().getCommandHandler().handleCommand(engine.getTeleApplicationEngine().getCommandParser().parseCommand(own, engine, update));
    }

    public String cutNameOutOfString(String string, boolean cutAllBevore) {
        String newString = "";
        String botName = engine.getProperties().telBotApplicationName;
        char[] chars = string.toCharArray();

        int compareCount = 0;

        for (int i = 0; i < chars.length; i++) {
            if (compareCount == 0) {
                if (chars[i] != botName.toCharArray()[compareCount]) {
                    if (!cutAllBevore)
                        newString = newString + chars[i];
                } else {
                    compareCount++;
                }
            } else {
                if (compareCount < botName.toCharArray().length) {
                    if (chars[i] == botName.toCharArray()[compareCount]) {
                        compareCount++;
                        continue;
                    }
                }
                newString = newString + chars[i];
            }
        }
        return newString;
    }
}
