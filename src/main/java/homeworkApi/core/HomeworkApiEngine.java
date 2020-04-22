package homeworkApi.core;

import core.Engine;
import homeworkApi.librarys.Task;
import homeworkApi.threads.ReminderThread;
import homeworkApi.threads.UpdateThread;
import org.json.simple.JSONArray;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;

public class HomeworkApiEngine {

    private Engine engine;

    private final String consMsgDef = "[Homework API]";

    private ArrayList<Task> tasks = new ArrayList<Task>();

    private LibParser libParser;
    private RequestManager requestManager;

    private UpdateThread updateThread;
    private ReminderThread reminderThread;

    public HomeworkApiEngine(Engine engine) {
        this.engine = engine;
    }

    public void boot() {
        engine.getUtilityBase().printOutput(consMsgDef + " !start initialized!", false);
        requestManager = new RequestManager(engine);
        updateThread = new UpdateThread(engine);
        reminderThread = new ReminderThread(engine);
        libParser = new LibParser(engine);
        engine.getUtilityBase().printOutput(consMsgDef + " !fetching latest updates!", false);
        JSONArray tasksJson;
        try {
            tasksJson = engine.getHomeworkApiEngine().getRequestManager().requestTasksUpdateFromServer(true);
        } catch (Exception e) {
            engine.getUtilityBase().printOutput(consMsgDef + " !!!Error can't fetching updates - Server maybe not available -> try again soon!!!", false);
            if (engine.getProperties().debug) {
                e.printStackTrace();
            }
            new Thread(new RestartThread()).start();
            return;
        }

        try {
            receiveTaskUpdate(engine.getHomeworkApiEngine().getLibParser().parseJsonToTaskArray(tasksJson));
        } catch (Exception e) {
            engine.getUtilityBase().printOutput(consMsgDef + " !!!Error can't fetching updates -> stopped bot!!!", false);
            if (engine.getProperties().debug) {
                e.printStackTrace();
            }
            engine.getTeleApplicationEngine().shutdown();
            engine.getDiscEngine().shutdownBotApplication();
            return;
        }

        engine.getUtilityBase().printOutput(consMsgDef + " !fetching latest done!", false);

        engine.getUtilityBase().printOutput(consMsgDef + " !starting update Threads!", false);
        new Thread(updateThread).start();
        new Thread(reminderThread).start();
    }

    private class RestartThread implements Runnable {

        @Override
        public void run() {
            engine.getUtilityBase().printOutput(consMsgDef + " !Started restart Thread -> retry in a few seconds!", false);
            try {
                Thread.sleep(1000 * 10);
            } catch (InterruptedException e) {
                if (engine.getProperties().debug) {
                    e.printStackTrace();
                }
            }
            engine.getUtilityBase().printOutput(consMsgDef + " !RETRY!", false);
            boot();
        }
    }

    public void shutdown() {
        updateThread.stop();
        reminderThread.stop();
    }

    public void receiveTaskUpdate(ArrayList<Task> update) {
        if (update == null) {
            engine.getUtilityBase().printOutput(consMsgDef + "!!!Cant make update - update was empty", true);
            return;
        }
        if (update.size() == 0) {
            engine.getUtilityBase().printOutput(consMsgDef + "!!!Cant make update - update was empty!!!", true);
            return;
        }
        engine.getUtilityBase().printOutput(consMsgDef + " !received Update!", true);
        tasks = update;
        Collections.sort(tasks, new SortDate());
        if (tasks.size() == 0) {
            engine.getUtilityBase().printOutput(consMsgDef + " !Update was empty!", true);
            return;
        }
        if (!tasks.get(tasks.size() - 1).getId().equals(engine.getProperties().hApiLatestPostedTaskId)) {
            if (engine.getProperties().hApiLatestPostedTaskDate == null) {
                for (Task task : tasks) {
                    engine.getUtilityBase().printOutput(consMsgDef + " !Making task announcement - there was no latest posted!", true);
                    makeNewTaskAnnouncement(task,true);
                }
            }
            engine.getUtilityBase().printOutput(consMsgDef + " !Searching for latest update!", true);
            boolean foundLast = false;
            for (int i = 0; i < tasks.size(); i++) {
                if (foundLast) {
                    makeNewTaskAnnouncement(tasks.get(i),true);
                } else {
                    if (engine.getProperties().hApiLatestPostedTaskDate == tasks.get(i).getCreationDate()) {
                        foundLast = true;
                        continue;
                    }
                    if (engine.getProperties().hApiLatestPostedTaskDate != null)
                        foundLast = engine.getProperties().hApiLatestPostedTaskDate.equals(tasks.get(i).getCreationDate());
                    else foundLast = true;

                    if (foundLast)
                        engine.getUtilityBase().printOutput(consMsgDef + " !Found latest task update - ID: " + tasks.get(i).getId() + "!", true);
                }
            }
        }
    }

    public void makeNewTaskAnnouncement(Task task, boolean overrideDate) {
        if (overrideDate)
            engine.getProperties().hApiLatestPostedTaskDate = task.getCreationDate();
        engine.getDiscEngine().makeNewTaskAnnouncement(task);
        engine.getTeleApplicationEngine().makeNewTaskAnnouncement(task);
    }

    public void makeReminderAnnouncement(Task task) {
        engine.getProperties().hApiLatestPostedTaskRemindDate = task.getSubmissionDate();
        engine.getDiscEngine().makeReminderTaskAnnouncement(task);
        engine.getTeleApplicationEngine().makeReminderTaskAnnouncement(task);
    }

    public void lookForReminder() {
        for (Task task : tasks) {
            if (isDateTomorrow(task.getSubmissionDate())) {
                if (engine.getProperties().hApiLatestPostedTaskRemindDate == null) {
                    makeReminderAnnouncement(task);
                } else if (!isDayEqual(task.getSubmissionDate(), engine.getProperties().hApiLatestPostedTaskRemindDate)) {
                    makeReminderAnnouncement(task);
                }
            }
        }
    }

    private boolean isDateTomorrow(Date date) {
        Date today = new Date();
        return makeDayInt(date) == makeDayInt(today) + 1;
    }

    private boolean isDayEqual(Date date1, Date date2) {
        return makeDayInt(date1) == makeDayInt(date2);
    }

    private int makeDayInt(Date date) {
        String year = String.valueOf(date.getYear() + 1900);
        String month = String.valueOf(date.getMonth() + 1);
        String day = String.valueOf(date.getDate());
        String ds = year + month + day;
        return Integer.valueOf(ds);
    }

    private class SortDate implements Comparator<Task> {

        @Override
        public int compare(Task o1, Task o2) {
            return o1.getCreationDate().compareTo(o2.getCreationDate());
        }
    }

    public LibParser getLibParser() {
        return libParser;
    }

    public RequestManager getRequestManager() {
        return requestManager;
    }
}