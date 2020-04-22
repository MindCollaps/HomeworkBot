package botApplications.discApplication.commands;

import botApplications.discApplication.librarys.DiscApplicationServer;
import botApplications.discApplication.librarys.DiscHomeworkConnection;
import botApplications.response.Response;
import core.Engine;
import homeworkApi.librarys.Class;
import homeworkApi.librarys.User;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.core.events.message.priv.PrivateMessageReceivedEvent;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.awt.*;
import java.util.ArrayList;

public class DiscCmdAddNotificationChannel implements DicCommand {
    @Override
    public boolean calledServer(String[] args, GuildMessageReceivedEvent event, DiscApplicationServer server, Engine engine) {
        return engine.getDiscApplicationEngine().getUtilityBase().userHasGuildAdminPermission(event.getMember(), event.getGuild(), event.getChannel());
    }

    @Override
    public void actionServer(String[] args, GuildMessageReceivedEvent event, DiscApplicationServer server, Engine engine) {
        /*
        if(args.length==0){
            engine.getTeleApplicationEngine().getTextUtils().sendMessage(command.message().chat().id(), "Keine Parameter gefunden! Schreibe /notification help um hilfe zu erhalten!");
            return;
        }
         */
        if(args.length>0){
            switch (args[0]){
                case "add":
                    addNotificationChannel(event, server, engine);
                    break;
                case "remove":
                    removeNotificationChannel(engine, event);
                    break;
                default:
                    engine.getDiscApplicationEngine().getTextUtils().sendError("Keine Parameter gefunden, schreibe -notification help um Hilfe zu erhalten!", event.getChannel(), false);

            }
        } else
            engine.getDiscApplicationEngine().getTextUtils().sendError("Keine Parameter gefunden, schreibe -notification help um Hilfe zu erhalten!", event.getChannel(), false);
    }

    @Override
    public boolean calledPrivate(String[] args, PrivateMessageReceivedEvent event, Engine engine) {
        engine.getDiscApplicationEngine().getTextUtils().sendError("Dieser Befehl ist nur auf Guilds möglich!", event.getChannel(), false);
        return false;
    }

    @Override
    public void actionPrivate(String[] args, PrivateMessageReceivedEvent event, Engine engine) {

    }

    @Override
    public String help(Engine engine) {
        return "notification add - Startet die Authentifizierung um einen neuen Notification Channel zu erstellen\nnotification remove - Entfernt den Notification Channel (nicht den ganzen Channel)";
    }

    @Override
    public void actionTelegram(Member member, Engine engine, String[] args) {

    }

    private void removeNotificationChannel(Engine engine, GuildMessageReceivedEvent event){
        for (DiscHomeworkConnection con:engine.getProperties().hApiDiscConnection){
            if(con.getChannelId().equals(event.getChannel().getId())){
                engine.getDiscApplicationEngine().getTextUtils().sendSucces("Der Bot wurde entfernt!", event.getChannel());
                engine.getProperties().hApiDiscConnection.remove(con);
                return;
            }
        }
        engine.getDiscApplicationEngine().getTextUtils().sendError("Der Channel in dem du dich befindest ist kein Notification Channel!", event.getChannel(), false);
    }

