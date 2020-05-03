package botRequestApi.netConnect;

import botRequestApi.netConnect.ServerThread.Action;
import botRequestApi.netConnect.ServerThread.ClientMessageHandlerThread;
import botRequestApi.netConnect.ServerThread.ServerConnectorThread;
import core.Engine;

import java.util.HashMap;

public class NetConnectCore {

    private Engine engine;
    private int portNumber;
    private String hostName;
    private Action action;

    private static HashMap<Long, ServerConnectorThread> servers = new HashMap<Long, ServerConnectorThread>();

    public NetConnectCore(Engine engine) {
        this.engine = engine;
    }

    public long startServer(){
        ServerConnectorThread sct = new ServerConnectorThread(engine, portNumber, action);
        new Thread(sct).start();
        servers.put(sct.getId(), sct);
        return sct.getId();
    }

    public long startServer(int portNumber){
        ServerConnectorThread sct = new ServerConnectorThread(engine, portNumber, action);
        new Thread(sct).start();
        servers.put(sct.getId(), sct);
        portNumber = portNumber;
        return sct.getId();
    }

    public ServerConnectorThread getServerById(long id) throws Exception{
        try {
            return servers.get(id);
        } catch (Exception e){
            throw new  Exception("server cant found by ID!");
        }
    }

    public int getPortNumber() {
        return portNumber;
    }

    public void setPortNumber(int portNumber) {
        portNumber = portNumber;
    }

    public void setAction(Action action) {
        this.action = action;
    }

    public String getHostName() {
        return hostName;
    }

    public void setHostName(String hostName) {
        hostName = hostName;
    }
}