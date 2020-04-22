package botApplications.telApplication.commands;

import botApplications.response.Response;
import botApplications.telApplication.librarys.TeleHomeworkConnection;
import com.pengrad.telegrambot.model.Chat;
import com.pengrad.telegrambot.model.Update;
import core.Engine;
import homeworkApi.librarys.Class;
import homeworkApi.librarys.User;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.util.ArrayList;

public class TeleCmdAddNotificationChannel implements TeleCommand {
    @Override
    public boolean called(Update command, Engine engine, String[] args) {
        if (!command.message().chat().type().equals(Chat.Type.group) && !command.message().chat().type().equals(Chat.Type.supergroup) && !command.message().chat().type().equals(Chat.Type.channel)) {
            engine.getTeleApplicationEngine().getTextUtils().sendMessage(command.message().chat().id(), "Diese Befehl ist nur in Gruppen oder Channels möglich!");
            return false;
        } else
            return true;
    }

    @Override
    public void action(Update command, Engine engine, String[] args) {
        /*
        if(args.length==0){
            engine.getTeleApplicationEngine().getTextUtils().sendMessage(command.message().chat().id(), "Keine Parameter gefunden! Schreibe /notification help um hilfe zu erhalten!");
            return;
        }
         */
        ArrayList<TeleHomeworkConnection> allConnections = engine.getProperties().hApiTeleConnection;
        for (TeleHomeworkConnection con : allConnections) {
            if (con.getChatId() == command.message().chat().id()) {
                engine.getTeleApplicationEngine().getTextUtils().sendMessage(command.message().chat().id(), "Der Bot ist bereits in dieser Gruppe als Messengerbot tätig!");
                return;
            }
        }

        engine.getTeleApplicationEngine().getTextUtils().sendMessage(command.message().chat().id(), "Bitte geben sie ihre im System hinterlegte E-Mail an!");
        Response emailResponse = new Response(Response.ResponseTyp.Telegram) {
            @Override
            public void respondTele(Update respondingUpdate) {
                String email = engine.getTeleApplicationEngine().getBotUtils().cutNameOutOfString(respondingUpdate.message().text(), true).replace(" ", "");
                JSONArray users = engine.getHomeworkApiEngine().getRequestManager().requestUserByEmail(email);
                if (users == null) {
                    engine.getTeleApplicationEngine().getTextUtils().sendMessage(command.message().chat().id(), "Die angegebene Email-Addresse wurde nicht gefunden!");
                    return;
                } else if (users.size() == 0) {
                    engine.getTeleApplicationEngine().getTextUtils().sendMessage(command.message().chat().id(), "Die angegebene Email-Addresse wurde nicht gefunden!");
                    return;
                }
                if (users.size() > 1) {
                    engine.getUtilityBase().printOutput("....something weird happened...we have more than one Person using " + respondingUpdate.message().text() + " as there email", false);
                }
                User user = engine.getHomeworkApiEngine().getLibParser().parseJsonToUser((JSONObject) users.get(0));
                ArrayList<Class> arrClasses = user.getClasses();
                if (user.getClasses().size() > 0) {
                    String classes = arrClasses.get(0).getName();
                    for (int i = 1; i < user.getClasses().size(); i++) {
                        classes = classes + ", " + arrClasses.get(i).getName();
                    }
                    engine.getTeleApplicationEngine().getTextUtils().sendMessage(command.message().chat().id(), "Welche Klassen sollen diesem Channel hinzugefügt werden?\n\nZugewiesene Klassen: " + classes + "\n\nFür mehrere Klassen bitte mit einem Komma trennen!\nWenn sie alle Klassen auswählen möchten, schreiben sie \"all\"");
                    String finalClasses = classes;
                    String finalClasses1 = classes;
                    Response classResponse = new Response(Response.ResponseTyp.Telegram) {
                        @Override
                        public void respondTele(Update respondingUpdate) {
                            String classIds = engine.getTeleApplicationEngine().getBotUtils().cutNameOutOfString(respondingUpdate.message().text().replace(" ", ""), true);
                            ArrayList<Class> classIdsArray = new ArrayList<>();
                            if (classIds.equalsIgnoreCase("all")) {
                                classIdsArray = arrClasses;
                            } else {
                                classIdsArray = classNameToClassId(arrClasses, classIds.split(","));
                            }
                            for (Class aClass : classIdsArray) {
                                if (!finalClasses1.contains(aClass.getName())) {
                                    engine.getTeleApplicationEngine().getTextUtils().sendMessage(command.message().chat().id(), "Sie haben eine Klasse angegeben, auf die sie keinen Zugirff haben!");
                                    return;
                                }
                            }
                            TeleHomeworkConnection connection = new TeleHomeworkConnection();
                            connection.setChatId(command.message().chat().id());
                            connection.setClasses(classIdsArray);
                            engine.getTeleApplicationEngine().getTextUtils().sendMessage(command.message().chat().id(), "Um ihren Account zu verifizieren, müssen sie den Bot Key der unter https://zgk.mxis.ch/account/ angezeigt wird, eingeben!");
                            String verifyCode = engine.getHomeworkApiEngine().getRequestManager().requestVerificationCodeByEmail(email);
                            //makeVerifyResponse(verifyCode, engine, command, connection, 2);
                            Response verifyResponse = new Response(Response.ResponseTyp.Telegram) {
                                @Override
                                public void respondTele(Update respondingUpdate) {
                                    if (engine.getTeleApplicationEngine().getBotUtils().cutNameOutOfString(respondingUpdate.message().text(), true).replace(" ", "").equals(verifyCode)) {
                                        engine.getProperties().hApiTeleConnection.add(connection);
                                        engine.getTeleApplicationEngine().getTextUtils().sendMessage(respondingUpdate.message().chat().id(), "Der Bot wurde erfolgreich hinzugefügt!");
                                    } else {
                                        engine.getTeleApplicationEngine().getTextUtils().sendMessage(respondingUpdate.message().chat().id(), "Der Code ist falsch!");
                                    }
                                }
                            };
                            verifyResponse.teleResponseUser = command.message().from();
                            verifyResponse.teleResponseChat = command.message().chat();
                            engine.getResponseHandler().makeResponse(verifyResponse);
                        }
                    };
                    classResponse.teleResponseUser = respondingUpdate.message().from();
                    classResponse.teleResponseChat = respondingUpdate.message().chat();
                    engine.getResponseHandler().makeResponse(classResponse);
                } else {
                    engine.getTeleApplicationEngine().getTextUtils().sendMessage(command.message().chat().id(), "Sie sind keiner Klasse zugewiesen!");
                }
            }
        };
        emailResponse.teleResponseChat = command.message().chat();
        emailResponse.teleResponseUser = command.message().from();
        engine.getResponseHandler().makeResponse(emailResponse);
    }