    private void addNotificationChannel(GuildMessageReceivedEvent event, DiscApplicationServer server, Engine engine){
        ArrayList<DiscHomeworkConnection> allConnections = engine.getProperties().hApiDiscConnection;
        for (DiscHomeworkConnection con : allConnections) {
            if (con.getGuildId().equals(event.getGuild().getId())) {
                engine.getDiscApplicationEngine().getTextUtils().sendError("Der Bot ist bereits auf diesem Guild als Messengerbot tätig!", event.getChannel(), false);
                return;
            }
        }

        engine.getDiscApplicationEngine().getTextUtils().sendCustomMessage("Bitte geben sie ihre im System hinterlegte E-Mail an!", event.getChannel(), "Verifizierung", Color.MAGENTA);
        Response emailResponse = new Response(Response.ResponseTyp.Discord) {
            @Override
            public void respondDisc(GuildMessageReceivedEvent respondingEvent) {
                String email = respondingEvent.getMessage().getContentRaw();
                JSONArray users = engine.getHomeworkApiEngine().getRequestManager().requestUserByEmail(email);
                if (users == null) {
                    engine.getDiscApplicationEngine().getTextUtils().sendError("Die angegebene Email-Addresse wurde nicht gefunden!", respondingEvent.getChannel(), false);
                    return;
                } else if (users.size() == 0) {
                    engine.getDiscApplicationEngine().getTextUtils().sendError("Die angegebene Email-Addresse wurde nicht gefunden!", respondingEvent.getChannel(), false);
                    return;
                }
                if (users.size() > 1) {
                    engine.getUtilityBase().printOutput("....something weird happened...we have more than one Person using " + respondingEvent.getMessage().getContentRaw() + " as there email", false);
                }
                User user = engine.getHomeworkApiEngine().getLibParser().parseJsonToUser((JSONObject) users.get(0));
                ArrayList<Class> arrClasses = user.getClasses();
                if (user.getClasses().size() > 0) {
                    String classes = arrClasses.get(0).getName();
                    for (int i = 1; i < user.getClasses().size(); i++) {
                        classes = classes + ", " + arrClasses.get(i).getName();
                    }
                    engine.getDiscApplicationEngine().getTextUtils().sendCustomMessage("Welche Klassen sollen diesem Channel hinzugefügt werden?\n\nZugewiesene Klassen: `" + classes + "`\n\nFür mehrere Klassen bitte mit einem Komma trennen!\nWenn sie alle Klassen auswählen möchten, schreiben sie \"all\"", respondingEvent.getChannel(), "Klassen", Color.MAGENTA);
                    String finalClasses = classes;
                    String finalClasses1 = classes;
                    Response classResponse = new Response(ResponseTyp.Discord) {
                        @Override
                        public void respondDisc(GuildMessageReceivedEvent respondingEvent) {
                            String classIds = respondingEvent.getMessage().getContentRaw().replace(" ", "");
                            ArrayList<Class> classIdsArray = new ArrayList<>();
                            if (classIds.equalsIgnoreCase("all")) {
                                classIdsArray = arrClasses;
                            } else {
                                classIdsArray = classNameToClassId(arrClasses, classIds.split(","));
                            }
                            for (Class aClass : classIdsArray) {
                                if (!finalClasses1.contains(aClass.getName())) {
                                    engine.getDiscEngine().getTextUtils().sendError("Sie haben eine Klasse angegeben, auf die sie keinen Zugirff haben!", event.getChannel(), false);
                                    return;
                                }
                            }
                            DiscHomeworkConnection connection = new DiscHomeworkConnection();
                            connection.setChannelId(respondingEvent.getChannel().getId());
                            connection.setGuildId(respondingEvent.getGuild().getId());
                            connection.setClasses(classIdsArray);
                            engine.getDiscApplicationEngine().getTextUtils().sendCustomMessage("Um ihren Account zu verifizieren, müssen sie den Bot Key der unter https://zgk.mxis.ch/account/ angezeigt wird, eingeben!", respondingEvent.getChannel(), "Verifizierung", Color.MAGENTA);
                            String verifyCode = engine.getHomeworkApiEngine().getRequestManager().requestVerificationCodeByEmail(email);
                            //makeVerifyResponse(verifyCode, engine, command, connection, 2);
                            Response verifyResponse = new Response(ResponseTyp.Discord) {
                                @Override
                                public void respondDisc(GuildMessageReceivedEvent respondingEvent) {
                                    if (respondingEvent.getMessage().getContentRaw().replace(" ", "").equals(verifyCode)) {
                                        engine.getProperties().hApiDiscConnection.add(connection);
                                        engine.getDiscApplicationEngine().getTextUtils().sendSucces("Der Bot wurde erfolgreich hinzugefügt!", respondingEvent.getChannel());
                                    } else {
                                        engine.getDiscApplicationEngine().getTextUtils().sendError("Der Code ist falsch!", respondingEvent.getChannel(), false);
                                    }
                                }
                            };
                            verifyResponse.discUserId = respondingEvent.getAuthor().getId();
                            verifyResponse.discChannelId = respondingEvent.getChannel().getId();
                            verifyResponse.discGuildId = respondingEvent.getGuild().getId();
                            engine.getResponseHandler().makeResponse(verifyResponse);
                        }
                    };
                    classResponse.discUserId = respondingEvent.getAuthor().getId();
                    classResponse.discChannelId = respondingEvent.getChannel().getId();
                    classResponse.discGuildId = respondingEvent.getGuild().getId();
                    engine.getResponseHandler().makeResponse(classResponse);
                } else {
                    engine.getDiscApplicationEngine().getTextUtils().sendError("Sie sind keiner Klasse zugewiesen!", event.getChannel(), false);
                }
            }
        };
        emailResponse.discUserId = event.getAuthor().getId();
        emailResponse.discChannelId = event.getChannel().getId();
        emailResponse.discGuildId = event.getGuild().getId();
        engine.getResponseHandler().makeResponse(emailResponse);
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
