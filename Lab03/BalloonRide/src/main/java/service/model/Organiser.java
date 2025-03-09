package service.model;

public class Organiser {
    private int id;
    private String name;

    public Organiser(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public Organiser(String name) {
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setId(int id) {
        this.id = id;
    }
}
