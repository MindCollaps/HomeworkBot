package botRequestApi.api;

import botRequestApi.netConnect.NetConnectCore;
import botRequestApi.netConnect.ServerThread.Action;
import botRequestApi.netConnect.ServerThread.ClientMessageHandlerThread;
import core.Engine;
import org.json.simple.JSONObject;

import java.io.PrintWriter;

public class BotRequestApi {

    private final String consMsgDef = "[Request API]";

    private Engine engine;

    private NetConnectCore netConnectCore;

    public BotRequestApi(Engine engine) {
        this.engine = engine;
    }

    public void boot(boolean overridePort){
        engine.getUtilityBase().printOutput(consMsgDef + " !reading environment variable api port!", true);
        String sysEnvStr = System.getenv("HWBOT_PORT");
        if((!sysEnvStr.equals("")&&sysEnvStr != null)||overridePort){
            Action action = new Action() {
                @Override
                public void action(String req, PrintWriter res) {
                    String rec = engine.getBotRequestApi().handleRec(req);
                    if (rec != null && !rec.equals(""))
                        res.println(rec);
                }
            };
            netConnectCore = new NetConnectCore(engine);
            if(overridePort){
                engine.getUtilityBase().printOutput(consMsgDef + " !Override port is active -> using port 5000 for bot api!", false);
                netConnectCore.setPortNumber(Integer.valueOf(5000));
            } else {
                netConnectCore.setPortNumber(Integer.valueOf(sysEnvStr));
            }
            netConnectCore.setAction(action);
            netConnectCore.startServer();
            engine.getUtilityBase().printOutput(consMsgDef + " !API Started!", false);
        } else {
            engine.getUtilityBase().printOutput(consMsgDef + "!!!Failed to read environment variable -> Failed to start API!!!", false);
        }
    }

    public String handleRec(String instructions){
        engine.getUtilityBase().printOutput(consMsgDef + " !Receded Api instructions!", true);
        JSONObject msg;
        try {
             msg = engine.getFileUtils().convertStringToJson(instructions);
        } catch (Exception e){
            engine.getUtilityBase().printOutput(consMsgDef + " !!!Api instructions was invalid (can't convert to JSON) -> abort!!!", true);
            return null;
        }
        return engine.getFileUtils().convertJsonToString(engine.getConsoleCommandHandler().handleApiCommand(msg));
    }
}
