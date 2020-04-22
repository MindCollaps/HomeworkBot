package core;

import homeworkApi.librarys.Task;
import org.json.simple.JSONArray;

import java.util.ArrayList;
import java.util.Scanner;

public class ConsoleCommandHandler {

    Engine engine;

    public ConsoleCommandHandler(Engine engine) {
        this.engine = engine;
        new Thread(new SystemInListener()).start();
    }

    public void handleConsoleCommand(String command) {
        String args0;
        try {
            args0 = command.split(" ")[0];
        } catch (Exception e) {
            return;
        }
        switch (args0.toLowerCase()) {
            case "save":
                engine.saveProperties();
                break;

            case "load":
                engine.loadProperties();
                break;
            case "debug":
                if (engine.getProperties().debug) {
                    engine.getProperties().debug = false;
                } else {
                    engine.getProperties().debug = true;
                }
                System.out.println("Debug is now " + engine.getProperties().debug);
                break;

            case "showtime":
                if (engine.getProperties().showTime) {
                    engine.getProperties().showTime = false;
                } else {
                    engine.getProperties().showTime = true;
                }
                System.out.println("Show time is now " + engine.getProperties().showTime);
                break;

            case "startbot":
                engine.discApplicationEngine.startBotApplication();
                engine.teleApplicationEngine.startBotApplication();
                engine.homeworkApiEngine.boot();
                break;

            case "stopbot":
                engine.getDiscEngine().shutdownBotApplication();
                engine.getTeleApplicationEngine().shutdown();
                break;

            case "teletoken":
                try {
                    engine.getProperties().telBotApplicationToken = command.split(" ")[1];
                } catch (Exception e) {
                    engine.getUtilityBase().printOutput("Invalid!", false);
                    return;
                }
                engine.getUtilityBase().printOutput("Setted Telegram token", false);
                break;

            case "telename":
                try {
                    engine.getProperties().telBotApplicationName = command.split(" ")[1];
                } catch (Exception e) {
                    engine.getUtilityBase().printOutput("Invalid!", false);
                    return;
                }
                engine.getUtilityBase().printOutput("Setted Telegram name", false);
                break;

            case "disctoken":
                try {
                    engine.getProperties().discBotApplicationToken = command.split(" ")[1];
                } catch (Exception e) {
                    engine.getUtilityBase().printOutput("Invalid!", false);
                    return;
                }
                engine.getUtilityBase().printOutput("Setted Discord token", false);
                break;

            case "savespeed":
                try {
                    engine.getProperties().saveSpeed = Integer.valueOf(command.split(" ")[1]);
                } catch (Exception e) {
                    engine.getUtilityBase().printOutput("Invalid", false);
                    break;
                }
                engine.getUtilityBase().printOutput("Changed save speed!", false);
                break;

            case "remindspeed":
                try {
                    engine.getProperties().hApiReminderUpdateSpeed = Integer.valueOf(command.split(" ")[1]);
                } catch (Exception e) {
                    if (engine.getProperties().debug) {
                        e.printStackTrace();
                    }
                    engine.getUtilityBase().printOutput("Invalid", false);
                    break;
                }
                engine.getUtilityBase().printOutput("Changed reminder speed!", false);
                break;

            case "stop":
                engine.shutdown();
                break;

            case "fetchupdate":
                JSONArray tasksJson = null;
                try {
                    tasksJson = engine.getHomeworkApiEngine().getRequestManager().requestTasksUpdateFromServer(true);
                } catch (Exception e) {
                    if (engine.getProperties().debug) {
                        e.printStackTrace();
                    }
                    engine.getUtilityBase().printOutput("~Error can't fetching updates", false);
                    break;
                }
                engine.getHomeworkApiEngine().receiveTaskUpdate(engine.getHomeworkApiEngine().getLibParser().parseJsonToTaskArray(tasksJson));
                engine.getUtilityBase().printOutput("~Fetching done", false);
                System.out.println(tasksJson.toJSONString());
                break;

            case "updateremind":
                engine.getHomeworkApiEngine().lookForReminder();
                engine.getUtilityBase().printOutput("~Update done", false);
                break;

            case "post":
                String key = "";
                try {
                    key = command.split(" ")[1];
                } catch (Exception e) {
                    engine.getUtilityBase().printOutput("Invalid", false);
                    break;
                }

                try {
                    tasksJson = engine.getHomeworkApiEngine().getRequestManager().requestTasksUpdateFromServer(true);
                } catch (Exception e) {
                    if (engine.getProperties().debug) {
                        e.printStackTrace();
                    }
                    engine.getUtilityBase().printOutput("~Error can't fetching updates", false);
                    break;
                }
                ArrayList<Task> taskList = engine.getHomeworkApiEngine().getLibParser().parseJsonToTaskArray(tasksJson);
                for (Task task : taskList) {
                    if (key.equals(task.getId())) {
                        engine.getHomeworkApiEngine().makeNewTaskAnnouncement(task,false);
                        engine.getUtilityBase().printOutput("Die Nachricht wurde an die jeweiligen Klassen gesendet!", false);
                        return;
                    }
                }
                engine.getUtilityBase().printOutput("Die Aufgabe wurde nicht gefunden", false);
                break;

            case "postall":
                String message = extractMessage(command,1);
                engine.getTeleApplicationEngine().sendToAllClasses(message);
                engine.getDiscApplicationEngine().sendToAllClasses(message);
                engine.getUtilityBase().printOutput("Die Nachricht wurde an alle Klassen gesendet!", false);
                break;

            case "postclass":
                key = "";
                try {
                    key = command.split(" ")[1];
                } catch (Exception e) {
                    engine.getUtilityBase().printOutput("Invalid", false);
                    break;
                }
                message = extractMessage(command,2);
                engine.getDiscApplicationEngine().sendToSpecificClass(message,key);
                engine.getTeleApplicationEngine().sendToSpecificClass(message,key);
                engine.getUtilityBase().printOutput("Die Nachricht wurde an die jeweiligen Klassen gesendet!", false);
                break;

            case "help":
                System.out.println("postclass <class> <text> - Posts message to a specific class\npostall <Text> - Posts a Message to all classes\npost <Id> - Posts a Task to classes\nremindspeed <speed> - changes speed of remind thread (minutes)\nsavespeed <speed> - changes speed of save intervall...lol (minutes)\nfetchUpdate - loads all tasks from server\nupdateremind - looks for reminding updates with current saved values\nload - loads all files (override)\nsave - saves all files\nstartBot - starts the bot...UwU\nstopBot - stops the bot\n<tele/disc>token <token> - sets api token\ntelename <name> - sets Name of the Telegram bot\ndebug - turns on debug mode to see more\nshowtime - shows time at console output");
                break;

            default:
                System.out.println("unknown command! Use \"help\" to help...yourself :D");
                break;
        }
    }

    private class SystemInListener implements Runnable {

        @Override
        public void run() {
            String line;
            Scanner scanner = new Scanner(System.in);
            while (true) {
                line = scanner.nextLine();
                handleConsoleCommand(line);
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private String extractMessage(String extractor, int startAt){
        String message = "";
        String[] commandSplit = extractor.split(" ");
        if (commandSplit.length > startAt) {
            message = commandSplit[startAt];
            for (int i = startAt+1; i < commandSplit.length; i++) {
                if(commandSplit[i].endsWith("\\n"))
                    message = message + " " + commandSplit[i].replace("\\n", "") + "\n";
                else
                    message = message + " " + commandSplit[i];
            }
        } else {
            engine.getUtilityBase().printOutput("Invalid amount of characters!", false);
            return null;
        }
        return message;
    }
}