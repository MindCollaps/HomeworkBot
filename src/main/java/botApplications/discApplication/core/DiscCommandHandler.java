package botApplications.discApplication.core;

import botApplications.discApplication.commands.DicCommand;

import java.util.ArrayList;
import java.util.HashMap;

public class DiscCommandHandler {

    public HashMap<String, DicCommand> commands = new HashMap<>();
    public ArrayList<String> commandIvokes = new ArrayList<>();

    public void handleServerCommand(DiscCommandParser.ServerCommandContainer cmd) {

        if (commands.containsKey(cmd.invoke)) {

            boolean exe = commands.get(cmd.invoke).calledServer(cmd.args, cmd.event, cmd.server, cmd.engine);

            if (exe) {
                String args0 = "";
                try {
                    args0 = cmd.args[0];
                } catch (Exception ignored) {
                }
                if (args0.equalsIgnoreCase("help")) {
                    cmd.engine.getDiscEngine().getTextUtils().sendHelp(commands.get(cmd.invoke).help(cmd.engine), cmd.event.getChannel());
                } else {
                    commands.get(cmd.invoke).actionServer(cmd.args, cmd.event, cmd.server, cmd.engine);
                }
            }
        }
    }

    public void handlePrivateCommand(DiscCommandParser.ClientCommandContainer cmd) {

        if (commands.containsKey(cmd.invoke)) {

            boolean exe = commands.get(cmd.invoke).calledPrivate(cmd.args, cmd.event, cmd.engine);

            if (exe) {
                String args0 = "";
                try {
                    args0 = cmd.args[0];
                } catch (Exception ignored) {
                }

                if (args0.equalsIgnoreCase("help")) {
                    cmd.engine.getDiscEngine().getTextUtils().sendHelp(commands.get(cmd.invoke).help(cmd.engine), cmd.event.getChannel());
                } else {
                    commands.get(cmd.invoke).actionPrivate(cmd.args, cmd.event, cmd.engine);
                }
            }
        }
    }

    public void createNewCommand(String ivoke, DicCommand cmd) {
        commands.put(ivoke, cmd);
        commandIvokes.add(ivoke);
    }
}
