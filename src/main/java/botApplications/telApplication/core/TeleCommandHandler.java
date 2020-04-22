package botApplications.telApplication.core;

import botApplications.telApplication.commands.TeleCommand;

import java.util.ArrayList;
import java.util.HashMap;

public class TeleCommandHandler {

    public HashMap<String, TeleCommand> commands = new HashMap<>();
    public ArrayList<String> commandIvokes = new ArrayList<>();

    public void handleCommand(TeleCommandParser.CommandContainer cmd){
        if (commands.containsKey(cmd.invoke)) {

            boolean exe = commands.get(cmd.invoke).called(cmd.update,cmd.engine, cmd.args);

            if (exe) {
                String args0 = "";
                try {
                    args0 = cmd.args[0];
                } catch (Exception ignored) {
                }
                if (args0.equalsIgnoreCase("help")) {
                    cmd.engine.getTeleApplicationEngine().getTextUtils().sendMessage(cmd.update.message().chat().id(), commands.get(cmd.invoke).help(cmd.engine, cmd.args));
                } else {
                    commands.get(cmd.invoke).action(cmd.update, cmd.engine, cmd.args);
                }
            }
        }
    }

    public void createNewCommand(String ivoke, TeleCommand cmd) {
        commands.put(ivoke, cmd);
        commandIvokes.add(ivoke);
    }
}
