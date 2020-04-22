package homeworkApi.librarys;

import java.util.ArrayList;
import java.util.Date;

public class Task {

    private String id;
    private String text;
    private String subject;
    private Date submissionDate;
    private ArrayList<HomeworkFile> homeworkFiles = new ArrayList<>();
    private String userId;
    private Date creationDate;
    private ArrayList<Class> classes = new ArrayList<>();

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public Date getSubmissionDate() {
        return submissionDate;
    }

    public void setSubmissionDate(Date submissionDate) {
        this.submissionDate = submissionDate;
    }

    public ArrayList<HomeworkFile> getHomeworkFiles() {
        return homeworkFiles;
    }

    public void setHomeworkFiles(ArrayList<HomeworkFile> homeworkFiles) {
        this.homeworkFiles = homeworkFiles;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public Date getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(Date creationDate) {
        this.creationDate = creationDate;
    }

    public ArrayList<Class> getClasses() {
        return classes;
    }

    public void setClasses(ArrayList<Class> classes) {
        this.classes = classes;
    }
}