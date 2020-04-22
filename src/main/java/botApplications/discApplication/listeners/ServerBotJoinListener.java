package botApplications.discApplication.listeners;

import core.Engine;
import net.dv8tion.jda.core.events.guild.GuildJoinEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;

import java.awt.*;

public class ServerBotJoinListener extends ListenerAdapter {

    private Engine engine;

    public ServerBotJoinListener(Engine engine) {
        this.engine = engine;
    }


    @Override
    public void onGuildJoin(GuildJoinEvent event) {
        try {
            engine.getDiscApplicationEngine().getTextUtils().sendCustomMessage("Hallo, ich bin der ZGK Messaging Bot. Ich informiere über neue Aufgaben und erinnere euch an diese. Um mich einem Channel zuzuweisen, muss in dem Channel -notification add geschreieben werden, um mit der erstellung zu beginnen.", event.getGuild().getTextChannels().get(0), "Hallo", Color.YELLOW);
        } catch (Exception e) {
            e.printStackTrace();
            engine.getUtilityBase().printOutput("[DISCORD - on Guild join] Can't send wellcome Message...idk why", true);
        }
    }
}
