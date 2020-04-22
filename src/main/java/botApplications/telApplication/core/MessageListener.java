package botApplications.telApplication.core;

import com.pengrad.telegrambot.UpdatesListener;
import com.pengrad.telegrambot.model.Update;
import core.Engine;

import java.util.List;

public class MessageListener implements UpdatesListener {

    private Engine engine;
    private boolean firstTime = true;

    public MessageListener(Engine engine) {
        this.engine = engine;
    }

    @Override
    public int process(List<Update> list) {
        Update current = list.get(0);
        new Thread(new CommandDispatcherThread(current)).start();
        if(firstTime){
            firstTime = false;
            return UpdatesListener.CONFIRMED_UPDATES_ALL;
        }
        return list.get(0).updateId();
    }

    private void sendCommand(Update update) {
        engine.getTeleApplicationEngine().getBotUtils().sendCommand(update);
    }

    private class CommandDispatcherThread implements Runnable {

        private Update update;

        public CommandDispatcherThread(Update update) {
            this.update = update;
        }

        @Override
        public void run() {
            if (update.message().text() != null) {
                if (update.message().text().startsWith("/")) {
                    if (update.message().text().length() < 2) {
                        engine.getTeleApplicationEngine().getTextUtils().sendMessage(update.message().chat().id(), "...thats not much tbh");
                    } else {
                        if (!engine.getResponseHandler().lookForResponse(update))
                            sendCommand(update);
                    }
                }
            }
        }
    }
}
