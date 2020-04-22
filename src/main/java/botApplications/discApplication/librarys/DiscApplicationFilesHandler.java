package botApplications.discApplication.librarys;

import core.Engine;
import net.dv8tion.jda.core.entities.Guild;

import java.util.HashMap;

public class DiscApplicationFilesHandler {

    HashMap<String, DiscApplicationServer> servers = new HashMap<>();

    Engine engine;

    public DiscApplicationFilesHandler(Engine engine) {
        this.engine = engine;
    }

    public DiscApplicationServer getServerById(String id) throws Exception {
        DiscApplicationServer server = null;
        if(servers.containsKey(id)){
            server = servers.get(id);
        }
        return server;
    }

    public DiscApplicationServer createNewServer(Guild guild) throws Exception{
        if(servers.containsKey(guild.getId())){
            engine.getUtilityBase().printOutput("Cant create new server because already exist! Id: " + guild.getId() + " name: " + guild.getName(), true);
            throw new Exception("Server already exist");
        }
        DiscApplicationServer server = new DiscApplicationServer(guild);
        servers.put(guild.getId(), server);
        return server;
    }

    public void loadAllBotFiles(){
        engine.getUtilityBase().printOutput("~load all bot files!",true);
        try {
            servers = (HashMap<String, DiscApplicationServer>) engine.getFileUtils().loadObject(engine.getFileUtils().getHome() + "/bot/utilize/servers.server");
        } catch (Exception e) {
            engine.getUtilityBase().printOutput("!!Servers cant load!!",true);
        }

        if(servers==null){
            engine.getUtilityBase().printOutput("!!Recreate Servers data!!",true);
            servers = new HashMap<>();
        }

        engine.getUtilityBase().printOutput("~finished loading bot files",true);
        wellcomeNews();
    }

    public void saveAllBotFiles(){
        engine.getUtilityBase().printOutput("~safe all bot files!",true);
        try {
            engine.getFileUtils().saveObject(engine.getFileUtils().getHome() + "/bot/utilize/servers.server", servers);
        } catch (Exception e) {
            if(engine.getProperties().debug){e.printStackTrace();}
            engine.getUtilityBase().printOutput("ERROR IN SAVE OWO", false);
        }
        engine.getUtilityBase().printOutput("~finished saving all bot files",true);
    }

    public void wellcomeNews(){
        for (DiscApplicationServer s:servers.values()) {
            s.initNewOnes();
        }
    }

    public HashMap<String, DiscApplicationServer> getServers() {
        return servers;
    }
}
