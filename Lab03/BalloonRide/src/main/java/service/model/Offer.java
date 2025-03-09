package service.model;

public class Offer {
    private int id;
    private String title;

    public Offer(int id, String title) {
        this.id = id;
        this.title = title;
    }

    public Offer(String title) {
        this.title = title;
    }

    public int getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public void setId(int id) {
        this.id = id;
    }
}
