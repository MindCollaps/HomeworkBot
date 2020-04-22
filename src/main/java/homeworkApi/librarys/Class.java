package homeworkApi.librarys;

import java.io.Serializable;

public class Class implements Serializable {

    public static final long serialVersionUID = 42L;

    private String name = "";
    private String id = "";

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
