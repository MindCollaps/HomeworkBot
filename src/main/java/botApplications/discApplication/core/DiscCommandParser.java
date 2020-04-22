package botApplications.discApplication.core;

import botApplications.discApplication.librarys.DiscApplicationServer;
import core.Engine;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.core.events.message.priv.PrivateMessageReceivedEvent;

import java.util.ArrayList;

public class DiscCommandParser {

    public ServerCommandContainer parseServerMessage(String raw, GuildMessageReceivedEvent event, DiscApplicationServer server, Engine engine) {

        String beheaded = raw.replaceFirst(engine.getProperties().discBotApplicationPrefix, "");
        String[] splitBeheaded = beheaded.split(" ");
        String invoke = splitBeheaded[0];
        ArrayList<String> split = new ArrayList<>();
        for (String s : splitBeheaded) {
            split.add(s);
        }

        String[] args = new String[split.size() - 1];
        split.subList(1, split.size()).toArray(args);

        return new ServerCommandContainer(raw, beheaded, splitBeheaded, invoke, args, event, server, engine);
    }

    public ClientCommandContainer parseClientMessage(String raw, PrivateMessageReceivedEvent event, Engine engine) {

        String beheaded = raw.replaceFirst(engine.getProperties().discBotApplicationPrefix, "");
        String[] splitBeheaded = beheaded.split(" ");
        String invoke = splitBeheaded[0];
        ArrayList<String> split = new ArrayList<>();
        for (String s : splitBeheaded) {
            split.add(s);
        }

        String[] args = new String[split.size() - 1];
        split.subList(1, split.size()).toArray(args);

        return new ClientCommandContainer(raw, beheaded, splitBeheaded, invoke, args, event, engine);
    }


    public class ServerCommandContainer {

        public final String raw;
        public final String beheaded;
        public final String[] splitBeheaded;
        public final String invoke;
        public final String[] args;
        public final GuildMessageReceivedEvent event;
        public final DiscApplicationServer server;
        public final Engine engine;

        public ServerCommandContainer(String rw, String beheaded, String[] splitBeheaded, String invoke, String[] args, GuildMessageReceivedEvent event, DiscApplicationServer server, Engine engine) {
            this.raw = rw;
            this.beheaded = beheaded;
            this.splitBeheaded = splitBeheaded;
            this.invoke = invoke;
            this.args = args;
            this.event = event;
            this.server = server;
            this.engine = engine;
        }

    }

    public class ClientCommandContainer {
        public final String raw;
        public final String beheaded;
        public final String[] splitBeheaded;
        public final String invoke;
        public final String[] args;
        public final PrivateMessageReceivedEvent event;
        public final Engine engine;

        public ClientCommandContainer(String rw, String beheaded, String[] splitBeheaded, String invoke, String[] args, PrivateMessageReceivedEvent event, Engine engine) {
            this.raw = rw;
            this.beheaded = beheaded;
            this.splitBeheaded = splitBeheaded;
            this.invoke = invoke;
            this.args = args;
            this.event = event;
            this.engine = engine;
        }
    }
}
