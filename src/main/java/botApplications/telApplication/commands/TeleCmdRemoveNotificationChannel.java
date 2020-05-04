package botApplications.telApplication.commands;

import botApplications.telApplication.librarys.TeleHomeworkConnection;
import com.pengrad.telegrambot.model.Chat;
import com.pengrad.telegrambot.model.Update;
import core.Engine;

public class TeleCmdRemoveNotificationChannel implements TeleCommand {
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
        for (TeleHomeworkConnection con:engine.getProperties().hApiTeleConnection){
            if(con.getChatId() == command.message().chat().id()){
                engine.getTeleApplicationEngine().getTextUtils().sendMessage(command.message().chat().id(), "Der Bot wurde entfernt!");
                engine.getProperties().hApiTeleConnection.remove(con);
                return;
            }
        }
        engine.getTeleApplicationEngine().getTextUtils().sendMessage(command.message().chat().id(), "Diese Gruppe wird nicht vom Bot betreut!");
    }

    @Override
    public String help(Engine engine, String[] args) {
        return "Löscht den Bot aus der Aktuellen Gruppe";
    }
}
