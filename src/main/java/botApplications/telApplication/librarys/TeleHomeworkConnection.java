package botApplications.telApplication.librarys;

import homeworkApi.librarys.Class;

import java.io.Serializable;
import java.util.ArrayList;

public class TeleHomeworkConnection implements Serializable {

    public static final long serialVersionUID = 42L;

    private ArrayList<Class> classes = new ArrayList<>();
    private long chatId;

    public ArrayList<Class> getClasses() {
        return classes;
    }

    public void setClasses(ArrayList<Class> classes) {
        this.classes = classes;
    }

    public long getChatId() {
        return chatId;
    }

    public void setChatId(long chatId) {
        this.chatId = chatId;
    }
}
