package homeworkApi.core;

import core.Engine;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.ProtocolException;
import java.net.URL;

public class RequestManager {

    private final String consMsgDef = "[Request manager]";

    Engine engine;

    private final String apiUrl = "https://zgk.mxis.ch/api/v1";
    private final String apiToken = "V2JndUxGcXJyS0hDb0wwVm44OE0Kd2o5UFQ2U3Y1a0tacTlUejZjVXEKNm9Nejl3";

    public RequestManager(Engine engine) {
        this.engine = engine;
    }

    public void test() {
        System.out.println("Test: " + post("test", "{\"random\": \"ich mag Bananen\"}"));
    }

    /**
     * @param getAll if true - server will look for last update and if there was a new apm it'll return all apms
     */
    public JSONArray requestTasksUpdateFromServer(boolean getAll) throws Exception{
        engine.getUtilityBase().printOutput(consMsgDef + " !Requesting tasks from server, get all : " + getAll + "!", true);
        String path = "get/aufgabe?apiKey=" + apiToken;
        if (!getAll) {
            path = path + "&new=true";
        }
        JSONObject response = engine.getFileUtils().convertStringToJson(get(path));
        if ((long) response.get("status") != 200){
            engine.getUtilityBase().printOutput(consMsgDef + " !!!requested Task status was not 200 -> it was " + response.get("status") + "!!!", true);
            return null;
        }
        JSONArray jsonArray = (JSONArray) response.get("data");
        engine.getUtilityBase().printOutput(consMsgDef + " !Server respond " + jsonArray.size() + " tasks!", true);
        return jsonArray;
    }

    public JSONArray requestUserByEmail(String email) {
        engine.getUtilityBase().printOutput(consMsgDef + " !Requesting user from server by email: " + email + "!", true);
        String path = "get/user?apiKey=" + apiToken + "&email=" + email;
        JSONObject response = engine.getFileUtils().convertStringToJson(get(path));
        if ((long) response.get("status") != 200){
            engine.getUtilityBase().printOutput(consMsgDef + " !!!requested Task status was not 200 -> it was " + response.get("status") + "!!!", true);
            return null;
        }
        engine.getUtilityBase().printOutput(consMsgDef + " !Server found user with email: " + email + "!", true);
        return (JSONArray) response.get("data");
    }

    public JSONObject requestUserByBotKey(String code) {
        engine.getUtilityBase().printOutput(consMsgDef + " !Requesting user from server by bot key: " + code + "!", true);
        String path = "verify?apiKey=" + apiToken + "&token=" + code;
        JSONObject response = engine.getFileUtils().convertStringToJson(get(path));
        if ((long) response.get("status") != 200){
            engine.getUtilityBase().printOutput(consMsgDef + " !!!requested Task status was not 200 -> it was " + response.get("status") + "!!!", true);
            return null;
        }
        engine.getUtilityBase().printOutput(consMsgDef + " !Server found user with bot key: " + code + "!", true);
        return (JSONObject) response.get("data");
    }

    /**
     *
     * @param email verify email
     * @return verify code
     */
    public String requestVerificationCodeByEmail(String email){
        engine.getUtilityBase().printOutput(consMsgDef + " !Requesting verification code for " + email + "!", true);
        String path = "verify?apiKey=" + apiToken + "&email=" + email;
        JSONObject response = engine.getFileUtils().convertStringToJson(get(path));
        if ((long) response.get("status") != 200){
            engine.getUtilityBase().printOutput(consMsgDef + " !!!requested Task status was not 200 -> it was " + response.get("status") + "!!!",true);
            return null;
        }
        JSONObject data = (JSONObject) response.get("data");
        String  code = (String) data.get("code");
        engine.getUtilityBase().printOutput(consMsgDef + " !Server send code " + code + " for " + email + "!", true);
        return code;
    }

    private String get(String path) {
        try {
            return readResponse(makeConnection(path));
        } catch (Exception e) {
            if(engine.getProperties().debug){e.printStackTrace();}
            return null;
        }
    }

    private String post(String path, String json) {
        HttpURLConnection connection;
        try {
            connection = makeConnection(path);
        } catch (Exception e) {
            if(engine.getProperties().debug){e.printStackTrace();}
            return null;
        }
        try {
            connection.setRequestMethod("POST");
        } catch (ProtocolException e) {
            if(engine.getProperties().debug){e.printStackTrace();}
            return null;
        }
        connection.setDoInput(true);
        connection.setDoOutput(true);
        connection.setUseCaches(false);
        try {
            OutputStreamWriter os = new OutputStreamWriter(connection.getOutputStream());
            os.write(json);
            os.flush();
        } catch (IOException e) {
            if(engine.getProperties().debug){e.printStackTrace();}
        }

        return readResponse(connection);
    }

    private String readResponse(HttpURLConnection connection) {
        String responseString = "";
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream(), "utf-8"));
            StringBuilder response = new StringBuilder();
            String responseLine = null;
            while ((responseLine = br.readLine()) != null) {
                response.append(responseLine.trim());
            }
            responseString = response.toString();
        } catch (IOException e) {
            if(engine.getProperties().debug){e.printStackTrace();}
        }
        return responseString;
    }

    private HttpURLConnection makeConnection(String path) throws Exception {
        return (HttpURLConnection) new URL(apiUrl + "/" + path).openConnection();
    }
}