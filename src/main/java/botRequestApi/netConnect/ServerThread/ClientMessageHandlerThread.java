package botRequestApi.netConnect.ServerThread;

import core.Engine;
import org.json.simple.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;

public class ClientMessageHandlerThread implements Runnable {

    private final String consMsgDef = "[Client Connection Thread]";

    private Engine engine;
    private Socket clientSocket;
    private InetAddress clientAdress;
    private long id;
    public int clientsConnectedToServer = 0;
    private Action action;

    public ClientMessageHandlerThread(Socket clientSocket, Engine engine, Action action) {
        this.clientSocket = clientSocket;
        this.engine = engine;
        this.action = action;
    }

    @Override
    public void run() {
        this.id = Thread.currentThread().getId();
        BufferedReader in = null;
        PrintWriter out = null;
        try {
            out = new PrintWriter(clientSocket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
        } catch (Exception e) {
            e.printStackTrace();
        }
        this.clientAdress = clientSocket.getInetAddress();
        clientsConnectedToServer++;
        engine.getUtilityBase().printOutput(consMsgDef + " Successfully connected to client: [" + id + ", " + clientSocket.getInetAddress() + "]", true);
        try {
            String inputLine;
            while ((inputLine = in.readLine()) != null) {
                if (!clientSocket.isConnected()) {
                    engine.getUtilityBase().printOutput(consMsgDef + " Lost connection to: [" + id + ", " + clientSocket.getInetAddress() + "]", true);
                    return;
                }
                engine.getUtilityBase().printOutput(consMsgDef + "[" + id + ", " + clientAdress + "] Client: " + inputLine, true);
                action.action(inputLine, out);
            }
        } catch (IOException e) {
            engine.getUtilityBase().printOutput(consMsgDef + " Lost connection to: [" + id + ", " + clientSocket.getInetAddress() + "]", true);
            clientsConnectedToServer--;
            return;
        }
        engine.getUtilityBase().printOutput(consMsgDef + " Ended connection to: [" + id + ", " + clientSocket.getInetAddress() + "]", true);
        clientsConnectedToServer--;
    }



    public static long getId() {
        return Thread.currentThread().getId();
    }
}


