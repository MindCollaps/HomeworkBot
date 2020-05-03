package botRequestApi.netConnect.ServerThread;

import core.Engine;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class ServerConnectorThread implements Runnable{

    private final String consMsgDef = "[Server Connection Thread]";

    private Engine engine;
    private boolean exit = false;

    private int portNumber;
    private ServerSocket serverSocket;
    private Socket clientSocket;
    private boolean serverIsRunning = false;
    private Action action;

    public ServerConnectorThread(Engine engine, int portNumber, Action action) {
        this.engine = engine;
        this.portNumber = portNumber;
        this.action = action;
    }

    @Override
    public void run() {
        try {
            serverSocket = new ServerSocket(portNumber);
        } catch (IOException e) {
            e.printStackTrace();
            serverIsRunning = false;
            return;
        }
        serverIsRunning = true;
        engine.getUtilityBase().printOutput(consMsgDef + "Started new Server Connector Thread on: " + serverSocket.getInetAddress() + ":" + portNumber, false);
        while (true){
            clientSocket = new Socket();
            try {
                clientSocket = serverSocket.accept();
                clientSocket.setKeepAlive(true);
            } catch (IOException e) {
                e.printStackTrace();
            }
            ClientMessageHandlerThread cst = new ClientMessageHandlerThread(clientSocket, engine, action);
            cst.run();
        }
    }
    
    public static long getId(){
        return Thread.currentThread().getId();
    }
}