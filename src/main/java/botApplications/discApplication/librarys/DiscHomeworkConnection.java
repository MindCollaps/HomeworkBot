package botApplications.discApplication.librarys;

import homeworkApi.librarys.Class;

import java.io.Serializable;
import java.util.ArrayList;

public class DiscHomeworkConnection implements Serializable {

    public static final long serialVersionUID = 42L;

    ArrayList<Class> classes;
    String guildId;
    String channelId;

    public ArrayList<Class> getClasses() {
        return classes;
    }

    public void setClasses(ArrayList<Class> classes) {
        this.classes = classes;
    }

    public String getGuildId() {
        return guildId;
    }

    public void setGuildId(String guildId) {
        this.guildId = guildId;
    }

    public String getChannelId() {
        return channelId;
    }

    public void setChannelId(String channelId) {
        this.channelId = channelId;
    }
}
