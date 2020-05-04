package botApplications.telApplication.commands;

import botApplications.telApplication.librarys.TeleHomeworkConnection;
import com.pengrad.telegrambot.model.Update;
import core.Engine;
import homeworkApi.librarys.User;

import java.util.ArrayList;

public class TeleCmdStart implements TeleCommand {
    @Override
    public boolean called(Update command, Engine engine, String[] args) {
        return true;
    }

    @Override
    public void action(Update command, Engine engine, String[] args) {
        ArrayList<TeleHomeworkConnection> allConnections = engine.getProperties().hApiTeleConnection;
        for (TeleHomeworkConnection con : allConnections) {
            if (con.getChatId() == command.message().chat().id()) {
                engine.getTeleApplicationEngine().getTextUtils().sendMessage(command.message().chat().id(), "Der Bot ist bereits in dieser Gruppe als Messengerbot tätig!");
                return;
            }
        }

        if(args.length>0){
            String key = engine.getTeleApplicationEngine().getBotUtils().cutNameOutOfString(command.message().text(), true);
            User user = engine.getHomeworkApiEngine().getLibParser().parseJsonToUser(engine.getHomeworkApiEngine().getRequestManager().requestUserByBotKey(key.replace(" ","")));
            if(user==null){
                engine.getTeleApplicationEngine().getTextUtils().sendMessage(command.message().chat().id(), "Fehler, die Anfrage wurde nicht gefunden!");
            }
            TeleHomeworkConnection connection = new TeleHomeworkConnection();
            connection.setChatId(command.message().chat().id());
            connection.setClasses(user.getClasses());
            engine.getProperties().hApiTeleConnection.add(connection);
            engine.getTeleApplicationEngine().getTextUtils().sendMessage(command.message().chat().id(), "Der Bot wurde erfolgreich hinzugefügt!");
        } else {
            engine.getTeleApplicationEngine().getTextUtils().sendMessage(command.message().chat().id(), "Fehler, keine Argumente gefunden!");
        }
    }

    @Override
    public String help(Engine engine, String[] args) {
        return null;
    }
}
