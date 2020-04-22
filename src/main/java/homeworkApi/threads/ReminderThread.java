package homeworkApi.threads;

        import core.Engine;

public class ReminderThread implements Runnable {

    private final String consMsgDef = "[Reminder Updater]";
    private Engine engine;

    boolean stop = false;

    public ReminderThread(Engine engine) {
        this.engine = engine;
    }

    @Override
    public void run() {
        while (true){
            if(stop){
                break;
            }
            try {
                Thread.sleep(60000*engine.getProperties().hApiReminderUpdateSpeed);
            } catch (InterruptedException e) {
                if(engine.getProperties().debug){e.printStackTrace();}
            }
            if(stop){
                break;
            }
            engine.getUtilityBase().printOutput(consMsgDef + "!Making new update!", true);
            engine.getHomeworkApiEngine().lookForReminder();
            engine.getUtilityBase().printOutput(consMsgDef + "!Making new update done!", true);
        }
    }

    public void stop(){
        stop = true;
    }
}
