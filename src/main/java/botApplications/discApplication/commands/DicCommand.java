package botApplications.discApplication.commands;

import botApplications.discApplication.librarys.DiscApplicationServer;
import core.Engine;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.core.events.message.priv.PrivateMessageReceivedEvent;

public interface DicCommand {

    boolean calledServer(String[] args, GuildMessageReceivedEvent event, DiscApplicationServer server, Engine engine);
    void actionServer(String[] args, GuildMessageReceivedEvent event, DiscApplicationServer server, Engine engine);
    boolean calledPrivate(String[] args, PrivateMessageReceivedEvent event, Engine engine);
    void actionPrivate(String[] args, PrivateMessageReceivedEvent event, Engine engine);
    String help(Engine engine);
    void actionTelegram(Member member, Engine engine, String[] args);
}
