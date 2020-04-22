package botApplications.telApplication.utils;

import com.pengrad.telegrambot.request.SendMessage;
import com.pengrad.telegrambot.response.SendResponse;
import core.Engine;

public class TeleTextUtils {

    Engine engine;

    public TeleTextUtils(Engine engine) {
        this.engine = engine;
    }

    public SendResponse sendMessage(Long chatId, String message){
        return engine.getTeleApplicationEngine().getBot().execute(new SendMessage(chatId, message));
    }
}
