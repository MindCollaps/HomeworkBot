package botApplications.telApplication.commands;

import com.pengrad.telegrambot.model.Update;
import core.Engine;

public interface TeleCommand {

    boolean called(Update command, Engine engine, String[] args);
    void action(Update command, Engine engine, String[] args);
    String help(Engine engine, String[] args);
}
