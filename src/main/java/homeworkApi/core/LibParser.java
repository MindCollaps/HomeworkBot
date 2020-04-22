package homeworkApi.core;

import core.Engine;
import homeworkApi.librarys.Class;
import homeworkApi.librarys.HomeworkFile;
import homeworkApi.librarys.Task;
import homeworkApi.librarys.User;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class LibParser {

    private Engine engine;

    public LibParser(Engine engine) {
        this.engine = engine;
    }

    public ArrayList<Task> parseJsonToTaskArray(JSONArray tasks){
        if(tasks.size() == 0){
            return null;
        }
        Class cClass;
        ArrayList<Task> taskArrayList = new ArrayList<>();
        Task hTask;
        HomeworkFile hFile;
        JSONObject task;
        JSONObject file;
        JSONObject hClass;
        JSONArray classIds;
        String classId;
        for (int i = 0; i < tasks.size(); i++) {
            task = (JSONObject) tasks.get(i);
            hTask = new Task();

            try {
                hTask.setCreationDate(convertStringToDate((String) task.get("createdAt")));
            } catch (Exception e) {
                if(engine.getProperties().debug){e.printStackTrace();}
            }
            hTask.setId((String) task.get("_id"));
            hTask.setSubject((String) task.get("subject"));
            try {
                hTask.setSubmissionDate(convertStringToDate((String) task.get("deadline")));
            } catch (Exception e) {
                if(engine.getProperties().debug){e.printStackTrace();}
            }
            hTask.setText((String) task.get("text"));
            hTask.setUserId((String) task.get("user_id"));

            hClass = (JSONObject) task.get("class");
            cClass = new Class();
            cClass.setId((String) hClass.get("_id"));
            cClass.setName((String) hClass.get("name"));
            hTask.getClasses().add(cClass);

            file = (JSONObject) task.get("files");
            long fileCount = (long) file.get("count");
            if(fileCount != 0){
                hFile = new HomeworkFile();

                hFile.setFileName((String) file.get("fileName"));
                hFile.setFileUrl((String) file.get("fileUrl"));
                hFile.setType((String) file.get("type"));

                hTask.getHomeworkFiles().add(hFile);
            }

            taskArrayList.add(hTask);
        }
        return taskArrayList;
    }

    public User parseJsonToUser(JSONObject object){
        JSONArray classes = null;
        JSONObject hClass;
        Class cClass;
        User user = new User();
        user.setEmail((String) object.get("email"));
        user.setName((String) object.get("name"));
        try {
            user.setRegisteredAt(convertStringToDate((String) object.get("registeredAt")));
        } catch (Exception e) {
            if(engine.getProperties().debug){e.printStackTrace();}
        }
        user.setUserId((String) object.get("_id"));
        switch ((String) object.get("role")){
            case "lehrer":
                user.setUserType(User.UserType.Teacher);
                break;
            case "user":
                user.setUserType(User.UserType.User);
                break;
            case "admin":
                user.setUserType(User.UserType.Admin);
                break;
        }
        
        try {
            classes = (JSONArray) object.get("classes");   
        } catch (Exception e){
            
        }
        
        if(classes != null){
            if(classes.size()!=0){
                for (int j = 0; j < classes.size(); j++) {
                    hClass = (JSONObject) classes.get(j);
                    cClass = new Class();
                    cClass.setId((String) hClass.get("_id"));
                    cClass.setName((String) hClass.get("name"));
                    user.getClasses().add(cClass);
                }
            }
        }
        return user;
    }

    private Date convertStringToDate(String date) throws Exception{
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSX");
            Date dateD = formatter.parse(date);
            return dateD;
    }
}
