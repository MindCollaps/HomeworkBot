package core;

import homeworkApi.librarys.Task;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.util.ArrayList;
import java.util.Scanner;

public class ConsoleCommandHandler {

    Engine engine;

    public ConsoleCommandHandler(Engine engine) {
        this.engine = engine;
        new Thread(new SystemInListener()).start();
    }

    public JSONObject handleApiCommand(JSONObject req){
        String request = "";
        JSONObject response = new JSONObject();
        request = (String) req.get("req");
        if(req.containsKey("c1")){
            request = request + " " + req.get("c1");
            if(req.containsKey("c2")){
                request = request + " " + req.get("c2");
            }
        }
        if(handleConsoleCommand(request) == false){
            response.put("status", "400");
            response.put("response", "invalid command");
        } else {
            response.put("status", "200");
            response.put("response", "success");
        }
        return response;
    }

    public boolean handleConsoleCommand(String command) {
        String args0;
        try {
            args0 = command.split(" ")[0];
        } catch (Exception e) {
            return false;
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
                engine.getDiscEngine().startBotApplication();
                engine.getTeleApplicationEngine().startBotApplication();
                engine.getHomeworkApiEngine().boot();
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
                    return false;
                }
                engine.getUtilityBase().printOutput("Setted Telegram token", false);
                break;

            case "telename":
                try {
                    engine.getProperties().telBotApplicationName = command.split(" ")[1];
                } catch (Exception e) {
                    engine.getUtilityBase().printOutput("Invalid!", false);
                    return false;
                }
                engine.getUtilityBase().printOutput("Setted Telegram name", false);
                break;

            case "disctoken":
                try {
                    engine.getProperties().discBotApplicationToken = command.split(" ")[1];
                } catch (Exception e) {
                    engine.getUtilityBase().printOutput("Invalid!", false);
                    return false;
                }
                engine.getUtilityBase().printOutput("Setted Discord token", false);
                break;

            case "savespeed":
                try {
                    engine.getProperties().saveSpeed = Integer.valueOf(command.split(" ")[1]);
                } catch (Exception e) {
                    engine.getUtilityBase().printOutput("Invalid", false);
                    return false;
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
                    return false;
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
                    return false;
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
                    return false;
                }

                try {
                    tasksJson = engine.getHomeworkApiEngine().getRequestManager().requestTasksUpdateFromServer(true);
                } catch (Exception e) {
                    if (engine.getProperties().debug) {
                        e.printStackTrace();
                    }
                    engine.getUtilityBase().printOutput("~Error can't fetching updates", false);
                    return false;
                }
                ArrayList<Task> taskList = engine.getHomeworkApiEngine().getLibParser().parseJsonToTaskArray(tasksJson);
                for (Task task : taskList) {
                    if (key.equals(task.getId())) {
                        engine.getHomeworkApiEngine().makeNewTaskAnnouncement(task,false);
                        engine.getUtilityBase().printOutput("Die Nachricht wurde an die jeweiligen Klassen gesendet!", false);
                        break;
                    }
                }
                engine.getUtilityBase().printOutput("Die Aufgabe wurde nicht gefunden", false);
                return false;

            case "postall":
                String message = extractMessage(command,1);
                engine.getTeleApplicationEngine().sendToAllClasses(convert(message));
                engine.getDiscApplicationEngine().sendToAllClasses(convert(message));
                engine.getUtilityBase().printOutput("Die Nachricht wurde an alle Klassen gesendet!", false);
                break;

            case "postclass":
                key = "";
                try {
                    key = command.split(" ")[1];
                } catch (Exception e) {
                    engine.getUtilityBase().printOutput("Invalid", false);
                    return false;
                }
                message = extractMessage(command,2);
                engine.getDiscApplicationEngine().sendToSpecificClass(convert(message),key);
                engine.getTeleApplicationEngine().sendToSpecificClass(convert(message),key);
                engine.getUtilityBase().printOutput("Die Nachricht wurde an die jeweiligen Klassen gesendet!", false);
                break;

            case "help":
                System.out.println("postclass <class> <text> - Posts message to a specific class\npostall <Text> - Posts a Message to all classes\npost <Id> - Posts a Task to classes\nremindspeed <speed> - changes speed of remind thread (minutes)\nsavespeed <speed> - changes speed of save intervall...lol (minutes)\nfetchUpdate - loads all tasks from server\nupdateremind - looks for reminding updates with current saved values\nload - loads all files (override)\nsave - saves all files\nstartBot - starts the bot...UwU\nstopBot - stops the bot\n<tele/disc>token <token> - sets api token\ntelename <name> - sets Name of the Telegram bot\ndebug - turns on debug mode to see more\nshowtime - shows time at console output");
                break;

            default:
                System.out.println("unknown command! Use \"help\" to help...yourself :D");
                break;
        }
        return true;
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

    private String convert(String s){
        return s.replace("\\n", "\n");
    }
}