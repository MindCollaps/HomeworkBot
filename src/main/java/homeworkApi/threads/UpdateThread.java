package homeworkApi.threads;

import core.Engine;
import org.json.simple.JSONArray;

import java.util.Date;

public class UpdateThread implements Runnable {

    private Engine engine;

    private final String consMsgDef = "[Homework API Updater]";

    private int updateTime = 0;

    private boolean stop = false;

    public UpdateThread(Engine engine) {
        this.engine = engine;
    }

    @Override
    public void run() {
        //in minutes
        while (true){
            if(stop){
                break;
            }
            makeUpdateTime();
            try {
                Thread.sleep(60000*updateTime);
            } catch (InterruptedException e) {
                if(engine.getProperties().debug){e.printStackTrace();}
            }
            if(stop){
                break;
            }
            engine.getUtilityBase().printOutput(consMsgDef + "!Making new update!", true);
            JSONArray tasksJson = null;
            try {
                tasksJson = engine.getHomeworkApiEngine().getRequestManager().requestTasksUpdateFromServer(false);
            } catch (Exception e) {
                if(engine.getProperties().debug){e.printStackTrace();}
            }
            if(tasksJson == null){
                engine.getUtilityBase().printOutput(consMsgDef + " !Update was empty!", true);
                continue;
            }
            engine.getHomeworkApiEngine().receiveTaskUpdate(engine.getHomeworkApiEngine().getLibParser().parseJsonToTaskArray(tasksJson));
            engine.getUtilityBase().printOutput(consMsgDef + "!Update done!", true);
        }
    }

    private void makeUpdateTime(){
        Date date = new Date();
        if(date.getHours() >= 5&& date.getHours() <= 22){
            updateTime = 5;
        } else {
            updateTime = 30;
        }
    }

    public void stop(){
        stop = true;
    }
}