    @Override
    public String help(Engine engine, String[] args) {
        return "Um eine Notivication Gruppe hinzuzufügen ";
    }

    private void makeVerifyResponse(String verifyCode, Engine engine, Update command, TeleHomeworkConnection connection, int tried) {
        if (tried <= 0)
            return;

        tried--;
        int finalTried = tried;
        Response verifyResponse = new Response(Response.ResponseTyp.Telegram) {
            @Override
            public void respondTele(Update respondingUpdate) {
                if (engine.getTeleApplicationEngine().getBotUtils().cutNameOutOfString(respondingUpdate.message().text(), true).equals(verifyCode)) {
                    engine.getProperties().hApiTeleConnection.add(connection);
                    engine.getTeleApplicationEngine().getTextUtils().sendMessage(respondingUpdate.message().chat().id(), "Der Bot wurde hinzugefügt!");
                } else {
                    engine.getTeleApplicationEngine().getTextUtils().sendMessage(respondingUpdate.message().chat().id(), "Der Code ist falsch! Es bleiben(" + finalTried + ") versuche!");
                }
            }
        };
        verifyResponse.teleResponseUser = command.message().from();
        verifyResponse.teleResponseChat = command.message().chat();
        engine.getResponseHandler().makeResponse(verifyResponse);
    }

    private ArrayList<Class> classNameToClassId(ArrayList<Class> classArrayList, String[] classesString){
        ArrayList<Class> arrayList = new ArrayList<>();
        for (String s:classesString) {
            for (Class c:classArrayList) {
                if(s.equalsIgnoreCase(c.getName())){
                    arrayList.add(c);
                    break;
                }
            }
        }
        return arrayList;
    }
}