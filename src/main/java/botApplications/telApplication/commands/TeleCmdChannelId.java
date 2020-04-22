package botApplications.telApplication.commands;

import com.pengrad.telegrambot.model.Chat;
import com.pengrad.telegrambot.model.Update;
import core.Engine;

public class TeleCmdChannelId implements TeleCommand{
    @Override
    public boolean called(Update command, Engine engine, String[] args) {
        if (!command.message().chat().type().equals(Chat.Type.group) && !command.message().chat().type().equals(Chat.Type.supergroup) && !command.message().chat().type().equals(Chat.Type.channel)) {
            engine.getTeleApplicationEngine().getTextUtils().sendMessage(command.message().chat().id(), "Diese Befehl ist nur in Gruppen oder Channels möglich!");
            return false;
        } else
            return true;
    }

    @Override
    public void action(Update command, Engine engine, String[] args) {
        engine.getTeleApplicationEngine().getTextUtils().sendMessage(command.message().chat().id(), "Diese Gruppe hat die ID: " + command.message().chat().id());
    }

    @Override
    public String help(Engine engine, String[] args) {
        return "";
    }
}
